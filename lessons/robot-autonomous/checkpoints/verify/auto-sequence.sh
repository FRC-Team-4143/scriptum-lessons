#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
AUTOS="$PROJECT/src/main/java/frc/robot/autos/Autos.java"
if [[ ! -f "$AUTOS" ]]; then
	echo "Can't find Autos.java."
	exit 1
fi
# The file's code on one line, without comment lines, so formatting doesn't matter.
code="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$AUTOS" | sed -E 's://[^"]*$::' | tr '\n' ' ')"
has() { grep -Eq "$1" <<<"$code"; }

if ! has 'loadTrajectory[[:space:]]*\([[:space:]]*"ToPickup"' || ! has 'loadTrajectory[[:space:]]*\([[:space:]]*"ToScore"'; then
	echo "Load both paths first: loadTrajectory(\"ToPickup\") and loadTrajectory(\"ToScore\")."
	exit 1
fi
if ! has 'followPath[[:space:]]*\([^;]*getTrajectory[[:space:]]*\([[:space:]]*"ToPickup"'; then
	echo "Drive the first path with DrivetrainCommands.followPath(getTrajectory(\"ToPickup\"))."
	exit 1
fi
if ! has 'followPath[[:space:]]*\([^;]*getTrajectory[[:space:]]*\([[:space:]]*"ToScore"'; then
	echo "Drive the second path with DrivetrainCommands.followPath(getTrajectory(\"ToScore\"))."
	exit 1
fi
if ! has '(\.aim[[:space:]]*\(|DriveStates\.AIM)'; then
	echo "Turn to face the goal before shooting: add DrivetrainCommands.aim() to the routine."
	exit 1
fi
if ! has 'isAimed'; then
	echo "End the aim step when the robot is aimed: DrivetrainCommands.aim().until(DrivetrainSubsystem.getInstance()::isAimed)."
	exit 1
fi
if ! has '(ShooterCommands\.shoot[[:space:]]*\(|ShooterStates\.SHOOT)'; then
	echo "Finish the routine by shooting: add ShooterCommands.shoot() (with a timeout, so the routine ends)."
	exit 1
fi
# The order matters: pickup path, score path, aim, then shoot.
if ! has 'getTrajectory[[:space:]]*\([[:space:]]*"ToPickup".*getTrajectory[[:space:]]*\([[:space:]]*"ToScore".*(\.aim[[:space:]]*\(|DriveStates\.AIM).*isAimed.*(ShooterCommands\.shoot[[:space:]]*\(|ShooterStates\.SHOOT)'; then
	echo "Put the steps in order inside addCommands(...): ToPickup path, ToScore path, aim at the goal, then shoot (shooting before the robot is aimed misses)."
	exit 1
fi

echo "The routine drives both paths, aims, then shoots."
