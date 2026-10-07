#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Multi Color View" and
		((.properties.topic // "") | endswith("LedStripColorsHex"))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Multi Color View bound to LedStripColorsHex looks good."
	exit 0
fi

echo "Add a Multi Color View widget and bind it to LedStripColorsHex."
exit 1
