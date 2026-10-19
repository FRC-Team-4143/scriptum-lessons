#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

# Everything but Main, so an unfinished part of the lesson this checkpoint
# doesn't use can't stop it compiling.
cp "$PROJECT"/src/*.java "$WORK"/ 2>/dev/null || true
rm -f "$WORK/Main.java"

cd "$WORK"
if ! javac ./*.java > compile.log 2>&1; then
	echo "Your code doesn't compile: $(grep -m1 'error:' compile.log || tail -1 compile.log)"
	exit 1
fi

# Your code compiles. Now the check uses it with types of its own, which only
# works once it is generic.
cp "$HERE/MaxCheck.java" "$WORK"/
if ! javac ./*.java > check.log 2>&1; then
	echo "The check couldn't call max with a List<Integer> and get an Integer back. Is it declared public static <T extends Comparable<T>> T max(List<T> items)? (javac: $(grep -m1 'error:' check.log | sed 's/^.*error: //' || tail -1 check.log))"
	exit 1
fi
if ! java MaxCheck > run.log 2>&1; then
	tail -1 run.log
	exit 1
fi
tail -1 run.log
