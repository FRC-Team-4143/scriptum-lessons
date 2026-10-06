#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"

if [ -d "$PROJECT/my-team" ]; then
	echo "Found the my-team folder."
	exit 0
fi
if [ -e "$PROJECT/my-team" ]; then
	echo "my-team is a file, not a folder. Delete it, then use New Folder."
	exit 1
fi
for d in "$PROJECT"/*/; do
	[ -d "$d" ] || continue
	name="$(basename "$d")"
	lower="$(printf '%s' "$name" | tr '[:upper:]' '[:lower:]' | tr ' _' '--')"
	if [ "$lower" = "my-team" ] || [ "$lower" = "myteam" ]; then
		echo "I found a folder called \"$name\", but it has to be named exactly my-team (small letters, with a dash)."
		exit 1
	fi
done
echo "I don't see a folder named my-team yet. Hover over the PROJECT bar in the Explorer and click the New Folder icon."
exit 1
