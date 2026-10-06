#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Large Text Display" and
		((.properties.topic // "") | endswith("StatusMessage"))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Large Text Display bound to StatusMessage looks good."
	exit 0
fi

echo "Add a Large Text Display widget and bind it to StatusMessage."
exit 1
