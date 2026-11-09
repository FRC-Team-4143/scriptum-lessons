#!/usr/bin/env bash
# Compiles the student's FlywheelPid.java (plain Java) with a hidden check that tests the math on
# exact numbers, then runs it against a model of the real flywheel.
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
. "$HERE/_code.sh"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

SRC="$PROJECT/src/main/java/frc/robot/subsystems/shooter/FlywheelPid.java"
if [[ ! -f "$SRC" ]]; then
	echo "Create subsystems/shooter/FlywheelPid.java, next to ShooterSubsystem.java."
	exit 1
fi

mkdir -p "$WORK/frc/robot/subsystems/shooter" "$WORK/out"
cp "$SRC" "$WORK/frc/robot/subsystems/shooter/"
cp "$HERE/OwnPidCheck.java" "$WORK/"
cd "$WORK"
if ! javac -d out frc/robot/subsystems/shooter/FlywheelPid.java OwnPidCheck.java >compile.log 2>&1; then
	echo "FlywheelPid.java doesn't compile with the check: $(grep -m1 'error:' compile.log || tail -1 compile.log)"
	exit 1
fi
if ! java -cp out OwnPidCheck >run.log 2>&1; then
	tail -1 run.log
	exit 1
fi

SUBSYSTEM_FILE="$(SUBSYSTEM "$PROJECT")"
CODE="$(flat "$SUBSYSTEM_FILE")"
if ! grep -Eq 'new[[:space:]]+FlywheelPid[[:space:]]*\(' <<<"$CODE" || ! grep -Eq '\.calculate[[:space:]]*\(' <<<"$CODE"; then
	echo "FlywheelPid works, but ShooterSubsystem doesn't use it yet. Make one with new FlywheelPid(...) and call its calculate(...) from runFlywheel()."
	exit 1
fi
if ! grep -Eq '\.reset[[:space:]]*\(' <<<"$CODE"; then
	echo "FlywheelPid works, but ShooterSubsystem never calls its reset(). Reset it whenever the shoot button is up, so old error doesn't carry into the next shot."
	exit 1
fi
tail -1 run.log
