#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT
# shellcheck source=mutation.sh
source "$HERE/mutation.sh"

# First the tests: they pass on a correct isAtSpeed and fail on the starter's
# buggy one (kept as mutant find-the-bug-1).
if ! (mutation_check "$PROJECT" ShooterMathTest ShooterMath isAtSpeed find-the-bug-1) > "$WORK/tests.log"; then
	tail -1 "$WORK/tests.log"
	exit 1
fi

# Then the fix: the project's own ShooterMath must now be right.
mkdir -p "$WORK/fixed"
cp "$PROJECT/src/ShooterMath.java" "$HERE/ShooterFixedCheck.java" "$WORK/fixed/"
cd "$WORK/fixed"
if ! javac ./*.java > compile.log 2>&1; then
	echo "ShooterMath.java doesn't compile: $(grep -m1 'error:' compile.log | sed 's|^\./||')"
	exit 1
fi
if ! java -cp . ShooterFixedCheck > run.log 2>&1; then
	tail -1 run.log
	exit 1
fi
tail -1 run.log
