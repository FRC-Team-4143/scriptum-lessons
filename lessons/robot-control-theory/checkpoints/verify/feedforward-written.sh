#!/usr/bin/env bash
set -euo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
. "$HERE/_code.sh"
FILE="$(SUBSYSTEM "$1")"
[[ -f "$FILE" ]] || { echo "Can't find ShooterSubsystem.java."; exit 1; }
BODY="$(flat "$FILE" | grep -o 'private void feedforward()[^{]*{[^}]*}' || true)"
if ! grep -q 'FLYWHEEL_KV' <<<"$BODY"; then
	echo "feedforward() should use CONSTANTS.FLYWHEEL_KV."
	exit 1
fi
if ! grep -q 'SHOOT_VELOCITY' <<<"$BODY"; then
	echo "feedforward() should start from CONSTANTS.SHOOT_VELOCITY."
	exit 1
fi
if ! grep -Eq 'setTargetDutyCycle\([[:space:]]*[^0)[:space:]]' <<<"$BODY"; then
	echo "feedforward() should pass the power it worked out to flywheel.setTargetDutyCycle(...)."
	exit 1
fi
echo "feedforward() is written."
