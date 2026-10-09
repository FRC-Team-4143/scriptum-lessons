#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
LOC="$PROJECT/src/main/java/frc/robot/subsystems/localization/LocalizationSubsystem.java"
if [[ ! -f "$LOC" ]]; then
	echo "Can't find LocalizationSubsystem.java."
	exit 1
fi
# The file's code on one line, without comments, so formatting doesn't matter.
CODE="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$LOC" | sed -E 's://.*$::' | tr '\n' ' ')"
# Just the arguments of the addVisionMeasurement(...) call (up to the first semicolon).
CALL="$(printf '%s' "$CODE" | grep -Eo '\.[[:space:]]*addVisionMeasurement[[:space:]]*\([^;]*;' | head -n 1 || true)"

if [[ -z "$CALL" ]]; then
	echo "In LocalizationSubsystem.updateLogic(), call pose_estimator_.addVisionMeasurement(...) for each measurement."
	exit 1
fi
if ! printf '%s' "$CALL" | grep -Eq 'getPose[[:space:]]*\('; then
	echo "Pass the pose the camera saw, measurement.getPose(), as the first argument."
	exit 1
fi
if ! printf '%s' "$CALL" | grep -Eq 'getTimestamp[[:space:]]*\('; then
	echo "Pass the time the PICTURE was taken, measurement.getTimestamp(), as the second argument (not the time now): the camera's picture is a little old."
	exit 1
fi
if ! printf '%s' "$CALL" | grep -Eq 'VecBuilder[[:space:]]*\.[[:space:]]*fill|VISION_XY_STD_METERS|VISION_HEADING_STD_RADIANS'; then
	echo "Pass the standard deviations as the third argument: VecBuilder.fill(x meters, y meters, heading radians), from LocalizationConstants."
	exit 1
fi
echo "The camera measurements go into the pose estimator."
