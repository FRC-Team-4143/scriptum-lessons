#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_common.sh"
text="$(user_text)"
names=$(echo "$text" | grep -oE '<input_port\b[^>]*\bname="[^"]+"' | sed -E 's/.*name="([^"]+)"/\1/' || true)
if [ -z "$names" ]; then
	echo "Declare a tunable: in the tree's properties add an input port (for example wait_msec, with a default of 500)."
	exit 1
fi
for n in $names; do
	if echo "$text" | grep -qE "=\"\{$n\}\""; then
		echo "'$n' is declared and used, so it can be tuned from the dashboard."
		exit 0
	fi
done
echo "You declared a port but nothing uses it. Type {name} (with the braces) into a node's port, for example msec = {wait_msec}."
exit 1
