#!/usr/bin/env bash
set -euo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
. "$HERE/_code.sh"
FILE="$(CONSTANTS "$1")"
[[ -f "$FILE" ]] || { echo "Can't find ShooterConstants.java."; exit 1; }
CODE="$(flat "$FILE")"
if ! grep -Eq 'FLYWHEEL_CONTROL[[:space:]]*=[[:space:]]*FlywheelControl\.PID' <<<"$CODE"; then
	echo "Finish with FLYWHEEL_CONTROL = FlywheelControl.PID."
	exit 1
fi
kv="$(grep -Eo 'FLYWHEEL_KV[[:space:]]*=[[:space:]]*[0-9.]+' <<<"$CODE" | grep -Eo '[0-9.]+$' || echo 0)"
kp="$(grep -Eo 'FLYWHEEL_KP[[:space:]]*=[[:space:]]*[0-9.]+' <<<"$CODE" | grep -Eo '[0-9.]+$' || echo 0)"
if ! awk -v kv="$kv" 'BEGIN { exit !(kv >= 0.05) }'; then
	echo "FLYWHEEL_KV is $kv - feedforward does most of the work, so it needs to be bigger. Try 12 divided by the top speed in rotations per second."
	exit 1
fi
if ! awk -v kp="$kp" 'BEGIN { exit !(kp > 0) }'; then
	echo "FLYWHEEL_KP is $kp - PID needs some feedback (kP greater than 0)."
	exit 1
fi
echo "PID is selected with kV = $kv and kP = $kp."
