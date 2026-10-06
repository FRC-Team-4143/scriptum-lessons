#!/usr/bin/env bash
# Shared helpers for the robot-control-theory verifiers.
SUBSYSTEM() { echo "$1/src/main/java/frc/robot/subsystems/shooter/ShooterSubsystem.java"; }
CONSTANTS() { echo "$1/src/main/java/frc/robot/subsystems/shooter/ShooterConstants.java"; }
# Prints a file's code on one line, without comment lines.
flat() { grep -Ev '^[[:space:]]*(//|\*|/\*)' "$1" | tr '\n' ' '; }
