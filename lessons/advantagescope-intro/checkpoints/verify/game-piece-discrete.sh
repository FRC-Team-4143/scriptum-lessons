#!/usr/bin/env bash
set -euo pipefail
LAYOUT="${2:-}"

if [[ -z "$LAYOUT" || ! -s "$LAYOUT" ]]; then
	echo "Open AdvantageScope and build a Line Graph, then click Verify again."
	exit 1
fi

# TabType.LineGraph = 1. discreteSources is the Line Graph's third source
# list (below the left/right axes), for boolean/discrete signals shown as
# colored bands rather than plotted numerically.
if jq -e '
	any(.tabs.tabs[]?;
		.type == 1 and
		((.controller.discreteSources // []) | any(.logKey | endswith("GamePieceLoaded")))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "GamePieceLoaded on the discrete field looks good."
	exit 0
fi

echo "Drag GamePieceLoaded onto the Line Graph's discrete field (below the left/right axes)."
exit 1
