#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

# Everything but Main, so a bug this checkpoint isn't about (like the crash or the
# endless loop) can't get in its way.
cp "$PROJECT"/src/*.java "$WORK"/ 2>/dev/null || true
rm -f "$WORK/Main.java"
cp "$HERE/FirstBreakpointCheck.java" "$WORK"/

cd "$WORK"
if ! javac ./*.java > compile.log 2>&1; then
	echo "Your code doesn't compile: $(grep -m1 'error:' compile.log || tail -1 compile.log)"
	exit 1
fi
set +e
timeout 5 java FirstBreakpointCheck > run.log 2>&1
status=$?
set -e
if [ "$status" -eq 124 ]; then
	echo "Your code was still running after 5 seconds, so something is stuck in a loop. Debug Main and press Pause to see where."
	exit 1
fi
tail -1 run.log
exit "$status"
