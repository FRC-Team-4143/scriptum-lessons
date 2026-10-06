#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Number Bar" and
		((.properties.topic // "") | endswith("ClimberSpeed"))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Number Bar bound to ClimberSpeed looks good."
	exit 0
fi

echo "Add a Number Bar widget and bind it to ClimberSpeed."
exit 1
