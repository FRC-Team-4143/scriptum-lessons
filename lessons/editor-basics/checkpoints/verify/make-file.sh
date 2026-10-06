#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/my-team/about.txt"

if [ ! -d "$PROJECT/my-team" ]; then
	echo "Make the my-team folder first."
	exit 1
fi
if [ ! -f "$FILE" ]; then
	if [ -f "$PROJECT/about.txt" ]; then
		echo "about.txt is outside the my-team folder. Drag it onto my-team, or delete it and make it again with my-team selected."
	elif [ -f "$PROJECT/my-team/about.txt.txt" ]; then
		echo "The file is called about.txt.txt. Rename it to about.txt (click it, press F2)."
	else
		echo "I don't see my-team/about.txt yet. Click the my-team folder, then hover over the PROJECT bar and click New File."
	fi
	exit 1
fi
if ! grep -q '[^[:space:]]' "$FILE"; then
	echo "about.txt is empty. Click on it, type a sentence about our team, and press Ctrl+S."
	exit 1
fi
echo "my-team/about.txt has your sentence in it."
