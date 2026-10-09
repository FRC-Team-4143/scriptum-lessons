#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
cd "$PROJECT/07-pull"

origin="$(git remote get-url origin 2>/dev/null || true)"
if [ -z "$origin" ] || [ ! -d "$origin" ]; then
	echo "This repository has lost its origin. Reload the lesson to start over."
	exit 1
fi

if [ "$(git rev-parse develop)" = "$(git rev-parse lesson-develop-before)" ]; then
	echo "Your develop still doesn't have your teammate's commit. Make sure you're on develop, then use the ... menu in Source Control: Pull, Push, Pull."
	exit 1
fi

remote_tip="$(git --git-dir="$origin" rev-parse refs/heads/develop)"
if ! git merge-base --is-ancestor "$remote_tip" develop 2>/dev/null; then
	echo "develop is missing the newest commit from origin. Use the ... menu in Source Control: Pull, Push, Pull."
	exit 1
fi
if ! grep -q "Saturday" Schedule.txt 2>/dev/null && ! git show develop:Schedule.txt | grep -q "Saturday"; then
	echo "Your teammate's Saturday practice line isn't on develop."
	exit 1
fi
echo "develop is up to date with origin."
