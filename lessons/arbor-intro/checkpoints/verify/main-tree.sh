#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_common.sh"
source "$(dirname "$0")/_trees.sh"
if [ ${#MAINS[@]} -eq 0 ]; then
	echo "Mark the tree the robot runs: right-click MyFirstAuto in the Trees list, choose Set as main tree of file, then save (Ctrl+S)."
	exit 1
fi
for m in "${MAINS[@]}"; do
	file="${m%%|*}"
	main="${m#*|}"
	if ! defined_in "$file" "$main"; then
		continue
	fi
	if is_used "$main"; then
		echo "$main is marked as the main tree, but it's a SubTree that another tree uses. Mark the whole auto (MyFirstAuto) as the main tree instead, then save."
		exit 1
	fi
	echo "$main is the main tree of $(basename "$file"), so that's the one the robot runs."
	exit 0
done
echo "Your file names a main tree that isn't in it. Right-click MyFirstAuto in the Trees list, choose Set as main tree of file, then save."
exit 1
