#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/src/main/java/frc/robot/Robot.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find Robot.java."
	exit 1
fi
CODE="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$FILE" | tr '\n' ' ')"
NAME="$(grep -Eo 'private[[:space:]]+static[[:space:]]+final[[:space:]]+double[[:space:]]+[A-Z][A-Z0-9_]*' <<<"$CODE" | awk '{print $NF}' | head -1 || true)"
if [[ -z "$NAME" ]]; then
	echo "Declare a constant near the top of the class: private static final double DEADBAND = 0.1;"
	exit 1
fi
if [[ "$(grep -Eo "if[[:space:]]*\\([[:space:]]*Math\\.abs\\([^)]*\\)[[:space:]]*<[[:space:]]*$NAME" <<<"$CODE" | wc -l | tr -d ' ')" -lt 2 ]]; then
	echo "Use the deadband on both sticks: if (Math.abs(forward) < $NAME) { forward = 0.0; } and the same for turn."
	exit 1
fi
if ! grep -Eq 'if[[:space:]]*\([^)]*(distanceMeters|getDistance)[^)]*<' <<<"$CODE"; then
	echo "In autonomousPeriodic() use an if / else on distanceMeters for bang-bang control."
	exit 1
fi
echo "The deadband constant, both if statements and the bang-bang if/else are in place."
