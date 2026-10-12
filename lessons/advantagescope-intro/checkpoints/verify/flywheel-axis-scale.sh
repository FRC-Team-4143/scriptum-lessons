#!/usr/bin/env bash
set -euo pipefail
LAYOUT="${2:-}"

if [[ -z "$LAYOUT" || ! -s "$LAYOUT" ]]; then
	echo "Open AdvantageScope and build a Line Graph, then click Verify again."
	exit 1
fi

# TabType.LineGraph = 1. Left axis specifically - the right axis holds the
# differentiated signal and is left auto-scaling on purpose. Tolerant but
# two-sided bounds around 0-600 (rad/s), so a stale 0-3000 or 0-6000 lock from
# the old RPM version of this lesson does not slip through.
if jq -e '
	any(.tabs.tabs[]?;
		.type == 1 and
		(.controller.leftLockedRange as $r
			| $r != null and ($r | length) == 2
				and $r[0] >= -20 and $r[0] <= 20
				and $r[1] >= 580 and $r[1] <= 620)
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Locked axis range looks good."
	exit 0
fi

echo "Lock the Line Graph's left Y axis to roughly 0-600 (right-click the axis)."
exit 1
