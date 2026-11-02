#!/usr/bin/env bash
# Compiles the student's Robot.java against small stand-ins for WPILib and MWLib, then runs a hidden
# check that plays exact stick positions into teleopPeriodic() (a keyboard only gives full stick).
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

SRC="$PROJECT/src/main/java/frc/robot/Robot.java"
if [[ ! -f "$SRC" ]]; then
	echo "Can't find src/main/java/frc/robot/Robot.java."
	exit 1
fi

mkdir -p "$WORK/src" "$WORK/out"
cp -r "$HERE/challenge-stubs/." "$WORK/src/"
mkdir -p "$WORK/src/frc/robot"
cp "$SRC" "$WORK/src/frc/robot/"
cp "$HERE/DriverSticksCheck.java" "$WORK/src/"

cd "$WORK/src"
if ! javac -d ../out $(find . -name '*.java') >../compile.log 2>&1; then
	echo "Your Robot.java doesn't compile here: $(grep -m1 'error:' ../compile.log || tail -1 ../compile.log) (This check only knows Math, MathUtil and the drive_ and controller_ objects.)"
	exit 1
fi
cd "$WORK"
if ! java -cp out DriverSticksCheck >run.log 2>&1; then
	tail -1 run.log
	exit 1
fi
tail -1 run.log
