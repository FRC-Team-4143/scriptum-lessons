#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
cd "$PROJECT/03-merge" 2>/dev/null || { echo "The 03-merge folder is missing. Click Reset this exercise next to this checkpoint to get it back."; exit 1; }

if ! git merge-base --is-ancestor issue-7-auto-distance develop 2>/dev/null; then
	echo "issue-7-auto-distance hasn't been merged into develop yet."
	exit 1
fi

main_now="$(git rev-parse main)"
main_start="$(git rev-parse lesson-main-start)"
if [ "$main_now" != "$main_start" ]; then
	echo "main shouldn't change for this exercise - merge into develop, not main."
	exit 1
fi

if [ -n "$(git status --porcelain)" ]; then
	echo "You have uncommitted changes."
	exit 1
fi

echo "issue-7-auto-distance is merged into develop."
