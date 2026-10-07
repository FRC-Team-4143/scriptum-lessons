#!/usr/bin/env bash
set -euo pipefail
LAYOUT="${2:-}"

if [[ -z "$LAYOUT" || ! -s "$LAYOUT" ]]; then
	echo "Open AdvantageScope and add a 3D Field tab, then click Verify again."
	exit 1
fi

# TabType.Field3d = 3 (vendor/AdvantageScope/src/shared/TabType.ts).
# Field3dController.saveState() returns { sources: SourceListState, game }
# - note the key is "game", not "field" (unlike Field2dController) - confirmed
# by reading both saveState() bodies directly, they're not symmetric. "game"
# holds the field id, "FRC:Evergreen" for the built-in Evergreen field.
# The camera mode lives separately, on the tab's renderer (not controller):
# Field3dRendererImpl.saveState() returns { cameraIndex, orbitFov, ... } -
# CameraIndexEnum.OrbitRobot = -2.
if jq -e '
	any(.tabs.tabs[]?;
		.type == 3 and
		((.controller.sources // []) | any(.logKey | endswith("RobotPose3d"))) and
		(.controller.game == "FRC:Evergreen") and
		(.renderer.cameraIndex == -2)
	)
' "$LAYOUT" >/dev/null 2>&1; then
	echo "3D Field plotting RobotPose3d looks good."
	exit 0
fi

echo "Add a 3D Field tab, drag RobotPose3d onto it, set the field to Evergreen, and set the camera to Orbit Robot."
exit 1
