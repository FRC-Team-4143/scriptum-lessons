#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DIR="$PROJECT/src/main/java/frc/robot/subsystems/drive"
CONSTANTS="$DIR/DrivetrainConstants.java"
DRIVE="$DIR/DrivetrainSubsystem.java"
RC="$PROJECT/src/main/java/frc/robot/RobotContainer.java"
for f in "$CONSTANTS" "$DRIVE" "$RC"; do
	if [[ ! -f "$f" ]]; then
		echo "Can't find $(basename "$f")."
		exit 1
	fi
done
# A file's code on one line, without comment lines, so formatting doesn't matter.
code() { grep -Ev '^[[:space:]]*(//|\*|/\*)' "$1" | tr '\n' ' '; }

if ! code "$CONSTANTS" | grep -Eq 'enum[[:space:]]+DriveStates[^}]*\bAIM\b'; then
	echo "Add an AIM state to the DriveStates enum in DrivetrainConstants.java (after ARCADE, with a comma between them)."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq 'case[[:space:]]+AIM[[:space:]]*:'; then
	echo "In DrivetrainSubsystem.updateLogic(), add a case AIM: to the switch."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq 'new[[:space:]]+PIDController[[:space:]]*\('; then
	echo "Create the aim PID with new PIDController(kP, kI, kD), using DrivetrainConstants.AIM_KP, AIM_KI and AIM_KD."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq 'AIM_KP' || ! code "$DRIVE" | grep -Eq 'AIM_KD'; then
	echo "Build the PIDController from DrivetrainConstants.AIM_KP, AIM_KI and AIM_KD so tuning the constants changes it."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq 'enableContinuousInput[[:space:]]*\('; then
	echo "Headings wrap around: call aim_pid_.enableContinuousInput(-Math.PI, Math.PI)."
	exit 1
fi
# The aim must use the pose ESTIMATE. (The subsystem itself defines getTruePose(), so only look at
# the two places the aim reads a pose: getAngleToGoal() and the PID's calculate(...).)
if { code "$DRIVE" | grep -Eo 'getAngleToGoal[[:space:]]*\([[:space:]]*\)[[:space:]]*\{[^}]*\}' || true
     code "$DRIVE" | grep -Eo '\.calculate[[:space:]]*\([^;]*;' || true; } | grep -Eq 'getTruePose'; then
	echo "Aim from the pose ESTIMATE, getPose(). getTruePose() is only for checking: a real robot has no such thing."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq '\.calculate[[:space:]]*\('; then
	echo "In the AIM case, ask the PID for a turn with aim_pid_.calculate(heading, angleToGoal)."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq 'GOAL[^;]*getTranslation[[:space:]]*\(\)[^;]*\.minus[[:space:]]*\(|getTranslation[[:space:]]*\(\)[^;]*\.minus[[:space:]]*\([^;]*GOAL'; then
	echo "getAngleToGoal() should subtract the robot's position from FieldTargets.GOAL (use getTranslation().minus(...))."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq 'boolean[[:space:]]+isAimed[[:space:]]*\([[:space:]]*\)[[:space:]]*\{[[:space:]]*return[[:space:]]+[^;]*(&&|\|\|)'; then
	echo "isAimed() should return true only when the error is small AND the robot has stopped turning (use &&)."
	exit 1
fi
if ! code "$RC" | grep -Eq 'case[[:space:]]+(RobotStates\.)?AIM[[:space:]]*:[^}]*drive_\.setWantedState\([[:space:]]*DriveStates\.AIM[[:space:]]*\)'; then
	echo "In RobotContainer.updateLogic(), add a case AIM: that asks the drivetrain for DriveStates.AIM (drive_.setWantedState(DriveStates.AIM);)."
	exit 1
fi
echo "The AIM state, its PID and the robot's AIM state are all written."
