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

# Both of the lesson's two paths need to actually solve, not just one of
# them - a path with an impossible constraint (like a velocity limit the
# robot can't hit) is left unsolved and shouldn't slide by on its sibling's
# success.
for traj in "${trajs[@]}"; do
	if ! jq -e '(.trajectory.samples | length) > 0' "$traj" >/dev/null 2>&1; then
		echo "$(basename "$traj" .traj) hasn't generated yet - Choreo couldn't solve it. Check that its constraints aren't impossible to satisfy (e.g. a velocity limit the robot can't hit)."
		exit 1
	fi
done

echo "Every path generated successfully."
exit 0
