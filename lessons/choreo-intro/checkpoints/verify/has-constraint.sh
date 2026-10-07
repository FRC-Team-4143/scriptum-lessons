#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DEPLOY="$PROJECT/src/main/deploy/choreo"

shopt -s nullglob
trajs=("$DEPLOY"/*.traj)

if [ ${#trajs[@]} -eq 0 ]; then
	echo "No path found yet. Create a path in the Choreo pane and generate it."
	exit 1
fi

# Every new path gets StopPoint (enabled) and a full-field KeepInRectangle
# (disabled) added automatically. Neither counts: StopPoint is a default, and
# KeepInRectangle is the usual field-boundary default on a whole path, so
# enabling it shows nothing about choosing a limit for the robot. The types
# that do count are limits a path's author picks (Max Velocity, Max Angular
# Velocity, Keep In Lane).
for traj in "${trajs[@]}"; do
	if jq -e '
		any(.snapshot.constraints[]?;
			.enabled == true and
			(.data.type as $t | ["MaxVelocity", "MaxAngularVelocity", "KeepInLane"] | index($t) != null)
		)
	' "$traj" >/dev/null 2>&1; then
		echo "Path has an enabled constraint."
		exit 0
	fi
done

echo "Add a constraint to your path from the constraints panel: Max Velocity or Max Angular Velocity. (Keep In Rectangle doesn't count: it's the usual field boundary on a whole path.)"
exit 1
