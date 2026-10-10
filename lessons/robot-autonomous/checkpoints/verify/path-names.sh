#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DEPLOY="$PROJECT/src/main/deploy/choreo"
AUTOS="$PROJECT/src/main/java/frc/robot/autos/Autos.java"

for name in ToPickup ToScore; do
	if [[ ! -f "$DEPLOY/$name.traj" ]]; then
		echo "No path named $name. Name the path that drives to the pickup ToPickup and the one to the scoring spot ToScore (rename them in the Choreo pane, capital letters matter)."
		exit 1
	fi
	if ! jq -e --arg n "$name" '.name == $n' "$DEPLOY/$name.traj" >/dev/null 2>&1; then
		echo "$name.traj is not named $name inside Choreo. Rename the path in the Choreo pane instead of renaming the file."
		exit 1
	fi
done

if [[ ! -f "$AUTOS" ]]; then
	echo "Can't find Autos.java."
	exit 1
fi
# The paths Autos.java loads, written without comment lines.
loaded="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$AUTOS" | grep -oE 'loadTrajectory[[:space:]]*\([[:space:]]*"[^"]*"' | sed -E 's/.*"([^"]*)"/\1/' || true)"
if [[ -z "$loaded" ]]; then
	echo "Autos.java does not load any path yet. Use loadTrajectory(\"ToPickup\") and loadTrajectory(\"ToScore\")."
	exit 1
fi
for name in ToPickup ToScore; do
	if ! grep -qx "$name" <<<"$loaded"; then
		echo "Autos.java should call loadTrajectory(\"$name\"), spelled exactly like the path's name."
		exit 1
	fi
done
while IFS= read -r name; do
	if [[ ! -f "$DEPLOY/$name.traj" ]]; then
		echo "Autos.java loads \"$name\" but there is no path with that name. Check the spelling against the Choreo pane."
		exit 1
	fi
done <<<"$loaded"

echo "The paths ToPickup and ToScore exist and Autos.java loads them."
