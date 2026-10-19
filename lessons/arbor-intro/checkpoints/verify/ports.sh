#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_common.sh"
text="$(user_text)"

# Every node of a kind must have its port filled in, not just one of them.
count() { { echo "$text" | grep -oE "$1" || true; } | wc -l; }
check() { # tag port pattern hint
	local total filled
	total=$(count "<$1\\b")
	filled=$(count "<$1\\b[^>]*\\b$2=\"$3\"")
	if [ "$total" -eq 0 ]; then
		echo "Add a $1 step to your tree."
		exit 1
	fi
	if [ "$filled" -lt "$total" ]; then
		echo "$4"
		exit 1
	fi
}
check SetWantedState state '[A-Z_]+' "Give every SetWantedState its state: select it and type a state in its state setting on the right (IDLE, DRIVE, AIM, SHOOT or AUTO)."
check FollowPath trajectory '[^"{}]+' "Give every FollowPath the name of a path: select it and type a path name in its trajectory setting on the right."
check Sleep msec '[^"]+' "Give every Sleep a time: select it and fill in msec (milliseconds, e.g. 500)."
echo "Every step has its settings filled in."
