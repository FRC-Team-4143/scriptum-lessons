#!/usr/bin/env bash
set -euo pipefail
LAYOUT="${2:-}"

if [[ -z "$LAYOUT" || ! -s "$LAYOUT" ]]; then
	echo "Open AdvantageScope and build a Line Graph, then click Verify again."
	exit 1
fi

# TabType.LineGraph = 1 (vendor/AdvantageScope/src/shared/TabType.ts). Left
# axis specifically - the raw signal belongs on the left, the differentiated
# one on the right (see flywheel-rpm-differentiated).
if jq -e '
	any(.tabs.tabs[]?;
		.type == 1 and
		((.controller.leftSources // []) | any(.logKey | endswith("FlywheelRPM")))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Line Graph plotting FlywheelRPM looks good."
	exit 0
fi

echo "Add a Line Graph tab and drag FlywheelRPM onto the left axis."
exit 1
