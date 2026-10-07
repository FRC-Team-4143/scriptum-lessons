#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DEPLOY="$PROJECT/src/main/deploy/choreo"

shopt -s nullglob
trajs=("$DEPLOY"/*.traj)

if [ ${#trajs[@]} -lt 2 ]; then
	echo "Create a second path - this lesson wants two different paths, not just one."
	exit 1
fi

# Compares waypoint geometry only (x/y/heading), not the path's name or its
# solved trajectory - so duplicating a path and just renaming it doesn't
# count, but two paths that happen to solve into similar-looking samples
# still pass as long as the waypoints themselves differ.
waypoints_of() {
	jq -c '[.snapshot.waypoints[]? | {x, y, heading}]' "$1"
}

signatures=()
for traj in "${trajs[@]}"; do
	signatures+=("$(waypoints_of "$traj")")
done

for ((i = 0; i < ${#signatures[@]}; i++)); do
	for ((j = i + 1; j < ${#signatures[@]}; j++)); do
		if [ "${signatures[i]}" != "${signatures[j]}" ]; then
			echo "Found two paths with different waypoints."
			exit 0
		fi
	done
done

echo "Your paths all have the same waypoints - move at least one of them to make a genuinely different path."
exit 1
