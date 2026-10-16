#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_common.sh"
text="$(user_text)"
names=$(echo "$text" | grep -oE '<input_port\b[^>]*\bname="[^"]+"' | sed -E 's/.*name="([^"]+)"/\1/' || true)
if [ -z "$names" ]; then
	echo "Make a tunable: click an empty part of the canvas, then under the tree's own settings (the heading that starts SubTree interface) click + Add port, and name it wait_msec with a default of 500."
	exit 1
fi
for n in $names; do
	if echo "$text" | grep -qE "=\"\{$n\}\""; then
		echo "'$n' is declared and used, so it can be tuned from the dashboard."
		exit 0
	fi
done
echo "The tree has a setting of its own, but no step uses it yet. Type its name in braces into a step's setting, for example the Sleep's msec = {wait_msec}."
exit 1
