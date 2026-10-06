#!/usr/bin/env bash
set -euo pipefail
LAYOUT="${2:-}"

if [[ -z "$LAYOUT" || ! -s "$LAYOUT" ]]; then
	echo "Open AdvantageScope and add a Swerve tab, then click Verify again."
	exit 1
fi

# TabType.Swerve = 9 (vendor/AdvantageScope/src/shared/TabType.ts).
# SwerveController.saveState() returns { sources: SourceListState }, same
# single-source-list shape as Field2d/Field3d
# (vendor/AdvantageScope/src/hub/controllers/SwerveController.ts).
if jq -e '
	any(.tabs.tabs[]?;
		.type == 9 and
		((.controller.sources // []) as $sources
			| ($sources | any(.logKey | endswith("SwerveModuleStates")))
			and ($sources | any(.logKey | endswith("ChassisSpeeds")))
			and ($sources | any(.logKey | endswith("ChassisRotation"))))
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "Swerve tab looks good."
	exit 0
fi

echo "Add a Swerve tab and drag SwerveModuleStates, ChassisSpeeds, and ChassisRotation onto it."
exit 1
