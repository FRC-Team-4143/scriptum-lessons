#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DIR="$PROJECT/src/main/java/frc/robot"

for f in OI.java Robot.java; do
	if [[ ! -f "$DIR/$f" ]]; then
		echo "Can't find $f in src/main/java/frc/robot."
		exit 1
	fi
done

flat() { tr '\n' ' ' <"$DIR/$1"; }

for method in getForward getTurn; do
	if ! flat OI.java | grep -Eq "public[[:space:]]+static[[:space:]]+double[[:space:]]+$method[[:space:]]*\\("; then
		echo "OI needs a method: public static double $method()"
		exit 1
	fi
done
if ! flat OI.java | grep -Eq 'return[^;]*(getLeftY|getLeftX)'; then
	echo "OI's methods should return values from the controller (getLeftY / getLeftX)."
	exit 1
fi
if grep -q 'XboxController' "$DIR/Robot.java"; then
	echo "Robot.java still uses XboxController. Read the controls through OI instead."
	exit 1
fi
if ! grep -Eq 'OI\.getForward\(\)' "$DIR/Robot.java" || ! grep -Eq 'OI\.getTurn\(\)' "$DIR/Robot.java"; then
	echo "Robot.java should use OI.getForward() and OI.getTurn()."
	exit 1
fi
echo "OI owns the controller and Robot uses it."
