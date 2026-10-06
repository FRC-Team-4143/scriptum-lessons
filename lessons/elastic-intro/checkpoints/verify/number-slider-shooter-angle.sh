#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Number Slider" and
		((.properties.topic // "") | endswith("ShooterAngleDegrees")) and
		(.properties.min_value == 0) and
		(.properties.max_value == 45)
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Number Slider bound to ShooterAngleDegrees looks good."
	exit 0
fi

echo "Add a Number Slider widget, bind it to ShooterAngleDegrees, and set its range to 0-45."
exit 1
