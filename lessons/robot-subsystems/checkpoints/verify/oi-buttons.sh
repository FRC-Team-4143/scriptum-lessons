#!/usr/bin/env bash
# Presses each bumper on a stand-in controller and checks what the student's OI.java reports, so any
# way of reading the bumpers passes. See stubs/README.md.
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$HERE/_java-check.sh"
FILE="$PROJECT/src/main/java/frc/robot/OI.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find OI.java."
	exit 1
fi

status=0
OWN_NAMES="class OI"
java_check OiButtonsCheck "" "$FILE" || status=$?
[[ $status -ne 2 ]] && exit $status

# The code uses something the check can't run here. Fall back to looking for the pieces.
CODE="$(code_without_comments "$FILE")"
if ! grep -Eq 'getShootButton[[:space:]]*\([[:space:]]*\)[[:space:]]*\{[^}]*(getRightBumper(Button)?[[:space:]]*\(|getRawButton[[:space:]]*\([[:space:]]*6)' <<<"$CODE"; then
	echo "getShootButton() should return driver_controller_.getRightBumperButton()."
	exit 1
fi
if ! grep -Eq 'getIndexButton[[:space:]]*\([[:space:]]*\)[[:space:]]*\{[^}]*(getLeftBumper(Button)?[[:space:]]*\(|getRawButton[[:space:]]*\([[:space:]]*5)' <<<"$CODE"; then
	echo "getIndexButton() should return driver_controller_.getLeftBumperButton()."
	exit 1
fi
echo "Both shooter buttons are wired up."
