# Use this script to ingest all PED logs from a given test and calculate the latency in ms between Presenters sending messages
# and Followers receiving them.
# Usage: python calculate-ped-message-latencies.py <log-folder>
# E.g: python calculate-ped-message-latencies.py ../logs/ped/20250219-1319
# An output file will be written the input log folder called latencies-output.txt

import os
import re
import json
import sys
from collections import defaultdict

def parse_presenter_line(line):
    match = re.search(r'sending page:(\d+) top:(-?\d+) left:(-?\d+) at (\d+)', line)
    if match:
        return {
            "page": int(match.group(1)),
            "top": int(match.group(2)),
            "left": int(match.group(3)),
            "timestamp": int(match.group(4))
        }
    return None

def parse_follower_line(line):
    match = re.search(r'Text\((\d+),.*?"IcpScreenUpdated".*?"pageNumber":(\d+),.*?"top":(-?\d+),.*?"left":(-?\d+)', line)
    if match:
        return {
            "timestamp": int(match.group(1)),
            "page": int(match.group(2)),
            "top": int(match.group(3)),
            "left": int(match.group(4))
        }
    return None

def process_logs(folder_path):
    sessions = defaultdict(lambda: {"presenter": [], "followers": defaultdict(list)})
    latencies = []

    for filename in os.listdir(folder_path):
        file_path = os.path.join(folder_path, filename)

        if not os.path.isfile(file_path):
            continue

        with open(file_path, 'r', encoding='utf-8') as file:
            for line in file:
                parts = line.strip().split(',', 4)
                if len(parts) < 5:
                    continue
                session_id, user, role, index, message = parts

                if "Presenter" in filename:
                    parsed_data = parse_presenter_line(message)
                    if parsed_data:
                        sessions[session_id]["presenter"].append(parsed_data)
                elif "Follower" in filename:
                    parsed_data = parse_follower_line(message)
                    if parsed_data:
                        sessions[session_id]["followers"][user].append(parsed_data)

    for session_id, data in sessions.items():
        for presenter_message in data["presenter"]:
            for follower, messages in data["followers"].items():
                for follower_message in messages:
                    if (presenter_message["page"] == follower_message["page"] and
                        presenter_message["top"] == follower_message["top"] and
                        presenter_message["left"] == follower_message["left"]):
                        latency = follower_message["timestamp"] - presenter_message["timestamp"]
                        latencies.append(latency)

    # Define output file path in the same folder as input files
    output_file_path = os.path.join(folder_path, "latencies_output.txt")

    # Write latencies to a file (one per line)
    with open(output_file_path, "w") as output_file:
        output_file.write("message-latencies-in-ms\n")
        for latency in latencies:
            output_file.write(f"{latency}\n")

    # Print full path of the output file
    print(f"Latencies written to: {output_file_path}")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <folder_path>")
        sys.exit(1)
    folder_path = sys.argv[1]
    process_logs(folder_path)