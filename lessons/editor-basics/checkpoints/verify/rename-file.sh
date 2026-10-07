#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"

if [ -f "$PROJECT/team.txt" ] && [ ! -e "$PROJECT/untitled.txt" ]; then
	if grep -q "Team 4143" "$PROJECT/team.txt"; then
		echo "untitled.txt is now team.txt."
		exit 0
	fi
	echo "team.txt is there, but its words changed. Rename untitled.txt instead of making a new file."
	exit 1
fi
if [ -f "$PROJECT/team.txt.txt" ]; then
	echo "It's called team.txt.txt. When you rename, only the part before the dot is highlighted, so type just team."
	exit 1
fi
if [ -e "$PROJECT/untitled.txt" ]; then
	echo "untitled.txt still has its old name. Click it once, press F2, type team and press Enter."
	exit 1
fi
echo "I can't find untitled.txt or team.txt. Switch away from this lesson and back to reset it, then try again."
exit 1
