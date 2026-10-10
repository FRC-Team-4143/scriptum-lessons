#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/src/main/java/frc/robot/Robot.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find src/main/java/frc/robot/Robot.java."
	exit 1
fi
# The code on one line, without comment lines.
CODE="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$FILE" | tr '\n' ' ')"

if [[ "$(grep -Eo '\bdouble[[:space:]]+[A-Za-z_][A-Za-z0-9_]*[[:space:]]*=' <<<"$CODE" | wc -l | tr -d ' ')" -lt 3 ]]; then
	echo "Declare your own double variables in teleopPeriodic(), for example forward, turn, left_speed and right_speed."
	exit 1
fi
if ! grep -q 'getLeftY()' <<<"$CODE" || ! grep -q 'getLeftX()' <<<"$CODE"; then
	echo "Arcade drive reads forward from controller_.getLeftY() and turn from controller_.getLeftX()."
	exit 1
fi
if ! grep -Eq '=[[:space:]]*[A-Za-z_][A-Za-z0-9_]*[[:space:]]*\+[[:space:]]*[A-Za-z_]' <<<"$CODE" || ! grep -Eq '=[[:space:]]*[A-Za-z_][A-Za-z0-9_]*[[:space:]]*-[[:space:]]*[A-Za-z_]' <<<"$CODE"; then
	echo "Use arithmetic for arcade drive: left_speed = forward + turn; right_speed = forward - turn;"
	exit 1
fi
if ! grep -Eq 'setDutyCycles\([[:space:]]*[A-Za-z]' <<<"$CODE"; then
	echo "Finish with drive_.setDutyCycles(left_speed, right_speed);"
	exit 1
fi
echo "Your variables and arcade drive are in place."
