#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Graph" and
		((.properties.topic // "") | endswith("IntakeCurrentAmps")) and
		(.properties.min_value == 0) and
		(.properties.max_value == 45) and
		(.properties.color == 4294967295)
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Graph bound to IntakeCurrentAmps looks good."
	exit 0
fi

echo "Add a Graph widget, bind it to IntakeCurrentAmps, set its range to 0-45, and set its Graph Color to pure white."
exit 1
