#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Match Time" and
		((.properties.topic // "") | endswith("MatchTimeRemaining")) and
		(.properties.red_start_time == 15) and
		(.properties.yellow_start_time == 45)
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Match Time bound to MatchTimeRemaining looks good."
	exit 0
fi

echo "Add a Match Time widget, bind it to MatchTimeRemaining, and set Red Start Time to 15 and Yellow Start Time to 45."
exit 1
