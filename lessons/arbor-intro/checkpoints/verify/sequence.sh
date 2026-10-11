#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_common.sh"
text="$(user_text)"
if ! echo "$text" | grep -qE '<Sequence\b'; then
	echo "Put your steps under a Sequence: it runs its children in order."
	exit 1
fi
steps=$({ echo "$text" | grep -oE '<(SetWantedState|FollowPath|Sleep)\b' || true; } | wc -l)
if [ "$steps" -lt 3 ]; then
	echo "Your Sequence has $steps step(s); add a SetWantedState, a Sleep and a FollowPath (at least 3 in all)."
	exit 1
fi
echo "A Sequence with $steps steps."
