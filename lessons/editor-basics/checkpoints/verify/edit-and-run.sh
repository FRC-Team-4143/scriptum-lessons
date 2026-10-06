#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

cp "$PROJECT"/src/*.java "$WORK"/ 2>/dev/null || true
cd "$WORK"
if ! javac ./*.java > compile.log 2>&1; then
	echo "Your program doesn't run yet: $(grep -m1 'error:' compile.log || tail -1 compile.log)"
	exit 1
fi
OUT="$(java Main 2>&1 | head -1)"
case "$OUT" in
	"Hello, my name is ..."*) echo "It still says the three dots. Replace them with your name and press Ctrl+S."; exit 1 ;;
	"Hello, my name is "?*) echo "$OUT" ;;
	*) echo "The program should print: Hello, my name is <your name>. It printed: $OUT"; exit 1 ;;
esac
