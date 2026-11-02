#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
cd "$PROJECT/07-pull" 2>/dev/null || { echo "The 07-pull folder is missing. Use Start over in the lesson chip."; exit 1; }

origin="$(git remote get-url origin 2>/dev/null || true)"
if [ -z "$origin" ] || [ ! -d "$origin" ]; then
	echo "This repository has lost its origin. Use Start over in the lesson chip."
	exit 1
fi

if [ "$(git rev-parse develop)" = "$(git rev-parse lesson-develop-before)" ]; then
	echo "Your develop still doesn't have your teammate's commit. Pick 07-pull in the bottom-left, make sure you're on develop, then use the ... menu on its row in Source Control: Pull, Push, Pull."
	exit 1
fi

remote_tip="$(git --git-dir="$origin" rev-parse refs/heads/develop)"
if ! git merge-base --is-ancestor "$remote_tip" develop 2>/dev/null; then
	echo "develop is missing the newest commit from origin. Use the ... menu on the 07-pull row in Source Control: Pull, Push, Pull."
	exit 1
fi
if ! grep -q "Saturday" Schedule.txt 2>/dev/null && ! git show develop:Schedule.txt | grep -q "Saturday"; then
	echo "Your teammate's Saturday practice line isn't on develop."
	exit 1
fi
echo "develop is up to date with origin."
