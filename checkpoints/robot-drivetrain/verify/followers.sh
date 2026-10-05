#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
SRC="$PROJECT/src/main/java/frc/robot/Constants.java"

if [[ ! -f "$SRC" ]]; then
	echo "Can't find src/main/java/frc/robot/Constants.java."
	exit 1
fi

# Count the motor(...) calls inside one "NAME = List.of( ... );" statement.
count_motors() {
	tr '\n' ' ' <"$SRC" | grep -o "$1 = List.of([^;]*;" | grep -o 'motor(' | wc -l | tr -d ' '
}

left="$(count_motors LEFT_MOTORS)"
right="$(count_motors RIGHT_MOTORS)"

if [[ "$left" -lt 2 ]]; then
	echo "LEFT_MOTORS has $left motor - add a second one (id 2) as a follower."
	exit 1
fi
if [[ "$right" -lt 2 ]]; then
	echo "RIGHT_MOTORS has $right motor - add a second one (id 4, inverted true) as a follower."
	exit 1
fi
echo "Both sides have a leader and a follower."
