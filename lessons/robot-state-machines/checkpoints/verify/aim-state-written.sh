#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DIR="$PROJECT/src/main/java/frc/robot/subsystems/drive"
CONSTANTS="$DIR/DrivetrainConstants.java"
DRIVE="$DIR/DrivetrainSubsystem.java"
CMDS="$DIR/DrivetrainCommands.java"
for f in "$CONSTANTS" "$DRIVE" "$CMDS"; do
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
	echo "Headings wrap around: call aimPid.enableContinuousInput(-Math.PI, Math.PI)."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq '\.calculate[[:space:]]*\('; then
	echo "In the AIM case, ask the PID for a turn with aimPid.calculate(heading, angleToGoal)."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq 'GOAL[^;]*getTranslation[[:space:]]*\(\)[^;]*\.minus[[:space:]]*\(|getTranslation[[:space:]]*\(\)[^;]*\.minus[[:space:]]*\([^;]*GOAL'; then
	echo "getAngleToGoal() should subtract the robot's position from DrivetrainConstants.GOAL (use getTranslation().minus(...))."
	exit 1
fi
if ! code "$DRIVE" | grep -Eq 'boolean[[:space:]]+isAimed[[:space:]]*\([[:space:]]*\)[[:space:]]*\{[[:space:]]*return[[:space:]]+[^;]*(&&|\|\|)'; then
	echo "isAimed() should return true only when the error is small AND the robot has stopped turning (use &&)."
	exit 1
fi
if ! code "$CMDS" | grep -q 'startEnd' || ! code "$CMDS" | grep -Eq '\bAIM\b'; then
	echo "DrivetrainCommands.aim() should return Commands.startEnd(...) that sets the wanted state to AIM and back to ARCADE."
	exit 1
fi
echo "The AIM state, its PID and its command are all written."
