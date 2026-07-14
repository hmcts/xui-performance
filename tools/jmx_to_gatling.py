#!/usr/bin/env python3
"""
Convert Apache JMeter .jmx test plans to Gatling Java DSL simulations.

Usage:
  python3 jmx_to_gatling.py path/to/test.jmx
  python3 jmx_to_gatling.py path/to/test.jmx -o OutSimulation.java
  python3 jmx_to_gatling.py path/to/test.jmx --scala -o OutSimulation.scala
  python3 jmx_to_gatling.py path/to/test.jmx --package com.example.perf

Supported (best-effort):
  - TestPlan / ThreadGroup / SetupThreadGroup
  - HTTP Request Defaults, HTTP Request (GET/POST/PUT/PATCH/DELETE/HEAD)
  - Header Manager, User Defined Variables, CSV Data Set
  - Constant / Uniform Random Timer → pause
  - Regex Extractor, JSON Extractor → checks with saveAs
  - Response Assertion (status / contains)
  - Simple Controllers, Transaction Controllers, Loop Controllers
  - IfController (simple ${var} / boolean expressions → doIf comments)

Not auto-converted (emitted as TODO comments):
  - JSR223 / BeanShell scripts
  - Complex Groovy IfController conditions
  - Auth Manager, Cookie Manager details, JDBC, JMS, etc.
"""

from __future__ import annotations

import argparse
import re
import sys
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any, Iterator, Optional
from urllib.parse import urljoin


# ---------------------------------------------------------------------------
# JMX helpers
# ---------------------------------------------------------------------------

def prop(el: ET.Element, name: str, default: str = "") -> str:
    for child in el:
        if child.get("name") == name:
            return (child.text or "").strip()
    return default


def prop_bool(el: ET.Element, name: str, default: bool = True) -> bool:
    raw = prop(el, name, "")
    if raw == "":
        return default
    return raw.lower() in ("true", "1", "yes")


def is_enabled(el: ET.Element) -> bool:
    return el.get("enabled", "true").lower() != "false"


def testclass(el: ET.Element) -> str:
    return el.get("testclass") or el.get("guiclass") or el.tag


def testname(el: ET.Element) -> str:
    return el.get("testname") or testclass(el)


def children_pairs(hash_tree: ET.Element) -> list[tuple[ET.Element, ET.Element]]:
    """JMeter stores siblings as: element, hashTree, element, hashTree, ..."""
    kids = list(hash_tree)
    pairs: list[tuple[ET.Element, ET.Element]] = []
    i = 0
    while i < len(kids):
        node = kids[i]
        subtree: ET.Element
        if i + 1 < len(kids) and kids[i + 1].tag == "hashTree":
            subtree = kids[i + 1]
            i += 2
        else:
            subtree = ET.Element("hashTree")
            i += 1
        pairs.append((node, subtree))
    return pairs


def jmeter_to_gatling_expr(text: str) -> str:
    """Convert ${var} / ${__P(name,default)} style refs to Gatling #{var}."""
    if not text:
        return text

    def repl_p(m: re.Match[str]) -> str:
        name = m.group(1)
        default = m.group(2) or ""
        # Gatling has no direct property helper; keep name, note default in comment elsewhere
        return "#{%s}" % name if not default else "#{%s}" % name

    text = re.sub(r"\$\{__P\(([^,)]+)(?:,([^)]*))?\)\}", repl_p, text)
    text = re.sub(r"\$\{__property\(([^,)]+)(?:,([^)]*))?\)\}", repl_p, text)
    text = re.sub(r"\$\{([A-Za-z_][A-Za-z0-9_.-]*)\}", r"#{\1}", text)
    return text


def java_string(s: str) -> str:
    return (
        '"'
        + s.replace("\\", "\\\\")
        .replace('"', '\\"')
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        + '"'
    )


def scala_string(s: str) -> str:
    return java_string(s)


def safe_ident(name: str, fallback: str = "Item") -> str:
    cleaned = re.sub(r"[^A-Za-z0-9_]", "_", name.strip())
    cleaned = re.sub(r"_+", "_", cleaned).strip("_")
    if not cleaned:
        cleaned = fallback
    if cleaned[0].isdigit():
        cleaned = "N_" + cleaned
    return cleaned


def safe_class_name(name: str) -> str:
    parts = re.split(r"[^A-Za-z0-9]+", name)
    joined = "".join(p[:1].upper() + p[1:] for p in parts if p)
    if not joined:
        joined = "ConvertedSimulation"
    if joined[0].isdigit():
        joined = "Sim" + joined
    if not joined.endswith("Simulation"):
        joined += "Simulation"
    return joined


# ---------------------------------------------------------------------------
# Model
# ---------------------------------------------------------------------------

@dataclass
class Header:
    name: str
    value: str


@dataclass
class Extractor:
    kind: str  # regex | json
    var_name: str
    expression: str
    match_number: str = "1"
    use_headers: bool = False


@dataclass
class Assertion:
    kind: str  # status | contains | equals
    values: list[str] = field(default_factory=list)


@dataclass
class HttpRequest:
    name: str
    method: str
    protocol: str
    domain: str
    port: str
    path: str
    body: str = ""
    form_params: list[tuple[str, str]] = field(default_factory=list)
    headers: list[Header] = field(default_factory=list)
    extractors: list[Extractor] = field(default_factory=list)
    assertions: list[Assertion] = field(default_factory=list)
    follow_redirects: bool = True


@dataclass
class Pause:
    millis: int
    random_max: Optional[int] = None  # uniform random upper bound


@dataclass
class Comment:
    text: str


@dataclass
class Loop:
    name: str
    times: int
    children: list[Any] = field(default_factory=list)


@dataclass
class Group:
    name: str
    children: list[Any] = field(default_factory=list)


@dataclass
class DoIf:
    name: str
    condition: str
    children: list[Any] = field(default_factory=list)
    unsupported: bool = False


@dataclass
class Scenario:
    name: str
    users: int = 1
    ramp_seconds: int = 0
    loops: int = 1
    duration: int = 0
    steps: list[Any] = field(default_factory=list)
    is_setup: bool = False


@dataclass
class Plan:
    name: str
    base_protocol: str = "https"
    base_domain: str = ""
    base_port: str = ""
    base_path: str = ""
    connect_timeout_ms: Optional[int] = None
    response_timeout_ms: Optional[int] = None
    default_headers: list[Header] = field(default_factory=list)
    variables: dict[str, str] = field(default_factory=dict)
    csv_feeders: list[dict[str, str]] = field(default_factory=list)
    scenarios: list[Scenario] = field(default_factory=list)
    warnings: list[str] = field(default_factory=list)


# ---------------------------------------------------------------------------
# Parser
# ---------------------------------------------------------------------------

class JmxParser:
    def __init__(self) -> None:
        self.plan = Plan(name="Converted")

    def parse(self, path: Path) -> Plan:
        root = ET.parse(path).getroot()
        # Root: jmeterTestPlan > hashTree > TestPlan + hashTree
        outer = root.find("hashTree")
        if outer is None:
            raise ValueError("Invalid JMX: missing root hashTree")

        for node, subtree in children_pairs(outer):
            if testclass(node) == "TestPlan":
                self.plan.name = testname(node) or path.stem
                self._parse_testplan_tree(subtree)
            else:
                # Some exports nest differently; still try
                self._dispatch(node, subtree, None)

        if not self.plan.scenarios:
            self.plan.warnings.append("No ThreadGroup found; generated empty scenario.")
            self.plan.scenarios.append(Scenario(name="Default"))

        return self.plan

    def _parse_testplan_tree(self, tree: ET.Element) -> None:
        for node, subtree in children_pairs(tree):
            self._dispatch(node, subtree, None)

    def _dispatch(self, node: ET.Element, subtree: ET.Element, scenario: Optional[Scenario]) -> None:
        if not is_enabled(node):
            return

        tc = testclass(node)

        if tc in ("ThreadGroup", "SetupThreadGroup", "PostThreadGroup"):
            sc = self._parse_thread_group(node, tc == "SetupThreadGroup")
            self.plan.scenarios.append(sc)
            self._parse_into(subtree, sc)
            return

        if scenario is None:
            # Plan-level config
            if tc == "Arguments":
                self.plan.variables.update(self._parse_arguments(node))
            elif tc == "ConfigTestElement" and "HttpDefaults" in (node.get("guiclass") or ""):
                self._apply_http_defaults(node)
            elif tc == "HeaderManager":
                self.plan.default_headers.extend(self._parse_headers(node))
            elif tc == "CSVDataSet":
                self.plan.csv_feeders.append(self._parse_csv(node))
            elif tc.startswith("JSR223") or tc.startswith("BeanShell"):
                self.plan.warnings.append(f"Skipped script element at plan level: {testname(node)}")
            elif tc in (
                "ResultCollector",
                "kg.apc.jmeter.vizualizers.CorrectedResultCollector",
                "DNSCacheManager",
                "AuthManager",
                "CookieManager",
                "CacheManager",
                "DebugSampler",
                "DebugPostProcessor",
                "TestAction",
            ):
                pass
            else:
                # Recurse into unknown containers
                self._parse_into(subtree, None)
            return

        # Scenario-level
        if tc == "HTTPSamplerProxy":
            req = self._parse_http_sampler(node, subtree)
            scenario.steps.append(req)
        elif tc == "ConfigTestElement" and "HttpDefaults" in (node.get("guiclass") or ""):
            self._apply_http_defaults(node)
        elif tc == "HeaderManager":
            # Apply as default if no requests yet, else attach to next request via scope is hard;
            # JMeter header managers apply to siblings below. We treat as scenario defaults.
            self.plan.default_headers.extend(self._parse_headers(node))
        elif tc == "Arguments":
            self.plan.variables.update(self._parse_arguments(node))
        elif tc == "CSVDataSet":
            self.plan.csv_feeders.append(self._parse_csv(node))
        elif tc == "ConstantTimer":
            delay = int(float(prop(node, "ConstantTimer.delay", "0") or 0))
            scenario.steps.append(Pause(millis=delay))
        elif tc == "UniformRandomTimer":
            delay = int(float(prop(node, "ConstantTimer.delay", "0") or 0))
            rng = int(float(prop(node, "RandomTimer.range", "0") or 0))
            scenario.steps.append(Pause(millis=delay, random_max=delay + rng))
        elif tc == "TransactionController":
            group = Group(name=testname(node))
            self._parse_into(subtree, scenario, into=group.children)
            scenario.steps.append(group)
        elif tc == "GenericController":
            group = Group(name=testname(node))
            self._parse_into(subtree, scenario, into=group.children)
            scenario.steps.append(group)
        elif tc == "LoopController":
            loops_raw = prop(node, "LoopController.loops", "1")
            try:
                times = int(float(loops_raw))
            except ValueError:
                times = 1
            loop = Loop(name=testname(node), times=max(times, 1))
            self._parse_into(subtree, scenario, into=loop.children)
            scenario.steps.append(loop)
        elif tc == "IfController":
            cond = prop(node, "IfController.condition", "")
            do_if = DoIf(name=testname(node), condition=cond)
            # Mark complex groovy / functions as unsupported for codegen
            if "__groovy" in cond or "__jexl" in cond or "JMeterThread" in cond:
                do_if.unsupported = True
                self.plan.warnings.append(
                    f"IfController '{testname(node)}' has a complex condition; emitted as TODO."
                )
            self._parse_into(subtree, scenario, into=do_if.children)
            scenario.steps.append(do_if)
        elif tc.startswith("JSR223") or tc.startswith("BeanShell") or tc == "JSR223Sampler":
            scenario.steps.append(
                Comment(
                    f"TODO: convert JMeter script element '{testname(node)}' ({tc}) manually"
                )
            )
            self.plan.warnings.append(f"Script not converted: {testname(node)}")
            # Still walk children (extractors rarely under scripts)
            self._parse_into(subtree, scenario)
        elif tc in (
            "ResultCollector",
            "kg.apc.jmeter.vizualizers.CorrectedResultCollector",
            "DebugSampler",
            "DebugPostProcessor",
            "TestAction",
            "DNSCacheManager",
            "AuthManager",
            "CookieManager",
            "CacheManager",
            "ResponseAssertion",  # handled as child of sampler
            "RegexExtractor",
            "JSONPostProcessor",
            "JSONPathAssertion",
            "XPath2Extractor",
            "HtmlExtractor",
            "BoundaryExtractor",
            "JSR223PostProcessor",
            "JSR223PreProcessor",
            "JSR223Listener",
        ):
            pass
        else:
            scenario.steps.append(Comment(f"Unsupported element skipped: {tc} ({testname(node)})"))
            self._parse_into(subtree, scenario)

    def _parse_into(
        self,
        tree: ET.Element,
        scenario: Optional[Scenario],
        into: Optional[list[Any]] = None,
    ) -> None:
        # When `into` is provided, temporarily redirect scenario.steps
        if into is not None and scenario is not None:
            original = scenario.steps
            scenario.steps = into
            try:
                for node, subtree in children_pairs(tree):
                    self._dispatch(node, subtree, scenario)
            finally:
                scenario.steps = original
        else:
            for node, subtree in children_pairs(tree):
                self._dispatch(node, subtree, scenario)

    def _parse_thread_group(self, node: ET.Element, is_setup: bool) -> Scenario:
        users = int(float(prop(node, "ThreadGroup.num_threads", "1") or 1))
        ramp = int(float(prop(node, "ThreadGroup.ramp_time", "0") or 0))
        duration = int(float(prop(node, "ThreadGroup.duration", "0") or 0))
        loops = 1
        # Loop controller nested as elementProp
        for child in node.iter():
            if child.get("name") == "LoopController.loops":
                try:
                    loops = int(float((child.text or "1").strip()))
                except ValueError:
                    loops = 1
                break
        return Scenario(
            name=testname(node),
            users=max(users, 1),
            ramp_seconds=max(ramp, 0),
            loops=max(loops, 1),
            duration=max(duration, 0),
            is_setup=is_setup,
        )

    def _apply_http_defaults(self, node: ET.Element) -> None:
        protocol = prop(node, "HTTPSampler.protocol")
        domain = prop(node, "HTTPSampler.domain")
        port = prop(node, "HTTPSampler.port")
        path = prop(node, "HTTPSampler.path")
        if protocol:
            self.plan.base_protocol = protocol
        if domain:
            self.plan.base_domain = domain
        if port and port not in ("0", ""):
            self.plan.base_port = port
        if path:
            self.plan.base_path = path
        ct = prop(node, "HTTPSampler.connect_timeout")
        rt = prop(node, "HTTPSampler.response_timeout")
        if ct:
            try:
                self.plan.connect_timeout_ms = int(ct)
            except ValueError:
                pass
        if rt:
            try:
                self.plan.response_timeout_ms = int(rt)
            except ValueError:
                pass

    def _parse_arguments(self, node: ET.Element) -> dict[str, str]:
        out: dict[str, str] = {}
        for ep in node.iter("elementProp"):
            if ep.get("elementType") != "Argument":
                continue
            name = prop(ep, "Argument.name")
            value = prop(ep, "Argument.value")
            if name:
                out[name] = value
        return out

    def _parse_headers(self, node: ET.Element) -> list[Header]:
        headers: list[Header] = []
        for ep in node.iter("elementProp"):
            if ep.get("elementType") != "Header":
                continue
            name = prop(ep, "Header.name")
            value = prop(ep, "Header.value")
            if name:
                headers.append(Header(name=name, value=value))
        return headers

    def _parse_csv(self, node: ET.Element) -> dict[str, str]:
        return {
            "filename": prop(node, "filename"),
            "variableNames": prop(node, "variableNames"),
            "delimiter": prop(node, "delimiter", ","),
            "ignoreFirstLine": prop(node, "ignoreFirstLine", "false"),
            "name": testname(node),
        }

    def _parse_http_sampler(self, node: ET.Element, subtree: ET.Element) -> HttpRequest:
        method = (prop(node, "HTTPSampler.method", "GET") or "GET").upper()
        protocol = prop(node, "HTTPSampler.protocol")
        domain = prop(node, "HTTPSampler.domain")
        port = prop(node, "HTTPSampler.port")
        path = prop(node, "HTTPSampler.path", "/")
        follow = prop_bool(node, "HTTPSampler.follow_redirects", True)
        post_raw = prop_bool(node, "HTTPSampler.postBodyRaw", False)

        body = ""
        form_params: list[tuple[str, str]] = []

        # Arguments / body
        for child in node:
            if child.get("name") in ("HTTPsampler.Arguments", "HTTPSampler.Arguments"):
                args = list(child.findall(".//elementProp[@elementType='HTTPArgument']"))
                if not args:
                    args = [
                        ep
                        for ep in child.iter("elementProp")
                        if ep.get("elementType") in ("HTTPArgument", "Argument")
                    ]
                if post_raw and args:
                    # First argument value is raw body
                    body = prop(args[0], "Argument.value")
                else:
                    for arg in args:
                        an = prop(arg, "Argument.name")
                        av = prop(arg, "Argument.value")
                        if an or av:
                            form_params.append((an, av))

        req = HttpRequest(
            name=testname(node),
            method=method,
            protocol=protocol,
            domain=domain,
            port=port,
            path=path or "/",
            body=body,
            form_params=form_params,
            follow_redirects=follow,
        )

        # Children: headers, extractors, assertions
        for child, child_tree in children_pairs(subtree):
            if not is_enabled(child):
                continue
            tc = testclass(child)
            if tc == "HeaderManager":
                req.headers.extend(self._parse_headers(child))
            elif tc == "RegexExtractor":
                ref = prop(child, "RegexExtractor.refname")
                regex = prop(child, "RegexExtractor.regex")
                if ref and regex:
                    req.extractors.append(
                        Extractor(
                            kind="regex",
                            var_name=ref,
                            expression=regex,
                            match_number=prop(child, "RegexExtractor.match_number", "1"),
                            use_headers=prop_bool(child, "RegexExtractor.useHeaders", False),
                        )
                    )
            elif tc == "JSONPostProcessor":
                names = prop(child, "JSONPostProcessor.referenceNames")
                exprs = prop(child, "JSONPostProcessor.jsonPathExprs")
                # Can be semicolon-separated lists
                name_list = [n.strip() for n in re.split(r"[;\n]", names) if n.strip()]
                expr_list = [e.strip() for e in re.split(r"[;\n]", exprs) if e.strip()]
                for i, n in enumerate(name_list):
                    expr = expr_list[i] if i < len(expr_list) else (expr_list[0] if expr_list else "")
                    if expr:
                        req.extractors.append(Extractor(kind="json", var_name=n, expression=expr))
            elif tc == "ResponseAssertion":
                req.assertions.extend(self._parse_response_assertion(child))
            elif tc.startswith("JSR223") or tc.startswith("BeanShell"):
                self.plan.warnings.append(
                    f"Script under HTTP '{req.name}' not converted: {testname(child)}"
                )

        return req

    def _parse_response_assertion(self, node: ET.Element) -> list[Assertion]:
        assertions: list[Assertion] = []
        test_field = prop(node, "Assertion.test_field", "Assertion.response_data")
        test_type = prop(node, "Assertion.test_type", "2")
        patterns: list[str] = []
        for col in node.iter("collectionProp"):
            if col.get("name") == "Asserion.test_strings" or col.get("name") == "Assertion.test_strings":
                for sp in col.findall("stringProp"):
                    if sp.text:
                        patterns.append(sp.text)
        # Also handle typo-free variant
        if not patterns:
            for sp in node.findall(".//collectionProp[@name='Assertion.test_strings']/stringProp"):
                if sp.text:
                    patterns.append(sp.text)

        if test_field == "Assertion.response_code":
            assertions.append(Assertion(kind="status", values=patterns))
        elif patterns:
            # 1=contains, 2=not contains, 8=equals, etc. Map loosely.
            kind = "contains"
            try:
                t = int(test_type)
                if t in (8, 16):
                    kind = "equals"
            except ValueError:
                pass
            assertions.append(Assertion(kind=kind, values=patterns))
        return assertions


# ---------------------------------------------------------------------------
# Code generators
# ---------------------------------------------------------------------------

def build_url(req: HttpRequest, plan: Plan) -> str:
    protocol = req.protocol or plan.base_protocol or "https"
    domain = req.domain or plan.base_domain
    port = req.port if req.port and req.port not in ("0", "") else (plan.base_port or "")
    path = req.path or "/"
    if not path.startswith("/") and not path.startswith("${") and not path.startswith("#{"):
        # relative path
        path = "/" + path

    if domain:
        authority = domain
        if port:
            authority = f"{domain}:{port}"
        # Absolute-ish URL; leave ${}/#{} intact for later conversion
        if "://" in domain:
            url = domain.rstrip("/") + (path if path.startswith("/") else "/" + path)
        else:
            url = f"{protocol}://{authority}{path if path.startswith('/') else '/' + path}"
    else:
        # relative to baseUrl
        url = path

    return jmeter_to_gatling_expr(url)


class JavaGenerator:
    def __init__(self, plan: Plan, package: Optional[str] = None) -> None:
        self.plan = plan
        self.package = package
        self.class_name = safe_class_name(plan.name)
        self.indent = "  "

    def generate(self) -> str:
        lines: list[str] = []
        if self.package:
            lines.append(f"package {self.package};")
            lines.append("")
        lines.append("import static io.gatling.javaapi.core.CoreDsl.*;")
        lines.append("import static io.gatling.javaapi.http.HttpDsl.*;")
        lines.append("")
        lines.append("import io.gatling.javaapi.core.*;")
        lines.append("import io.gatling.javaapi.http.*;")
        lines.append("")
        lines.append("import java.time.Duration;")
        lines.append("import java.util.Map;")
        lines.append("")
        lines.append(f"/**")
        lines.append(f" * Auto-generated from JMeter plan: {self.plan.name}")
        lines.append(f" * Review TODOs and scripted logic before running.")
        lines.append(f" */")
        lines.append(f"public class {self.class_name} extends Simulation {{")
        lines.append("")

        # Variables as Map feeder / session
        if self.plan.variables:
            lines.append(f"{self.indent}// User Defined Variables from JMeter")
            lines.append(
                f"{self.indent}private static final Map<String, Object> UDV = Map.ofEntries("
            )
            for i, (k, v) in enumerate(self.plan.variables.items()):
                comma = "," if i < len(self.plan.variables) - 1 else ""
                lines.append(
                    f"{self.indent}{self.indent}Map.entry({java_string(k)}, {java_string(v)}){comma}"
                )
            lines.append(f"{self.indent});")
            lines.append("")

        # CSV feeders
        for i, csv in enumerate(self.plan.csv_feeders):
            fname = csv.get("filename") or f"data_{i}.csv"
            feeder_name = safe_ident(csv.get("name") or f"csvFeeder{i}", "csvFeeder")
            ignore = csv.get("ignoreFirstLine", "false").lower() == "true"
            lines.append(
                f"{self.indent}private static final FeederBuilder.Batchable<String> {feeder_name} ="
            )
            lines.append(
                f"{self.indent}{self.indent}csv({java_string(fname)})"
                + (".circular();" if True else ";")
            )
            if ignore:
                lines.append(
                    f"{self.indent}// JMeter ignoreFirstLine=true — ensure CSV has a header row (Gatling uses headers by default)."
                )
            vars_ = csv.get("variableNames")
            if vars_:
                lines.append(f"{self.indent}// Expected columns: {vars_}")
            lines.append("")

        # HTTP protocol
        lines.extend(self._http_protocol())
        lines.append("")

        # Scenarios
        sc_builders: list[str] = []
        for idx, sc in enumerate(self.plan.scenarios):
            if sc.is_setup and not sc.steps:
                continue
            var = safe_ident(sc.name, f"scenario{idx}")
            sc_builders.append((var, sc))
            lines.extend(self._scenario(var, sc))
            lines.append("")

        # setUp
        lines.append(f"{self.indent}{{")
        setup_parts = []
        for var, sc in sc_builders:
            inj = self._injection(sc)
            setup_parts.append(f"{var}.{inj}")
        if not setup_parts:
            setup_parts.append('scenario("Empty").exec(http("noop").get("/")).injectOpen(atOnceUsers(1))')

        if len(setup_parts) == 1:
            lines.append(f"{self.indent}{self.indent}setUp(")
            lines.append(f"{self.indent}{self.indent}{self.indent}{setup_parts[0]}")
            lines.append(f"{self.indent}{self.indent}).protocols(httpProtocol);")
        else:
            lines.append(f"{self.indent}{self.indent}setUp(")
            for i, part in enumerate(setup_parts):
                comma = "," if i < len(setup_parts) - 1 else ""
                lines.append(f"{self.indent}{self.indent}{self.indent}{part}{comma}")
            lines.append(f"{self.indent}{self.indent}).protocols(httpProtocol);")
        lines.append(f"{self.indent}}}")
        lines.append("}")

        if self.plan.warnings:
            warn_block = "\n".join(f"// WARN: {w}" for w in self.plan.warnings)
            lines.insert(0, warn_block + "\n")

        return "\n".join(lines) + "\n"

    def _base_url(self) -> str:
        protocol = self.plan.base_protocol or "https"
        domain = self.plan.base_domain
        port = self.plan.base_port
        if not domain:
            # Try from variables
            for key in ("BASE_URL", "baseUrl", "BASE_URL_1", "host"):
                if key in self.plan.variables:
                    domain = self.plan.variables[key]
                    break
        if not domain:
            return "https://example.com"
        if "://" in domain:
            return domain.rstrip("/")
        authority = f"{domain}:{port}" if port else domain
        return f"{protocol}://{authority}"

    def _http_protocol(self) -> list[str]:
        lines = [
            f"{self.indent}private static final HttpProtocolBuilder httpProtocol = http",
            f"{self.indent}{self.indent}.baseUrl({java_string(self._base_url())})",
            f"{self.indent}{self.indent}.acceptHeader(\"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\")",
            f"{self.indent}{self.indent}.acceptEncodingHeader(\"gzip, deflate\")",
            f"{self.indent}{self.indent}.userAgentHeader(\"Gatling/jmx-converter\")",
        ]
        if self.plan.connect_timeout_ms:
            lines.append(
                f"{self.indent}{self.indent}// JMeter connect_timeout={self.plan.connect_timeout_ms}ms"
            )
        if self.plan.response_timeout_ms:
            lines.append(
                f"{self.indent}{self.indent}// JMeter response_timeout={self.plan.response_timeout_ms}ms"
            )

        # Prefer a few common default headers
        skip = {"accept", "accept-encoding", "user-agent", "host", "content-length"}
        for h in self.plan.default_headers:
            if h.name.lower() in skip:
                continue
            lines.append(
                f"{self.indent}{self.indent}.header({java_string(h.name)}, {java_string(jmeter_to_gatling_expr(h.value))})"
            )

        # Fix trailing — last line should end with ;
        # Rebuild last non-comment
        last_idx = max(i for i, l in enumerate(lines) if not l.strip().startswith("//") and l.strip())
        if not lines[last_idx].rstrip().endswith(";"):
            lines[last_idx] = lines[last_idx] + ";"
        return lines

    def _injection(self, sc: Scenario) -> str:
        users = sc.users
        ramp = sc.ramp_seconds
        if sc.is_setup:
            return "injectOpen(atOnceUsers(1))"
        if sc.duration and sc.duration > 0:
            return f"injectOpen(rampUsers({users}).during(Duration.ofSeconds({ramp or sc.duration})))"
        if ramp > 0 and users > 1:
            return f"injectOpen(rampUsers({users}).during(Duration.ofSeconds({ramp})))"
        if users == 1:
            return "injectOpen(atOnceUsers(1))"
        return f"injectOpen(atOnceUsers({users}))"

    def _scenario(self, var: str, sc: Scenario) -> list[str]:
        lines: list[str] = []
        label = ""
        lines.append(
            f"{self.indent}private static final ScenarioBuilder {var} = scenario({java_string(sc.name)})"
        )

        prefix = f"{self.indent}{self.indent}"
        if self.plan.variables:
            lines.append(f"{prefix}.exec(session -> session.setAll(UDV))")
        for i, csv in enumerate(self.plan.csv_feeders):
            feeder_name = safe_ident(csv.get("name") or f"csvFeeder{i}", "csvFeeder")
            lines.append(f"{prefix}.feed({feeder_name})")

        steps = sc.steps
        if sc.loops > 1 and not sc.is_setup:
            lines.append(f"{prefix}.repeat({sc.loops}).on(")
            step_lines = self._steps(steps, indent=3, nested=True)
            if not any(self._is_code_line(l) for l in step_lines):
                step_lines.append(f"{self.indent * 3}exec(http(\"placeholder\").get(\"/\"))")
            lines.extend(step_lines)
            lines.append(f"{prefix});")
        else:
            step_lines = self._steps(steps, indent=2, nested=False)
            code_steps = [l for l in step_lines if self._is_code_line(l)]
            comment_steps = [l for l in step_lines if l.strip().startswith("//")]
            if code_steps:
                lines.extend(step_lines)
            else:
                # Scripts/comments only — keep TODOs, end the builder cleanly
                lines.extend(comment_steps)
            self._terminate_statement(lines)
            if not code_steps and not any(
                ".exec(" in l or ".feed(" in l for l in lines
            ):
                # Completely empty scenario
                lines[-1] = lines[-1].rstrip(";") 
                lines.append(f"{prefix}.exec(http(\"placeholder\").get(\"/\"));")

        return lines

    @staticmethod
    def _is_code_line(line: str) -> bool:
        s = line.strip()
        return bool(s) and not s.startswith("//")

    def _terminate_statement(self, lines: list[str]) -> None:
        for i in range(len(lines) - 1, -1, -1):
            if self._is_code_line(lines[i]):
                if not lines[i].rstrip().endswith(";"):
                    lines[i] = lines[i] + ";"
                return
        if lines and not lines[-1].rstrip().endswith(";"):
            lines[-1] = lines[-1] + ";"

    def _steps(self, steps: list[Any], indent: int, nested: bool) -> list[str]:
        """Render steps. If nested=True, first action is `exec(...)` (no leading dot)."""
        lines: list[str] = []
        first_action = nested
        for step in steps:
            part, first_action = self._step(step, indent, first_action)
            lines.extend(part)
        return lines

    def _dot(self, first_action: bool) -> tuple[str, bool]:
        """Return (prefix, next_first_action). Nested blocks start without a leading dot."""
        if first_action:
            return "", False
        return ".", False

    def _step(
        self, step: Any, indent: int, first_action: bool
    ) -> tuple[list[str], bool]:
        pad = self.indent * indent
        if isinstance(step, Comment):
            return [f"{pad}// {step.text}"], first_action

        if isinstance(step, Pause):
            dot, first_action = self._dot(first_action)
            if step.random_max and step.random_max > step.millis:
                return [
                    f"{pad}{dot}pause(Duration.ofMillis({step.millis}), Duration.ofMillis({step.random_max}))"
                ], first_action
            return [f"{pad}{dot}pause(Duration.ofMillis({step.millis}))"], first_action

        if isinstance(step, Group):
            dot, first_action = self._dot(first_action)
            lines = [f"{pad}{dot}group({java_string(step.name)}).on("]
            lines.extend(self._steps(step.children, indent + 1, nested=True))
            lines.append(f"{pad})")
            return lines, first_action

        if isinstance(step, Loop):
            dot, first_action = self._dot(first_action)
            lines = [f"{pad}{dot}repeat({step.times}).on("]
            lines.extend(self._steps(step.children, indent + 1, nested=True))
            lines.append(f"{pad})")
            return lines, first_action

        if isinstance(step, DoIf):
            if step.unsupported:
                # Keep requests; caller must replace with a real doIf later.
                lines = [
                    f"{pad}// TODO IfController '{step.name}': {step.condition}",
                    f"{pad}// Children run unconditionally until you wrap them in doIf(...).",
                ]
                part, first_action = [], first_action
                for child in step.children:
                    child_lines, first_action = self._step(child, indent, first_action)
                    lines.extend(child_lines)
                return lines, first_action

            dot, first_action = self._dot(first_action)
            cond = jmeter_to_gatling_expr(step.condition)
            m = re.fullmatch(r"#\{([A-Za-z_][A-Za-z0-9_]*)\}", cond.strip())
            if m:
                var = m.group(1)
                lines = [
                    f"{pad}{dot}doIf(session -> Boolean.TRUE.equals(session.get(\"{var}\")) "
                    f"|| \"true\".equalsIgnoreCase(String.valueOf(session.get(\"{var}\")))).then("
                ]
            else:
                lines = [
                    f"{pad}// Condition from JMeter: {step.condition}",
                    f"{pad}{dot}doIf(session -> true).then(",
                ]
            lines.extend(self._steps(step.children, indent + 1, nested=True))
            lines.append(f"{pad})")
            return lines, first_action

        if isinstance(step, HttpRequest):
            return self._http_request(step, indent, first_action)

        return [f"{pad}// Unknown step: {step!r}"], first_action

    def _http_request(
        self, req: HttpRequest, indent: int, first_action: bool
    ) -> tuple[list[str], bool]:
        pad = self.indent * indent
        dot, first_action = self._dot(first_action)
        url = build_url(req, self.plan)
        method = req.method.upper()
        lines = [f"{pad}{dot}exec("]
        p2 = self.indent * (indent + 1)
        lines.append(f"{p2}http({java_string(req.name)})")

        method_map = {
            "GET": "get",
            "POST": "post",
            "PUT": "put",
            "DELETE": "delete",
            "PATCH": "patch",
            "HEAD": "head",
            "OPTIONS": "options",
        }
        m = method_map.get(method, "get")
        if method not in method_map:
            lines.append(f"{p2}// Unsupported method {method}; defaulting to GET")
        lines.append(f"{p2}.{m}({java_string(url)})")

        for h in req.headers:
            lines.append(
                f"{p2}.header({java_string(h.name)}, {java_string(jmeter_to_gatling_expr(h.value))})"
            )

        if req.body:
            lines.append(f"{p2}.body(StringBody({java_string(jmeter_to_gatling_expr(req.body))}))")
        elif req.form_params and method in ("POST", "PUT", "PATCH"):
            for name, value in req.form_params:
                lines.append(
                    f"{p2}.formParam({java_string(name)}, {java_string(jmeter_to_gatling_expr(value))})"
                )
        elif req.form_params and method == "GET":
            for name, value in req.form_params:
                lines.append(
                    f"{p2}.queryParam({java_string(name)}, {java_string(jmeter_to_gatling_expr(value))})"
                )

        if not req.follow_redirects:
            lines.append(f"{p2}.disableFollowRedirect()")

        for a in req.assertions:
            if a.kind == "status" and a.values:
                for v in a.values:
                    if v.isdigit():
                        lines.append(f"{p2}.check(status().is({v}))")
                    else:
                        lines.append(
                            f"{p2}.check(status().is({java_string(jmeter_to_gatling_expr(v))}))"
                        )
            elif a.kind == "contains":
                for v in a.values:
                    lines.append(f"{p2}.check(bodyString().contains({java_string(v)}))")
            elif a.kind == "equals":
                for v in a.values:
                    lines.append(f"{p2}.check(bodyString().is({java_string(v)}))")

        for ex in req.extractors:
            if ex.kind == "json":
                lines.append(
                    f"{p2}.check(jsonPath({java_string(ex.expression)}).saveAs({java_string(ex.var_name)}))"
                )
            elif ex.kind == "regex":
                if ex.use_headers:
                    lines.append(
                        f"{p2}// TODO: header regex — refine header name for '{ex.var_name}'"
                    )
                lines.append(
                    f"{p2}.check(regex({java_string(ex.expression)}).saveAs({java_string(ex.var_name)}))"
                )

        lines.append(f"{pad})")
        return lines, first_action


def scala_relative_url(url: str, base_domain: str) -> str:
    """Prefer relative paths so Environment.baseURL can be used."""
    url = jmeter_to_gatling_expr(url)
    # Absolute with Gatling EL host → keep absolute (multi-host login flows)
    if url.startswith("https://#{") or url.startswith("http://#{"):
        return url
    if base_domain and base_domain in url:
        # strip scheme://host[:port]
        m = re.match(r"^https?://[^/]+(/.*)?$", url)
        if m:
            return m.group(1) or "/"
    if url.startswith("https://") or url.startswith("http://"):
        return url
    if not url.startswith("/"):
        return "/" + url
    return url


class ScalaGenerator:
    """Scala DSL generator matching xui-performance layout (scenarios + simulations)."""

    def __init__(
        self,
        plan: Plan,
        package: Optional[str] = None,
        class_name: Optional[str] = None,
        use_environment: bool = True,
    ) -> None:
        self.plan = plan
        self.package = package or "simulations"
        self.class_name = class_name or safe_class_name(plan.name)
        self.object_name = self.class_name.replace("Simulation", "") or "Converted"
        if self.object_name.endswith("_"):
            self.object_name = self.object_name.rstrip("_")
        self.use_environment = use_environment
        self.indent = "\t"

    def _base_url_expr(self) -> str:
        if self.use_environment:
            return 'Environment.baseURL.replace("#{env}", env)'
        protocol = self.plan.base_protocol or "https"
        domain = self.plan.base_domain or "example.com"
        if "://" in domain:
            return scala_string(domain.rstrip("/"))
        port = f":{self.plan.base_port}" if self.plan.base_port else ""
        return scala_string(f"{protocol}://{domain}{port}")

    def generate(self) -> str:
        """Single-file Simulation (fallback)."""
        return self.generate_simulation(include_scenario_inline=True)

    def generate_scenario_object(self) -> str:
        lines: list[str] = []
        if self.plan.warnings:
            lines.extend(f"// WARN: {w}" for w in self.plan.warnings)
            lines.append("")
        lines.append("package scenarios")
        lines.append("")
        lines.append("import io.gatling.core.Predef._")
        lines.append("import io.gatling.http.Predef._")
        lines.append("")
        lines.append("import scala.concurrent.duration._")
        lines.append("")
        lines.append("import utils.Environment")
        lines.append("")
        lines.append(f"object {self.object_name} {{")
        lines.append("")
        lines.append(f"{self.indent}val BaseURL = Environment.baseURL")
        lines.append(f"{self.indent}val IdamURL = Environment.idamURL")
        lines.append(f"{self.indent}val MinThinkTime = Environment.minThinkTime")
        lines.append(f"{self.indent}val MaxThinkTime = Environment.maxThinkTime")
        lines.append("")

        # Prefer the main (non-setup) scenario
        main_sc = next((s for s in self.plan.scenarios if not s.is_setup), None)
        if main_sc is None and self.plan.scenarios:
            main_sc = self.plan.scenarios[0]
        if main_sc is None:
            lines.append(f"{self.indent}val Flow = exec(http(\"placeholder\").get(\"/\"))")
        else:
            lines.append(f"{self.indent}/*====================================================================================")
            lines.append(f"{self.indent}* Auto-converted from JMeter: {self.plan.name}")
            lines.append(f"{self.indent}* Review TODOs (JSR223 scripts are not converted).")
            lines.append(f"{self.indent} ====================================================================================*/")
            lines.append("")
            lines.append(f"{self.indent}val Flow =")
            lines.append("")
            step_lines = self._steps(main_sc.steps, indent=2, nested=True)
            if not step_lines:
                step_lines = [f"{self.indent * 2}exec(http(\"placeholder\").get(\"/\"))"]
            lines.extend(step_lines)

        lines.append("")
        lines.append("}")
        return "\n".join(lines) + "\n"

    def generate_simulation(self, include_scenario_inline: bool = False) -> str:
        lines: list[str] = []
        if self.plan.warnings and include_scenario_inline:
            lines.extend(f"// WARN: {w}" for w in self.plan.warnings)
            lines.append("")
        lines.append(f"package {self.package}")
        lines.append("")
        lines.append("import io.gatling.core.Predef._")
        lines.append("import io.gatling.http.Predef._")
        lines.append("import scenarios._")
        lines.append("import utils._")
        lines.append("")
        lines.append("import scala.concurrent.duration._")
        lines.append("")
        lines.append(f"class {self.class_name} extends Simulation {{")
        lines.append("")
        lines.append(f'{self.indent}val env = System.getProperty("env", "perftest")')
        lines.append(f'{self.indent}val debugMode = System.getProperty("debug", "off")')
        lines.append("")

        for i, csv in enumerate(self.plan.csv_feeders):
            fname = Path(csv.get("filename") or f"data_{i}.csv").name
            feeder_name = safe_ident(csv.get("name") or f"csvFeeder{i}", "csvFeeder")
            lines.append(
                f"{self.indent}val {feeder_name} = csv({scala_string(fname)}).circular"
            )
        if self.plan.csv_feeders:
            lines.append("")

        lines.append(f"{self.indent}val httpProtocol = http")
        lines.append(f"{self.indent}{self.indent}.baseUrl({self._base_url_expr()})")
        lines.append(f'{self.indent}{self.indent}.inferHtmlResources()')
        lines.append(f'{self.indent}{self.indent}.silentResources')
        lines.append(f'{self.indent}{self.indent}.header("experimental", "true")')
        skip = {"accept", "accept-encoding", "user-agent", "host", "content-length", "experimental"}
        for h in self.plan.default_headers:
            if h.name.lower() in skip:
                continue
            # Keep only a few useful defaults; browser client hints are noisy
            if h.name.lower().startswith("sec-ch-ua"):
                continue
            lines.append(
                f"{self.indent}{self.indent}.header({scala_string(h.name)}, {scala_string(jmeter_to_gatling_expr(h.value))})"
            )
        lines.append("")

        main_sc = next((s for s in self.plan.scenarios if not s.is_setup), None)
        users = main_sc.users if main_sc else 1
        ramp = main_sc.ramp_seconds if main_sc else 0
        loops = main_sc.loops if main_sc else 1

        lines.append(f'{self.indent}val scn = scenario({scala_string(self.object_name)})')
        lines.append(f"{self.indent}{self.indent}.exitBlockOnFail {{")
        body_prefix = f"{self.indent}{self.indent}{self.indent}"
        first_in_block = True
        for i, csv in enumerate(self.plan.csv_feeders):
            feeder_name = safe_ident(csv.get("name") or f"csvFeeder{i}", "csvFeeder")
            if first_in_block:
                lines.append(f"{body_prefix}feed({feeder_name})")
                first_in_block = False
            else:
                lines.append(f"{body_prefix}.feed({feeder_name})")
        if first_in_block:
            lines.append(f'{body_prefix}exec(_.set("env", env))')
            first_in_block = False
        else:
            lines.append(f'{body_prefix}.exec(_.set("env", env))')

        if include_scenario_inline and main_sc:
            if loops > 1:
                lines.append(f"{body_prefix}.repeat({loops}) {{")
                lines.extend(self._steps(main_sc.steps, indent=4, nested=True))
                lines.append(f"{body_prefix}}}")
            else:
                # continue chain with leading dots
                lines.extend(self._steps(main_sc.steps, indent=3, nested=False))
        else:
            chain = f"{self.object_name}.Flow"
            if loops > 1:
                lines.append(f"{body_prefix}.repeat({loops}) {{")
                lines.append(f"{body_prefix}{self.indent}exec({chain})")
                lines.append(f"{body_prefix}}}")
            else:
                lines.append(f"{body_prefix}.exec({chain})")
        lines.append(f"{self.indent}{self.indent}}}")
        lines.append("")

        if ramp > 0 and users > 1:
            inj = f"rampUsers({users}).during({ramp}.seconds)"
        else:
            inj = f"atOnceUsers(if (debugMode == \"on\") 1 else {users})"

        lines.append(f"{self.indent}setUp(")
        lines.append(f"{self.indent}{self.indent}scn.inject({inj})")
        lines.append(f"{self.indent}).protocols(httpProtocol)")
        lines.append("}")
        return "\n".join(lines) + "\n"

    def _steps(self, steps: list[Any], indent: int, nested: bool) -> list[str]:
        lines: list[str] = []
        first = nested
        for step in steps:
            part, first = self._step(step, indent, first)
            lines.extend(part)
        return lines

    def _dot(self, first: bool) -> tuple[str, bool]:
        return ("", False) if first else (".", False)

    def _step(self, step: Any, indent: int, first: bool) -> tuple[list[str], bool]:
        pad = self.indent * indent
        if isinstance(step, Comment):
            return [f"{pad}// {step.text}"], first
        if isinstance(step, Pause):
            dot, first = self._dot(first)
            # Map to Environment think time when pause is substantial
            if step.millis >= 1000:
                return [f"{pad}{dot}pause(MinThinkTime, MaxThinkTime)"], first
            return [f"{pad}{dot}pause({step.millis}.millis)"], first
        if isinstance(step, Group):
            dot, first = self._dot(first)
            lines = [f"{pad}{dot}group({scala_string(step.name)}) {{"]
            lines.extend(self._steps(step.children, indent + 1, nested=True))
            lines.append(f"{pad}}}")
            return lines, first
        if isinstance(step, Loop):
            dot, first = self._dot(first)
            lines = [f"{pad}{dot}repeat({step.times}) {{"]
            lines.extend(self._steps(step.children, indent + 1, nested=True))
            lines.append(f"{pad}}}")
            return lines, first
        if isinstance(step, DoIf):
            lines = [
                f"{pad}// TODO IfController '{step.name}': {step.condition}",
                f"{pad}// Children run unconditionally until wrapped in doIf(...).",
            ]
            for child in step.children:
                child_lines, first = self._step(child, indent, first)
                lines.extend(child_lines)
            return lines, first
        if isinstance(step, HttpRequest):
            return self._http(step, indent, first)
        return [f"{pad}// Unknown step: {step!r}"], first

    def _http(self, req: HttpRequest, indent: int, first: bool) -> tuple[list[str], bool]:
        pad = self.indent * indent
        dot, first = self._dot(first)
        path = "/" + (req.path or "").lstrip("/")
        # Preserve query string embedded in path
        domain = req.domain or ""
        method = req.method.lower()
        if method not in ("get", "post", "put", "delete", "patch", "head", "options"):
            method = "get"

        # Choose URL: relative for manage-case base, absolute IDAM with #{env}
        if "BASE_URL_2" in domain or "idam" in domain.lower():
            url = f"https://idam-web-public.#{{env}}.platform.hmcts.net{path}"
        elif domain and "BASE_URL_1" not in domain and "://" not in domain and domain != (self.plan.base_domain or ""):
            protocol = req.protocol or self.plan.base_protocol or "https"
            url = jmeter_to_gatling_expr(f"{protocol}://{domain}{path}")
        else:
            url = path

        lines = [f"{pad}{dot}exec(http({scala_string(req.name)})"]
        p2 = self.indent * (indent + 1)
        lines.append(f"{p2}.{method}({scala_string(jmeter_to_gatling_expr(url))})")

        for h in req.headers:
            if h.name.lower() in ("host", "content-length"):
                continue
            lines.append(
                f"{p2}.header({scala_string(h.name)}, {scala_string(jmeter_to_gatling_expr(h.value))})"
            )

        if req.body:
            body = jmeter_to_gatling_expr(req.body)
            lines.append(f"{p2}.body(StringBody({scala_string(body)}))")
        elif req.form_params and method in ("post", "put", "patch"):
            for name, value in req.form_params:
                lines.append(
                    f"{p2}.formParam({scala_string(name)}, {scala_string(jmeter_to_gatling_expr(value))})"
                )
        elif req.form_params and method == "get":
            for name, value in req.form_params:
                lines.append(
                    f"{p2}.queryParam({scala_string(name)}, {scala_string(jmeter_to_gatling_expr(value))})"
                )

        for a in req.assertions:
            if a.kind == "status":
                for v in a.values:
                    if v.isdigit():
                        lines.append(f"{p2}.check(status.is({v}))")
            elif a.kind == "contains":
                for v in a.values:
                    lines.append(f"{p2}.check(substring({scala_string(v)}))")

        for ex in req.extractors:
            if ex.kind == "json":
                lines.append(
                    f"{p2}.check(jsonPath({scala_string(ex.expression)}).saveAs({scala_string(ex.var_name)}))"
                )
            elif ex.kind == "regex":
                lines.append(
                    f"{p2}.check(regex({scala_string(ex.expression)}).saveAs({scala_string(ex.var_name)}))"
                )

        lines.append(f"{pad})")
        return lines, first


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def main(argv: Optional[list[str]] = None) -> int:
    parser = argparse.ArgumentParser(
        description="Convert JMeter .jmx files to Gatling simulations."
    )
    parser.add_argument("jmx", type=Path, help="Path to the .jmx file")
    parser.add_argument(
        "-o",
        "--output",
        type=Path,
        help="Output file (default: <jmx_stem>Simulation.java|.scala)",
    )
    parser.add_argument(
        "--scala",
        action="store_true",
        help="Generate Scala DSL instead of Java DSL",
    )
    parser.add_argument(
        "--xui",
        action="store_true",
        help="Write xui-performance layout: scenarios/<Name>.scala + simulations/<Name>_Simulation.scala",
    )
    parser.add_argument(
        "--name",
        dest="type_name",
        default=None,
        help="Scala object/class base name (e.g. CC_CaseLink)",
    )
    parser.add_argument(
        "--gatling-root",
        type=Path,
        default=None,
        help="xui-performance root (used with --xui)",
    )
    parser.add_argument(
        "--package",
        dest="package_name",
        default=None,
        help="Java/Scala package name",
    )
    parser.add_argument(
        "-q",
        "--quiet",
        action="store_true",
        help="Suppress warning summary on stderr",
    )
    args = parser.parse_args(argv)

    jmx_path: Path = args.jmx
    if not jmx_path.is_file():
        print(f"Error: file not found: {jmx_path}", file=sys.stderr)
        return 1

    try:
        plan = JmxParser().parse(jmx_path)
    except ET.ParseError as exc:
        print(f"Error: invalid XML: {exc}", file=sys.stderr)
        return 1

    type_name = args.type_name or safe_ident(jmx_path.stem, "Converted")
    # Prefer readable names like CC_CaseLink
    if args.type_name:
        type_name = args.type_name
    else:
        type_name = safe_class_name(jmx_path.stem).replace("Simulation", "") or "Converted"

    if args.xui or (args.scala and args.gatling_root):
        args.scala = True
        root = args.gatling_root
        if root is None:
            # tools/ -> xui-performance
            root = Path(__file__).resolve().parents[1]
        sim_dir = root / "src" / "gatling" / "simulations"
        scenario_path = sim_dir / "scenarios" / f"{type_name}.scala"
        simulation_path = sim_dir / "simulations" / f"{type_name}_Simulation.scala"
        gen = ScalaGenerator(
            plan,
            package="simulations",
            class_name=f"{type_name}_Simulation",
            use_environment=True,
        )
        gen.object_name = type_name
        scenario_path.parent.mkdir(parents=True, exist_ok=True)
        simulation_path.parent.mkdir(parents=True, exist_ok=True)
        scenario_path.write_text(gen.generate_scenario_object(), encoding="utf-8")
        simulation_path.write_text(gen.generate_simulation(include_scenario_inline=False), encoding="utf-8")
        outs = [scenario_path, simulation_path]
    elif args.scala:
        gen = ScalaGenerator(plan, args.package_name or "simulations", f"{type_name}_Simulation")
        gen.object_name = type_name
        code = gen.generate_simulation(include_scenario_inline=True)
        out = args.output or (jmx_path.parent / f"{type_name}_Simulation.scala")
        out.write_text(code, encoding="utf-8")
        outs = [out]
    else:
        code = JavaGenerator(plan, args.package_name).generate()
        out = args.output or (jmx_path.parent / f"{safe_class_name(plan.name)}.java")
        out.write_text(code, encoding="utf-8")
        outs = [out]

    if not args.quiet:
        for out in outs:
            print(f"Wrote {out}")
        print(f"Scenarios: {len(plan.scenarios)}")
        http_count = sum(
            1
            for sc in plan.scenarios
            for st in _walk_steps(sc.steps)
            if isinstance(st, HttpRequest)
        )
        print(f"HTTP requests converted: {http_count}")
        if plan.warnings:
            print(f"Warnings: {len(plan.warnings)} (see comments in output)", file=sys.stderr)
            for w in plan.warnings[:12]:
                print(f"  - {w}", file=sys.stderr)
            if len(plan.warnings) > 12:
                print(f"  ... and {len(plan.warnings) - 12} more", file=sys.stderr)

    return 0


def _walk_steps(steps: list[Any]) -> Iterator[Any]:
    for s in steps:
        yield s
        kids = getattr(s, "children", None)
        if kids:
            yield from _walk_steps(kids)


if __name__ == "__main__":
    raise SystemExit(main())
