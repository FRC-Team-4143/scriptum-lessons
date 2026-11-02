#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
cd "$PROJECT/06-push" 2>/dev/null || { echo "The 06-push folder is missing. Use Start over in the lesson chip."; exit 1; }

origin="$(git remote get-url origin 2>/dev/null || true)"
if [ -z "$origin" ] || [ ! -d "$origin" ]; then
	echo "This repository has lost its origin. Use Start over in the lesson chip."
	exit 1
fi

local_tip="$(git rev-parse issue-18-team-colors 2>/dev/null || true)"
if [ -z "$local_tip" ]; then
	echo "The issue-18-team-colors branch is missing."
	exit 1
fi
if [ "$local_tip" != "$(git rev-parse lesson-push-tip)" ]; then
	echo "Your branch has changed since the exercise started. Push it as it is."
	exit 1
fi

remote_tip="$(git --git-dir="$origin" rev-parse --verify -q refs/heads/issue-18-team-colors || true)"
if [ -z "$remote_tip" ]; then
	echo "origin doesn't have issue-18-team-colors yet. Pick 06-push in the bottom-left, then use the ... on the CHANGES heading in Source Control: Pull, Push, Push to..., origin."
	exit 1
fi
if [ "$remote_tip" != "$local_tip" ]; then
	echo "origin has issue-18-team-colors, but at a different commit than yours."
	exit 1
fi
echo "issue-18-team-colors is on origin."
