#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Boolean Box" and
		((.properties.topic // "") | endswith("GamePieceLoaded"))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Boolean Box bound to GamePieceLoaded looks good."
	exit 0
fi

echo "Add a Boolean Box widget and bind it to GamePieceLoaded."
exit 1
