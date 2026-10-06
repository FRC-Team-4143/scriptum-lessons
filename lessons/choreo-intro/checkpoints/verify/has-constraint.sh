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
# (disabled) added automatically - neither should count on their own, so
# StopPoint is excluded and KeepInRectangle only counts once the student
# actually enables it. This list matches the constraint types real FRC
# paths actually use in practice (see docs/decisions - Choreo lesson).
for traj in "${trajs[@]}"; do
	if jq -e '
		any(.snapshot.constraints[]?;
			.enabled == true and
			(.data.type as $t | ["MaxVelocity", "MaxAngularVelocity", "KeepInRectangle", "KeepInLane"] | index($t) != null)
		)
	' "$traj" >/dev/null 2>&1; then
		echo "Path has an enabled constraint."
		exit 0
	fi
done

echo "Add a constraint to your path (e.g. Max Velocity, Max Angular Velocity, or Keep In Rectangle) from the constraints panel."
exit 1
