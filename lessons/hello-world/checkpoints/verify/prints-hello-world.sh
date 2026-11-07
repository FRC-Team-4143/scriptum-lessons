#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

cp "$PROJECT"/src/*.java "$WORK"/ 2>/dev/null || true
cp "$HERE/HelloWorldCheck.java" "$WORK"/

cd "$WORK"
if ! javac ./*.java > compile.log 2>&1; then
	if grep -q "';' expected" compile.log; then
		echo "Your code doesn't compile: a ; is missing. Every Java statement ends with a ; so add one at the end of the println line."
		exit 1
	fi
	echo "Your code doesn't compile: $(grep -m1 'error:' compile.log || tail -1 compile.log)"
	exit 1
fi
if ! java HelloWorldCheck > run.log 2>&1; then
	tail -1 run.log
	exit 1
fi
tail -1 run.log
