#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "Voltage View" and
		((.properties.topic // "") | endswith("BatteryVoltage"))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Voltage View bound to BatteryVoltage looks good."
	exit 0
fi

echo "Add a Voltage View widget and bind it to BatteryVoltage."
exit 1
