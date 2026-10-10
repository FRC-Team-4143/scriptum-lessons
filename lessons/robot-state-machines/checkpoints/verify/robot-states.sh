#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DIR="$PROJECT/src/main/java/frc/robot"
OI="$DIR/OI.java"
RC="$DIR/RobotContainer.java"
for f in "$OI" "$RC"; do
	if [[ ! -f "$f" ]]; then
		echo "Can't find $(basename "$f")."
		exit 1
	fi
done
code() { grep -Ev '^[[:space:]]*(//|\*|/\*)' "$1" | tr '\n' ' '; }

# RobotContainer.updateLogic(): a switch on the robot state that asks the subsystems for states.
if ! code "$RC" | grep -Eq 'switch[[:space:]]*\([[:space:]]*(getSystemState\(\)|system_state_)[[:space:]]*\)'; then
	echo "In RobotContainer.updateLogic(): write a switch (getSystemState()) with a case for each robot state."
	exit 1
fi
for state in IDLE DRIVE SHOOT; do
	if ! code "$RC" | grep -Eq "case[[:space:]]+(RobotStates\.)?$state\b"; then
		echo "RobotContainer's switch needs a case $state."
		exit 1
	fi
done
if ! code "$RC" | grep -Eq 'shooter_\.setWantedState\([[:space:]]*ShooterStates\.SHOOT[[:space:]]*\)'; then
	echo "In the SHOOT case: shooter_.setWantedState(ShooterStates.SHOOT);"
	exit 1
fi
if ! code "$RC" | grep -Eq 'drive_\.setWantedState\([[:space:]]*DriveStates\.ARCADE[[:space:]]*\)'; then
	echo "In the DRIVE and SHOOT cases: drive_.setWantedState(DriveStates.ARCADE);"
	exit 1
fi

# OI: hold the right bumper for SHOOT, let go for DRIVE; A simulates a launch.
if ! code "$OI" | grep -Eq 'rightBumper\(\)[[:space:]]*\.whileTrue\([[:space:]]*Commands\.startEnd\([^;]*setWantedState\([[:space:]]*RobotStates\.SHOOT[[:space:]]*\)[^;]*setWantedState\([[:space:]]*RobotStates\.DRIVE[[:space:]]*\)'; then
	echo "In OI.configureBindings(): driver_controller_.rightBumper().whileTrue(Commands.startEnd("
	echo "    () -> robot.setWantedState(RobotStates.SHOOT), () -> robot.setWantedState(RobotStates.DRIVE)));"
	exit 1
fi
if ! code "$OI" | grep -Eq '\ba\(\)[[:space:]]*\.onTrue\([[:space:]]*Commands\.runOnce\([^;]*simulateBallLaunch'; then
	echo "In OI.configureBindings(): driver_controller_.a().onTrue(Commands.runOnce(() -> ShooterSubsystem.getInstance().simulateBallLaunch()));"
	exit 1
fi
echo "The robot asks the subsystems for their states, and the buttons ask the robot."
