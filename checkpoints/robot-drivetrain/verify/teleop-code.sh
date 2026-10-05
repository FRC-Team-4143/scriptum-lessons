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
	echo "Declare your own double variables in teleopPeriodic(), for example forward, turn, leftSpeed and rightSpeed."
	exit 1
fi
if ! grep -q 'getLeftY()' <<<"$CODE" || ! grep -q 'getRightX()' <<<"$CODE"; then
	echo "Arcade drive reads forward from controller.getLeftY() and turn from controller.getRightX()."
	exit 1
fi
if ! grep -Eq '=[[:space:]]*[A-Za-z_][A-Za-z0-9_]*[[:space:]]*\+[[:space:]]*[A-Za-z_]' <<<"$CODE" || ! grep -Eq '=[[:space:]]*[A-Za-z_][A-Za-z0-9_]*[[:space:]]*-[[:space:]]*[A-Za-z_]' <<<"$CODE"; then
	echo "Use arithmetic for arcade drive: leftSpeed = forward + turn; rightSpeed = forward - turn;"
	exit 1
fi
if ! grep -Eq 'setDutyCycles\([[:space:]]*[A-Za-z]' <<<"$CODE"; then
	echo "Finish with drive.setDutyCycles(leftSpeed, rightSpeed);"
	exit 1
fi
echo "Your variables and arcade drive are in place."
