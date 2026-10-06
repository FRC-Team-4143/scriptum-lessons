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

# Every path the student has made needs the full four waypoints - not just
# one of them - so a second, lazily-built path can't skate by on the first
# path's work.
for traj in "${trajs[@]}"; do
	if ! jq -e '(.snapshot.waypoints | length) >= 4' "$traj" >/dev/null 2>&1; then
		echo "$(basename "$traj" .traj) needs at least four waypoints."
		exit 1
	fi
done

echo "Every path has at least four waypoints."
exit 0
