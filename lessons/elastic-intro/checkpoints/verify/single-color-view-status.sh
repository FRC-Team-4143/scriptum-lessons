#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Single Color View" and
		((.properties.topic // "") | endswith("StatusColorHex"))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Single Color View bound to StatusColorHex looks good."
	exit 0
fi

echo "Add a Single Color View widget and bind it to StatusColorHex."
exit 1
