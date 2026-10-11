#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
CONTAINER="$PROJECT/src/main/java/frc/robot/RobotContainer.java"
if [[ ! -f "$CONTAINER" ]]; then
	echo "Can't find RobotContainer.java."
	exit 1
fi
# The code of handleStateTransition, on one line and without comments.
code="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$CONTAINER" | sed -E 's://.*$::' | tr '\n' ' ')"
transition="$(sed -E 's/.*handleStateTransition[[:space:]]*\(//; s/(protected|public)[^(]*updateLogic[[:space:]]*\(.*//' <<<"$code")"
if [[ "$transition" == "$code" ]]; then
	echo "Can't find handleStateTransition in RobotContainer.java."
	exit 1
fi
if ! grep -qE 'system_state_[[:space:]]*=[[:space:]]*RobotStates\.AIMING' <<<"$transition"; then
	echo "In handleStateTransition, the AIM case should put the robot in the step state AIMING while it is still turning: system_state_ = RobotStates.AIMING;"
	exit 1
fi
if ! grep -qE 'isAimed[[:space:]]*\(' <<<"$transition"; then
	echo "The AIM case has to decide between AIMING and AIM. Use drive_.isAimed() to tell whether the robot already faces the goal."
	exit 1
fi
if ! grep -qE 'system_state_[[:space:]]*=[[:space:]]*RobotStates\.AIM[^A-Z_]' <<<"$transition"; then
	echo "Once the robot is aimed, the AIM case should move it to AIM itself: system_state_ = RobotStates.AIM;"
	exit 1
fi
echo "AIM turns first (AIMING) and only reaches AIM once the robot is aimed."
