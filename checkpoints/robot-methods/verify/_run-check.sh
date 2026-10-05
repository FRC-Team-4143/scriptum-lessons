#!/usr/bin/env bash
# Shared by the robot-methods verifiers: compiles the student's DriveMath.java together with one
# hidden check class (pure Java, no WPILib needed) and runs it.
#   usage: _run-check.sh <projectDir> <CheckClassName>
set -euo pipefail
PROJECT="$1"
CHECK="$2"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

SRC="$PROJECT/src/main/java/frc/robot/DriveMath.java"
if [[ ! -f "$SRC" ]]; then
	echo "Can't find src/main/java/frc/robot/DriveMath.java."
	exit 1
fi

mkdir -p "$WORK/frc/robot" "$WORK/out"
cp "$SRC" "$WORK/frc/robot/"
cp "$HERE/$CHECK.java" "$WORK/"

cd "$WORK"
if ! javac -d out frc/robot/DriveMath.java "$CHECK.java" >compile.log 2>&1; then
	echo "Your code doesn't compile: $(grep -m1 'error:' compile.log || tail -1 compile.log)"
	exit 1
fi
if ! java -cp out "$CHECK" >run.log 2>&1; then
	tail -1 run.log
	exit 1
fi
tail -1 run.log
