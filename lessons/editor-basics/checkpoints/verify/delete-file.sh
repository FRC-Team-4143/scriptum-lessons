#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"

if [ -e "$PROJECT/delete-me.txt" ]; then
	echo "delete-me.txt is still there. Click it once, press Delete, then confirm."
	exit 1
fi
if [ ! -f "$PROJECT/src/Main.java" ]; then
	echo "delete-me.txt is gone, but src/Main.java is missing too. Switch away from this lesson and back to reset it."
	exit 1
fi
echo "delete-me.txt is gone."
