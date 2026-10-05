#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/src/main/java/frc/robot/OI.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find OI.java."
	exit 1
fi
CODE="$(grep -Ev '^[[:space:]]*//' "$FILE" | tr '\n' ' ')"
if ! grep -Eq 'getShootButton[[:space:]]*\([[:space:]]*\)[[:space:]]*\{[^}]*getRightBumperButton' <<<"$CODE"; then
	echo "getShootButton() should return driverController.getRightBumperButton()."
	exit 1
fi
if ! grep -Eq 'getIndexButton[[:space:]]*\([[:space:]]*\)[[:space:]]*\{[^}]*getLeftBumperButton' <<<"$CODE"; then
	echo "getIndexButton() should return driverController.getLeftBumperButton()."
	exit 1
fi
echo "Both shooter buttons are wired up."
