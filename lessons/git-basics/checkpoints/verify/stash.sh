#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
cd "$PROJECT/08-stash"

# 1. The typo fix is a commit on develop.
if [ "$(git rev-list --count lesson-main-start..develop)" -lt 1 ]; then
	echo "develop doesn't have your typo fix yet. Stash your work, switch to develop, fix \"Teh\" and commit."
	exit 1
fi
if git show develop:Notes.md | grep -q "Teh"; then
	echo "develop still says \"Teh\". Fix the typo on develop and commit it."
	exit 1
fi

# 2. Back on the work branch, with no commit added to it.
branch="$(git rev-parse --abbrev-ref HEAD)"
if [ "$branch" != "issue-21-new-motor" ]; then
	echo "Switch back to issue-21-new-motor (you're on $branch)."
	exit 1
fi
if [ "$(git rev-parse HEAD)" != "$(git rev-parse lesson-issue-tip)" ]; then
	echo "Your unfinished work should stay uncommitted. Use git stash, not git commit."
	exit 1
fi

# 3. The shelf is empty and the work is back.
if [ -n "$(git stash list)" ]; then
	echo "Your work is still on the shelf. Bring it back with: git stash pop"
	exit 1
fi
if ! grep -q "WIP" Notes.md; then
	echo "Your unfinished change (the WIP line in Notes.md) is gone. If it's in the stash, run: git stash pop"
	exit 1
fi
if git diff --quiet HEAD -- Notes.md; then
	echo "Notes.md has no unfinished changes. They should be back after git stash pop."
	exit 1
fi
echo "Stashed, fixed develop, and popped your work back."
