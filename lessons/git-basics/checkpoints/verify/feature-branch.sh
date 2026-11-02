#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
cd "$PROJECT/02-feature-branch" 2>/dev/null || { echo "The 02-feature-branch folder is missing. Use Start over in the lesson chip."; exit 1; }

branch="$(git for-each-ref --format='%(refname:short)' refs/heads \
	| grep -E '^issue-12-[a-z0-9]+(-[a-z0-9]+)*$' | head -n1 || true)"
if [ -z "$branch" ]; then
	near="$(git for-each-ref --format='%(refname:short)' refs/heads \
		| grep -iE '^issue-?12([^0-9]|$)' | head -n1 || true)"
	if [ -n "$near" ]; then
		echo "$near doesn't count: branch names are all lowercase letters, numbers and hyphens, like issue-12-turbo-boost. Pick 02-feature-branch in the bottom-left, then rename it with the ... on the CHANGES heading: Branch, Rename Branch..."
		exit 1
	fi
	echo "No branch named issue-12-<short-description> found."
	exit 1
fi

if ! git merge-base --is-ancestor develop "$branch" 2>/dev/null; then
	echo "Branch $branch doesn't start from develop."
	exit 1
fi

ahead="$(git rev-list --count "develop..$branch")"
if [ "$ahead" -lt 2 ]; then
	echo "Branch $branch has $ahead commit(s) so far - it needs at least 2."
	exit 1
fi

main_now="$(git rev-parse main)"
main_start="$(git rev-parse lesson-main-start)"
if [ "$main_now" != "$main_start" ]; then
	echo "main shouldn't change while working on a feature branch."
	exit 1
fi

develop_now="$(git rev-parse develop)"
if [ "$develop_now" != "$main_start" ]; then
	echo "develop shouldn't change either - branch off it, don't commit to it."
	exit 1
fi

echo "Feature branch $branch looks good."
