#!/usr/bin/env bash
set -euo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
. "$HERE/_code.sh"
FILE="$(SUBSYSTEM "$1")"
[[ -f "$FILE" ]] || { echo "Can't find ShooterSubsystem.java."; exit 1; }
BODY="$(flat "$FILE" | grep -o 'private void bangBang()[^{]*{.*private void feedforward()' || true)"
if ! grep -Eq 'if[[:space:]]*\(' <<<"$BODY" || ! grep -q 'SHOOT_VELOCITY' <<<"$BODY"; then
	echo "bangBang() should compare flywheel.getCurrentVelocity() with CONSTANTS.SHOOT_VELOCITY in an if / else."
	exit 1
fi
if ! grep -Eq 'setTargetDutyCycle\(1\.0\)' <<<"$BODY"; then
	echo "When the flywheel is too slow, bangBang() should call flywheel.setTargetDutyCycle(1.0)."
	exit 1
fi
if ! grep -Eq 'setTargetDutyCycle\(0\.0\)' <<<"$BODY"; then
	echo "Otherwise bangBang() should call flywheel.setTargetDutyCycle(0.0)."
	exit 1
fi
echo "bangBang() is written."
