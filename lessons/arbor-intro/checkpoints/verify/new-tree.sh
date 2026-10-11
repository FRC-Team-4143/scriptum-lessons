#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_common.sh"
if [ ${#USER_FILES[@]} -eq 0 ]; then
	echo "Make your own tree: click + New tree in Arbor, name it, save it in a new file, then press Ctrl+S."
	exit 1
fi
for f in "${USER_FILES[@]}"; do
	text="$(strip < "$f")"
	if echo "$text" | grep -qE '<BehaviorTree[^>]*ID="[^"]+"'; then
		echo "Found your tree in $(basename "$f")."
		exit 0
	fi
done
echo "Your tree file has no tree in it yet. Open it in Arbor and save it (Ctrl+S)."
exit 1
