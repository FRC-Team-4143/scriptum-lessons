#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DIR="$PROJECT/src/main/java/frc/robot"
OI="$DIR/OI.java"
CMDS="$DIR/subsystems/shooter/ShooterCommands.java"
for f in "$OI" "$CMDS"; do
	if [[ ! -f "$f" ]]; then
		echo "Can't find $(basename "$f")."
		exit 1
	fi
done
code() { grep -Ev '^[[:space:]]*(//|\*|/\*)' "$1" | tr '\n' ' '; }

if ! code "$OI" | grep -Eq 'rightBumper\(\)[[:space:]]*\.whileTrue\([[:space:]]*ShooterCommands\.shoot\(\)'; then
	echo "In OI.configureBindings(): driverController.rightBumper().whileTrue(ShooterCommands.shoot());"
	exit 1
fi
if ! code "$OI" | grep -Eq '\ba\(\)[[:space:]]*\.onTrue\([[:space:]]*ShooterCommands\.simulateBallLaunch\(\)'; then
	echo "In OI.configureBindings(): driverController.a().onTrue(ShooterCommands.simulateBallLaunch());"
	exit 1
fi
if ! code "$CMDS" | grep -q 'startEnd'; then
	echo "ShooterCommands.shoot() should return a Commands.startEnd(...) command."
	exit 1
fi
if ! code "$CMDS" | grep -q 'runOnce'; then
	echo "ShooterCommands.simulateBallLaunch() should return a Commands.runOnce(...) command."
	exit 1
fi
echo "Both buttons are bound to shooter commands."
