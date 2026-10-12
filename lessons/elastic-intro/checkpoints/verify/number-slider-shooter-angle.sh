#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

# Radians: the max is pi/4, about 0.785 (45 degrees). Accept 0.78-0.79 so a
# student who types 0.785 or 0.7854 passes.
if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Number Slider" and
		((.properties.topic // "") | endswith("ShooterAngleRad")) and
		(.properties.min_value == 0) and
		(.properties.max_value >= 0.78 and .properties.max_value <= 0.79)
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Number Slider bound to ShooterAngleRad looks good."
	exit 0
fi

echo "Add a Number Slider widget, bind it to ShooterAngleRad, and set its range to 0-0.785 (radians)."
exit 1
