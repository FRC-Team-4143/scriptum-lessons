#!/usr/bin/env bash
# Runs the student's ShooterSubsystem.updateLogic() with the shoot button pressed and released and
# checks what the flywheel is told, so any logic that behaves right passes (if/else, a ternary,
# setting 0.0 first, ...). See stubs/README.md.
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$HERE/_java-check.sh"
DIR="$PROJECT/src/main/java/frc/robot/subsystems/shooter"
FILE="$DIR/ShooterSubsystem.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find ShooterSubsystem.java."
	exit 1
fi

status=0
OWN_NAMES="ShooterConstants|ConstantsType|class ShooterSubsystem"
out="$(java_check ShootLogicCheck shoot-stubs "$FILE" "$DIR/ShooterConstants.java")" || status=$?
if [[ "$out" == *"(variable OI)"* ]]; then
	echo "ShooterSubsystem.java can't see OI yet. Add import frc.robot.OI; at the top, next to the other imports."
	exit 1
fi
if [[ $status -ne 2 ]]; then
	echo "$out"
	exit $status
fi

# The code uses something the check can't run here. Fall back to looking for the pieces.
CODE="$(code_without_comments "$FILE")"
if ! grep -Eq 'getShootButton[[:space:]]*\(' <<<"$CODE"; then
	echo "In updateLogic(), check OI.getShootButton()."
	exit 1
fi
if ! grep -Eq 'flywheel_\.setTargetDutyCycle[[:space:]]*\(' <<<"$CODE" || ! grep -q 'SHOOT_DUTY_CYCLE' <<<"$CODE"; then
	echo "While the shoot button is held, call flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE)."
	exit 1
fi
if ! grep -Eq '[^0-9.]0(\.0*)?[dD]?[[:space:]]*[);:]' <<<"$CODE"; then
	echo "When the shoot button is released, set the flywheel's duty cycle to 0.0 so it stops."
	exit 1
fi
echo "The shoot button logic is written."
