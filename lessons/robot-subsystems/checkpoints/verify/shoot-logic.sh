#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/src/main/java/frc/robot/subsystems/shooter/ShooterSubsystem.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find ShooterSubsystem.java."
	exit 1
fi
# Ignore comment lines so a commented-out answer doesn't count.
CODE="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$FILE" | tr '\n' ' ')"
if ! grep -Eq 'if[[:space:]]*\([[:space:]]*OI\.getShootButton[[:space:]]*\([[:space:]]*\)[[:space:]]*\)' <<<"$CODE"; then
	echo "In updateLogic(), write an if that checks OI.getShootButton()."
	exit 1
fi
if ! grep -Eq 'setTargetDutyCycle[[:space:]]*\([[:space:]]*CONSTANTS\.SHOOT_DUTY_CYCLE[[:space:]]*\)' <<<"$CODE"; then
	echo "While the shoot button is held, call flywheel.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE)."
	exit 1
fi
if ! grep -Eq 'else[[:space:]]*\{[^}]*flywheel\.setTargetDutyCycle[[:space:]]*\([[:space:]]*0(\.0)?[[:space:]]*\)' <<<"$CODE"; then
	echo "Add an else that calls flywheel.setTargetDutyCycle(0.0) so the flywheel stops when the button is released."
	exit 1
fi
echo "The shoot button logic is written."
