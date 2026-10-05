#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/src/main/java/frc/robot/RobotContainer.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find RobotContainer.java."
	exit 1
fi
# Ignore comment lines so a commented-out registration doesn't count.
if grep -Ev '^[[:space:]]*//' "$FILE" | tr '\n' ' ' | grep -Eq 'registerSubsystem[[:space:]]*\([[:space:]]*ShooterSubsystem\.getInstance\(\)[[:space:]]*\)'; then
	echo "The shooter is registered."
	exit 0
fi
echo "In RobotContainer, add: registerSubsystem(ShooterSubsystem.getInstance());"
exit 1
