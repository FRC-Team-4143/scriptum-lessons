#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/src/main/java/frc/robot/RobotContainer.java"
AUTOS="$PROJECT/src/main/java/frc/robot/autos/Autos.java"
for f in "$FILE" "$AUTOS"; do
	if [[ ! -f "$f" ]]; then
		echo "Can't find $(basename "$f")."
		exit 1
	fi
done
code() { grep -Ev '^[[:space:]]*(//|\*|/\*)' "$1" | tr '\n' ' '; }

if ! code "$FILE" | grep -Eq 'addOption[[:space:]]*\([[:space:]]*"[^"]+"[[:space:]]*,[[:space:]]*Autos\.leftAuto\(\)'; then
	echo "In RobotContainer add: autoChooser.addOption(\"Left Auto\", Autos.leftAuto());"
	exit 1
fi
if ! code "$FILE" | grep -Eq 'addOption[[:space:]]*\([[:space:]]*"[^"]+"[[:space:]]*,[[:space:]]*Autos\.rightAuto\(\)'; then
	echo "In RobotContainer add: autoChooser.addOption(\"Right Auto\", Autos.rightAuto());"
	exit 1
fi
if ! code "$FILE" | grep -Eq 'addOption[[:space:]]*\([[:space:]]*"[^"]+"[[:space:]]*,[[:space:]]*Autos\.squareAuto\(\)'; then
	echo "In RobotContainer add: autoChooser.addOption(\"Square Auto\", Autos.squareAuto());"
	exit 1
fi
for name in leftAuto rightAuto; do
	if ! code "$AUTOS" | grep -Eq "$name[[:space:]]*\\([[:space:]]*\\)[[:space:]]*\\{[^}]*sequence"; then
		echo "$name() in Autos.java should return Commands.sequence(...)."
		exit 1
	fi
done
echo "All three autos are built and in the chooser."
