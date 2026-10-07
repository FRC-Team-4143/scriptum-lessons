#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/src/main/java/frc/robot/autos/Autos.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find Autos.java."
	exit 1
fi
CODE="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$FILE" | tr '\n' ' ')"
BODY="$(grep -o 'Command[[:space:]]\+squareAuto()[^{]*{.*' <<<"$CODE" || true)"
if ! grep -Eq 'for[[:space:]]*\(' <<<"$BODY"; then
	echo "squareAuto() should use a for loop instead of writing the same commands four times."
	exit 1
fi
if ! grep -q 'addCommands' <<<"$BODY"; then
	echo "Inside the loop, add the commands to the group with group.addCommands(...)."
	exit 1
fi
if ! grep -Eq 'TurnToAngleCommand\([^)]*\b[a-z]\b' <<<"$BODY"; then
	echo "Turn to 90.0 * i degrees so each turn is 90 degrees further than the last."
	exit 1
fi
echo "squareAuto() uses a loop."
