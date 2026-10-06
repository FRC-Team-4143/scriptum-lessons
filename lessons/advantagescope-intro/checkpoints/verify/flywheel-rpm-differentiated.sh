#!/usr/bin/env bash
set -euo pipefail
LAYOUT="${2:-}"

if [[ -z "$LAYOUT" || ! -s "$LAYOUT" ]]; then
	echo "Open AdvantageScope and build a Line Graph, then click Verify again."
	exit 1
fi

# TabType.LineGraph = 1. LineGraphFilter.Differentiate = 1
# (vendor/AdvantageScope/src/shared/LineGraphFilter.ts). Right axis's own
# scale is left auto - only the filter and the source placement are checked.
if jq -e '
	any(.tabs.tabs[]?;
		.type == 1 and
		((.controller.rightSources // []) | any(.logKey | endswith("FlywheelRPM"))) and
		(.controller.rightFilter == 1)
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Differentiated FlywheelRPM on the right axis looks good."
	exit 0
fi

echo "Drag FlywheelRPM onto the Line Graph's right axis, then set its Filter to Differentiate (right-click the right axis)."
exit 1
