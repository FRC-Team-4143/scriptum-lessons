#!/usr/bin/env bash
set -euo pipefail
LAYOUT="$1/src/main/deploy/elastic-layout.json"

if [[ ! -s "$LAYOUT" ]]; then
	echo "Save your layout in Elastic (File > Save), then click Verify again."
	exit 1
fi

if jq -e '
	any(.tabs[]?.grid_layout.containers[]?;
		.type == "ComboBox Chooser" and
		((.properties.topic // "") | endswith("AutoRoutine"))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "ComboBox Chooser bound to AutoRoutine looks good."
	exit 0
fi

echo "Add a ComboBox Chooser widget and bind it to AutoRoutine."
exit 1
