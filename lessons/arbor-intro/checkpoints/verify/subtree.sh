#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_common.sh"
source "$(dirname "$0")/_trees.sh"
if [ ${#USED[@]} -eq 0 ]; then
	echo "Use a tree inside another: make a small tree called AimAndShoot (+ New tree, saved in trees/my_first_auto.xml), give it a few steps, then drag it from the Trees list onto the Sequence in MyFirstAuto, and save (Ctrl+S)."
	exit 1
fi
for id in "${USED[@]}"; do
	if ! defined_anywhere "$id"; then
		# Easy to do: the New tree dialog's "An existing file" list starts on a file that came with
		# the lesson, and those files never count as yours.
		for starter in Example.xml ReplayDemo.xml; do
			if [ -f "$TREES/$starter" ] && strip < "$TREES/$starter" | grep -qE "<BehaviorTree\b[^>]*\bID=\"$id\""; then
				echo "$id is saved in $starter, which came with the lesson. Right-click $id in the Trees list, choose Move to file..., pick your own file (trees/my_first_auto.xml), and save."
				exit 1
			fi
		done
		echo "Your tree uses a SubTree called $id, but there's no tree with that name. Make it with + New tree, or drag in one from the Trees list."
		exit 1
	fi
done
for id in "${USED[@]}"; do
	if ! has_steps "$id"; then
		echo "$id is used as a SubTree but has no steps yet. Open it, give it a root node and a couple of steps, and save."
		exit 1
	fi
done
echo "Your tree uses ${USED[0]} as a SubTree."
