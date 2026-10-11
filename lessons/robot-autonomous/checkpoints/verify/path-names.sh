#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_trees.sh"
DEPLOY="$PROJECT/src/main/deploy/choreo"

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

# The paths the trees drive: every trajectory="..." of a FollowPath node.
used="$(all_trees | grep -oE '<FollowPath\b[^>]*>' | grep -oE '\btrajectory="[^"]*"' | sed -E 's/.*"([^"]*)"/\1/' || true)"
if [[ -z "$used" ]]; then
	echo "Your tree does not drive any path yet. Open PickupAndScore in the Arbor pane and give FollowPath nodes the trajectory names ToPickup and ToScore."
	exit 1
fi
for name in ToPickup ToScore; do
	if ! grep -qx "$name" <<<"$used"; then
		echo "Add a FollowPath with trajectory $name to your tree, spelled exactly like the path's name."
		exit 1
	fi
done
while IFS= read -r name; do
	if [[ ! -f "$DEPLOY/$name.traj" ]]; then
		echo "A FollowPath drives \"$name\" but there is no path with that name. Check the spelling against the Choreo pane."
		exit 1
	fi
done <<<"$used"

echo "The paths ToPickup and ToScore exist and your tree drives them."
