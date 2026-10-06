#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Radial Gauge" and
		((.properties.topic // "") | endswith("GyroHeadingDegrees")) and
		(.properties.min_value == 0) and
		(.properties.max_value == 360) and
		(.properties.start_angle == 0) and
		(.properties.end_angle == 360)
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Radial Gauge bound to GyroHeadingDegrees looks good."
	exit 0
fi

echo "Add a Radial Gauge widget, bind it to GyroHeadingDegrees, set its Min/Max Value to 0/360, and its Start/End Angle to 0/360."
exit 1
