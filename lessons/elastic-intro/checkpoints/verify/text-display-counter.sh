#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Text Display" and
		((.properties.topic // "") | endswith("Counter"))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Text Display bound to Counter looks good."
	exit 0
fi

echo "Add a Text Display widget and bind it to Counter."
exit 1
