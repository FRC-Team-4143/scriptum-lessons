#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_trees.sh"
if [[ ! -f "$TREE" ]]; then
	echo "Can't find deploy/autos/trees/PickupAndScore.xml. Build the tree in the Arbor pane under the name PickupAndScore, and save it."
	exit 1
fi
text="$(strip <"$TREE")"
# Where a pattern first appears in the tree, or nothing if it doesn't.
at() { { grep -oEb "$1" <<<"$text" || true; } | head -1 | cut -d: -f1; }

if ! grep -qE 'main_tree_to_execute="PickupAndScore"' <<<"$text"; then
	echo "PickupAndScore.xml should run the tree called PickupAndScore. Name the tree PickupAndScore in Arbor."
	exit 1
fi
if [[ -z "$(at '<Sequence\b')" ]]; then
	echo "Put the steps under a Sequence: it runs its children one after another."
	exit 1
fi
pickup="$(at '<FollowPath\b[^>]*\btrajectory="ToPickup"')"
score="$(at '<FollowPath\b[^>]*\btrajectory="ToScore"')"
aim="$(at '<SetWantedState\b[^>]*\bstate="AIM"')"
wait="$(at '<IsSystemState\b[^>]*\bstate="AIM"')"
shoot="$(at '<SetWantedState\b[^>]*\bstate="SHOOT"')"
if [[ -z "$pickup" ]]; then
	echo "Drive the first path: a FollowPath with trajectory ToPickup."
	exit 1
fi
if [[ -z "$score" ]]; then
	echo "Drive the second path: a FollowPath with trajectory ToScore."
	exit 1
fi
if [[ -z "$aim" ]]; then
	echo "Turn to face the goal before shooting: a SetWantedState with state AIM."
	exit 1
fi
# Asking is instant, so the tree waits for AIM: Timeout > RetryUntilSuccessful(-1) > IsSystemState AIM.
if [[ -z "$wait" ]]; then
	echo "Asking for AIM succeeds at once, before the robot has turned. After it, wait until the robot is in AIM: an IsSystemState with state AIM."
	exit 1
fi
retry='<RetryUntilSuccessful\b[^>]*\bnum_attempts="-1"([^>]*[^/>])?>[[:space:]]*<IsSystemState\b[^>]*\bstate="AIM"'
if [[ -z "$(at "$retry")" ]]; then
	echo "IsSystemState AIM only checks once. Put it inside a RetryUntilSuccessful with num_attempts -1, so it asks again every tick until the robot is in AIM."
	exit 1
fi
if [[ -z "$(at '<Timeout\b[^>]*\bmsec="[0-9]+"([^>]*[^/>])?>[[:space:]]*'"$retry")" ]]; then
	echo "Put the wait for AIM (the RetryUntilSuccessful) inside a Timeout with a msec, so a robot that cannot aim cannot hold up the routine."
	exit 1
fi
if [[ -z "$shoot" ]]; then
	echo "Shoot: a SetWantedState with state SHOOT, after the aim step."
	exit 1
fi
if ! ((pickup < score && score < aim && aim < wait && wait < shoot)); then
	echo "Put the steps in order inside the Sequence: FollowPath ToPickup, FollowPath ToScore, ask for AIM, wait for AIM, then shoot (shooting before the robot is aimed misses)."
	exit 1
fi
# After SHOOT: let the shooter run for a while, then go back to AUTO.
after="${text:$shoot}"
sleep_at="$({ grep -oEb '<Sleep\b[^>]*\bmsec="[0-9]+"' <<<"$after" || true; } | head -1 | cut -d: -f1)"
auto_at="$({ grep -oEb '<SetWantedState\b[^>]*\bstate="AUTO"' <<<"$after" || true; } | head -1 | cut -d: -f1)"
if [[ -z "$sleep_at" ]]; then
	echo "SHOOT succeeds as soon as the robot is shooting. Add a Sleep (with a msec) after it, so the shooter has time to shoot."
	exit 1
fi
if [[ -z "$auto_at" ]] || ((auto_at < sleep_at)); then
	echo "End the routine with a SetWantedState AUTO after the Sleep, so the shooter stops."
	exit 1
fi

echo "The tree drives both paths, asks for AIM and waits for it (with a time limit), shoots, then stops."
