#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$HERE/_java-check.sh"
FILE="$PROJECT/src/main/java/frc/robot/RobotContainer.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find RobotContainer.java."
	exit 1
fi
# Comments are taken out so a commented-out registration doesn't count.
CODE="$(code_without_comments "$FILE")"
S='[[:space:]]*'
if grep -Eq "registerSubsystem$S\($S(ShooterSubsystem$S\.$S)?getInstance$S\($S\)$S\)" <<<"$CODE" &&
	grep -q 'ShooterSubsystem' <<<"$CODE"; then
	echo "The shooter is registered."
	exit 0
fi
# Also fine: keep the shooter in a variable first, then register the variable.
for name in $(grep -Eo "[A-Za-z_][A-Za-z0-9_]*$S=$S(new$S)?ShooterSubsystem$S\.$S?getInstance$S\(" <<<"$CODE" | grep -Eo '^[A-Za-z_][A-Za-z0-9_]*'); do
	if grep -Eq "registerSubsystem$S\($S(this$S\.$S)?$name$S\)" <<<"$CODE"; then
		echo "The shooter is registered."
		exit 0
	fi
done
echo "In RobotContainer, add: registerSubsystem(ShooterSubsystem.getInstance());"
exit 1
