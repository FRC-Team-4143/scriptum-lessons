#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

# Min/Max Value are the heading in radians: one full turn is 2*pi, about 6.28.
# Accept 6.27-6.29 so 6.28 or 6.283 both pass. Start/End Angle are Elastic's
# own dial drawing settings, which Elastic only takes in degrees, so those stay
# 0/360 (a full circle dial).
if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Radial Gauge" and
		((.properties.topic // "") | endswith("GyroHeadingRad")) and
		(.properties.min_value == 0) and
		(.properties.max_value >= 6.27 and .properties.max_value <= 6.29) and
		(.properties.start_angle == 0) and
		(.properties.end_angle == 360)
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Radial Gauge bound to GyroHeadingRad looks good."
	exit 0
fi

echo "Add a Radial Gauge widget, bind it to GyroHeadingRad, set its Min/Max Value to 0/6.28 (radians), and its Start/End Angle to 0/360."
exit 1
