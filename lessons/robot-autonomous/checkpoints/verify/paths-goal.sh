#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
DEPLOY="$PROJECT/src/main/deploy/choreo"
TARGETS="$PROJECT/src/main/java/frc/robot/FieldTargets.java"

for name in ToPickup ToScore; do
	if ! jq -e '(.trajectory.samples | length) > 0' "$DEPLOY/$name.traj" >/dev/null 2>&1; then
		echo "Can't read a generated path named $name yet. Draw it, name it and press Generate."
		exit 1
	fi
done

# The field targets come from FieldTargets.java (the same numbers the robot's checks use).
flat="$(grep -Ev '^[[:space:]]*(//|\*|/\*)' "$TARGETS" | tr '\n' ' ')"
target() { # target NAME -> "x y"
	sed -nE "s/.*[[:space:]]$1[[:space:]]*=[[:space:]]*new[[:space:]]+Pose2d[[:space:]]*\([[:space:]]*(-?[0-9.]+)[[:space:]]*,[[:space:]]*(-?[0-9.]+).*/\1 \2/p" <<<"$flat"
}
read -r START_X START_Y <<<"$(target START)"
read -r PICKUP_X PICKUP_Y <<<"$(target PICKUP)"
read -r SCORE_X SCORE_Y <<<"$(target SCORE_SPOT)"

# distance between the first or last point of a path and a place (meters)
ends() { # ends FILE first|last X Y -> distance
	jq -r --argjson x "$3" --argjson y "$4" ".trajectory.samples | .[$([ "$2" = first ] && echo 0 || echo -1)] | ((.x - \$x) * (.x - \$x) + (.y - \$y) * (.y - \$y)) | sqrt" "$1"
}
joint() { # joint FILE1 FILE2 -> distance from FILE1's last point to FILE2's first point
	jq -n -r --slurpfile a "$1" --slurpfile b "$2" '($a[0].trajectory.samples | .[-1]) as $p | ($b[0].trajectory.samples | .[0]) as $q | (($p.x - $q.x) * ($p.x - $q.x) + ($p.y - $q.y) * ($p.y - $q.y)) | sqrt'
}
within() { awk -v d="$1" -v m="$2" 'BEGIN { exit !(d <= m) }'; }

d="$(ends "$DEPLOY/ToPickup.traj" first "$START_X" "$START_Y")"
if ! within "$d" 0.3; then
	echo "ToPickup should start at the Start point ($START_X, $START_Y); it starts $(printf '%.2f' "$d") meters away."
	exit 1
fi
d="$(ends "$DEPLOY/ToPickup.traj" last "$PICKUP_X" "$PICKUP_Y")"
if ! within "$d" 0.3; then
	echo "ToPickup should end at the Pickup point ($PICKUP_X, $PICKUP_Y); it ends $(printf '%.2f' "$d") meters away."
	exit 1
fi
d="$(joint "$DEPLOY/ToPickup.traj" "$DEPLOY/ToScore.traj")"
if ! within "$d" 0.15; then
	echo "ToScore should start where ToPickup ended; they are $(printf '%.2f' "$d") meters apart."
	exit 1
fi
d="$(ends "$DEPLOY/ToScore.traj" last "$SCORE_X" "$SCORE_Y")"
if ! within "$d" 0.3; then
	echo "ToScore should end at the ScoreSpot point ($SCORE_X, $SCORE_Y); it ends $(printf '%.2f' "$d") meters away."
	exit 1
fi

echo "ToPickup goes from Start to Pickup, and ToScore carries on from there to ScoreSpot."
