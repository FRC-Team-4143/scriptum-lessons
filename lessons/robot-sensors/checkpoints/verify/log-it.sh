#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
FILE="$PROJECT/src/main/java/frc/robot/Robot.java"
if [[ ! -f "$FILE" ]]; then
	echo "Can't find Robot.java."
	exit 1
fi
# The code without comments, on one line.
CODE="$(sed -E 's#//.*$##' "$FILE" | grep -Ev '^[[:space:]]*(\*|/\*)' | tr '\n' ' ')"
# Just the body of robotPeriodic(): from its name to the next method.
PERIODIC="$(sed -E 's/.*void[[:space:]]+robotPeriodic[[:space:]]*\(\)//; s/@Override.*//' <<<"$CODE")"
if ! grep -Eq 'void[[:space:]]+robotPeriodic[[:space:]]*\(' <<<"$CODE"; then
	echo "Can't find robotPeriodic() in Robot.java."
	exit 1
fi

check() {
	local key="$1" value="$2" unit="$3"
	local call="MwLog\\.log[[:space:]]*\\([[:space:]]*\"$key\"[[:space:]]*,"
	if ! grep -Eq "$call" <<<"$CODE"; then
		if grep -Eiq "MwLog\\.log[[:space:]]*\\([[:space:]]*\"$key\"" <<<"$CODE"; then
			echo "Found a log line for \"$key\", but the capitals don't match. Keys are case sensitive: spell it exactly \"$key\"."
		else
			echo "Log $value ($unit) with MwLog.log, using the key \"$key\"."
		fi
		exit 1
	fi
	if ! grep -Eq "$call[[:space:]]*$value[[:space:]]*\\)" <<<"$CODE"; then
		echo "The \"$key\" line should log $value. Check the second thing inside the brackets."
		exit 1
	fi
	if ! grep -Eq "$call" <<<"$PERIODIC"; then
		echo "Move the \"$key\" line into robotPeriodic(), after STEP 2, so it's logged in every mode."
		exit 1
	fi
}

check "Robot/LinearSpeed" "linear_speed" "m/s"
check "Robot/AngularSpeed" "angular_speed" "rad/s"
check "Robot/Distance" "distance_meters_" "m"
echo "All three numbers are logged in robotPeriodic(): Robot/LinearSpeed, Robot/AngularSpeed and Robot/Distance."
