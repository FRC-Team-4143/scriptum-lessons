#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/src/main/java/frc/robot/Robot.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find Robot.java."
	exit 1
fi
CONSTANTS="$PROJECT/src/main/java/frc/robot/Constants.java"
if [[ ! -f "$CONSTANTS" ]]; then
	echo "Can't find Constants.java."
	exit 1
fi
CODE="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$FILE" | tr '\n' ' ')"
CONSTANTS_CODE="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$CONSTANTS" | tr '\n' ' ')"
NAME="$(grep -Eo 'static[[:space:]]+final[[:space:]]+double[[:space:]]+DEADBAND[[:space:]]*=' <<<"$CONSTANTS_CODE" | head -1 || true)"
if [[ -z "$NAME" ]]; then
	echo "Add the constant to Constants.java: public static final double DEADBAND = 0.1;"
	exit 1
fi
if [[ "$(grep -Eo "if[[:space:]]*\\([[:space:]]*Math\\.abs\\([^)]*\\)[[:space:]]*<[[:space:]]*Constants\\.DEADBAND" <<<"$CODE" | wc -l | tr -d ' ')" -lt 2 ]]; then
	echo "Use the deadband on both sticks: if (Math.abs(forward) < Constants.DEADBAND) { forward = 0.0; } and the same for turn."
	exit 1
fi
if ! grep -Eq 'if[[:space:]]*\([^)]*(distanceMeters|getDistance)[^)]*<' <<<"$CODE"; then
	echo "In autonomousPeriodic() use an if / else on distanceMeters for bang-bang control."
	exit 1
fi
echo "The DEADBAND constant in Constants.java, both if statements and the bang-bang if/else are in place."
