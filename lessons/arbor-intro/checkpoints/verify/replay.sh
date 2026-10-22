#!/usr/bin/env bash
# The answers in sample-logs/Answers.java, from replaying sample-logs/ReplayDemo.btlog.
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ANSWERS="$PROJECT/sample-logs/Answers.java"
if [ ! -f "$ANSWERS" ]; then
	echo "sample-logs/Answers.java is missing. Reset the lesson to get it back (your trees are kept in trees/, but copy them somewhere first)."
	exit 1
fi
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT
cp "$ANSWERS" "$HERE/ReplayCheck.java" "$WORK"/

cd "$WORK"
if ! javac ./*.java > compile.log 2>&1; then
	echo "Answers.java doesn't compile: $(grep -m1 'error:' compile.log || tail -1 compile.log). Check you only changed the values after the = signs."
	exit 1
fi
set +e
timeout 5 java ReplayCheck > run.log 2>&1
status=$?
set -e
tail -1 run.log
exit "$status"
