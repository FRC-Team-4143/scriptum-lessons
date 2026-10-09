#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DEPLOY="$PROJECT/src/main/deploy/choreo"

shopt -s nullglob
trajs=("$DEPLOY"/*.traj)

if [ ${#trajs[@]} -lt 2 ]; then
	echo "Draw two paths in the Choreo pane (this routine drives two: one to the pickup, one to the scoring spot). Found ${#trajs[@]}."
	exit 1
fi

# Every path needs a start, a point to steer through and an end, and Choreo has to have solved it
# for the robot (Differential: left and right wheel speeds), not just drawn it.
for traj in "${trajs[@]}"; do
	name="$(basename "$traj" .traj)"
	if ! jq -e '(.snapshot.waypoints | length) >= 3' "$traj" >/dev/null 2>&1; then
		echo "$name needs at least three waypoints: where it starts, a point in between, and where it ends."
		exit 1
	fi
	if ! jq -e '(.trajectory.samples | length) > 0' "$traj" >/dev/null 2>&1; then
		echo "$name has not been generated yet. Press Generate in the Choreo pane (and check its constraints can be met)."
		exit 1
	fi
	if ! jq -e '.trajectory.sampleType == "Differential"' "$traj" >/dev/null 2>&1; then
		echo "$name was not made for a Differential drivetrain. Use the project's own robot.chor settings."
		exit 1
	fi
done

echo "${#trajs[@]} paths drawn, each with at least three waypoints and generated."
