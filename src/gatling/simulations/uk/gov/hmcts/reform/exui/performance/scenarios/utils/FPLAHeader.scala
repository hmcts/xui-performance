package uk.gov.hmcts.reform.exui.performance.scenarios.utils

object FPLAHeader {

 val baseURL = Environment.baseURL

 val commonHeader = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "cross-site",
  "Upgrade-Insecure-Requests" -> "1")

 val headers_16 = Map(
  "Accept" -> "application/json",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-Type" -> "application/json",
  "Origin" -> baseURL,
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "cross-site")

 val headers_uploadfile = Map(
  "Accept" -> "",
  "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundary0NoZ3fbG8de034Bj",
  "Origin" -> baseURL,
  "Sec-Fetch-Dest" -> "empty",
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "same-origin")

 val headers_casecreation = Map(
  "Accept" -> "application/json",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-Type" -> "application/json",
  "Origin" -> baseURL,
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "cross-site")

 val headers_startcreatecase = Map(
  "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-Type" -> "application/json",
  "Origin" -> baseURL,
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "cross-site",
  "experimental" -> "true")

 val headers_71 = Map(
  "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
  "Content-Type" -> "application/json",
  "Origin" -> baseURL,
  "Sec-Fetch-Dest" -> "empty",
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "same-origin",
  "experimental" -> "true")

 val headers_72 = Map(
  "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-Type" -> "application/json",
  "Origin" -> baseURL,
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "cross-site",
  "experimental" -> "true")

 val headers_74 = Map(
  "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-Type" -> "application/json",
  "Origin" -> baseURL,
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "cross-site",
  "experimental" -> "true")

 val headers_76 = Map(
  "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-Type" -> "application/json",
  "Origin" -> baseURL,
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "cross-site",
  "experimental" -> "true")

 val headers_80 = Map(
  "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-Type" -> "application/json",
  "Origin" -> baseURL,
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Dest" -> "empty",
  "Sec-Fetch-Site" -> "same-origin",
  "experimental" -> "true")

 val headers_81 = Map(
  "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
  "Content-Type" -> "application/json",
  "Origin" -> baseURL,
  "Sec-Fetch-Dest" -> "empty",
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "same-origin",
  "experimental" -> "true")

 val headers_139 = Map(
  "Accept" -> "",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundaryucjE7WrGyxbtCSQN",
  "Origin" -> baseURL,
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "cross-site")

 val headers_140 = Map(
  "Accept" -> "",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundary9n7AAqp1SNXC8LRR",
  "Origin" -> baseURL,
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "cross-site")

 val headers_search = Map(
  "Sec-Fetch-Dest" -> "empty",
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "same-origin")

 val headers_documents = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "none",
  "Upgrade-Insecure-Requests" -> "1")

 val headers_createprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h38vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-filter")

 val headers_opencaseprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h40vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-create/PUBLICLAW/CARE_SUPERVISION_EPO/openCase/openCase1")

 val headers_casesprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h43vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0")

 val headers_orddersprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.Y7WW",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h48vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}")

 val headers_ordersneed1profile= Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.xtL1X",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h51vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/ordersNeeded/ordersNeeded1")

 val headers_hearingneededprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.foKPP",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h61vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/hearingNeeded/hearingNeeded1")
 val headers_childrenprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.c7MKw",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h72vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/enterChildren/enterChildren1")
 val headers_respondantprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.qOiFU",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h83vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/enterRespondents/enterRespondents1")

 val headers_applicantprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.SiNFL",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h101vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/enterApplicant/enterApplicant1")

 val headers_groundsprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.eINt",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h111vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/enterGrounds/enterGrounds1")

 val headers_otherprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.fYX/R",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h121vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/otherProposal/otherProposal1")

 val headers_uploaddocprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.l5g3b",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h132vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/uploadDocuments/uploadDocuments1")

 val headers_submitprofile = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
  "request-id" -> "|l2yDf.bYLSk",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$407919787_877h171vNMOKVINRMMCFSCDPWHJRAGBTITOCLLHM-0",
  "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/submitApplication/submitApplication1")

 val headers_viewtab = Map(
  "accept" -> "application/json, text/plain, */*",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$430739744_795h37vRRPOHSUEPACJDAFRRDTWMOFWLCMTVMIB-0")

 val headers_searchinputs = Map(
  "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-search-input-details.v2+json;charset=UTF-8",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "content-type" -> "application/json",
  "experimental" -> "true",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$430739744_795h28vRRPOHSUEPACJDAFRRDTWMOFWLCMTVMIB-0")

  

}
