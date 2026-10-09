// robot-computer-vision: the lesson is a skeleton for now (no checkpoints yet), so this only checks that
// the starter and the reference solution differ in the one place they should, and that the PhotonLib
// vendordep the vision simulator needs is there. The solution is laid over project/ like robot-autonomous's.
import { describe, expect, test } from "bun:test";
import { readFileSync, existsSync } from "node:fs";
import { rm } from "node:fs/promises";
import { join } from "node:path";
import { lessonKit, ROBOT_SRC } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);
const LOCALIZATION = "subsystems/localization/LocalizationSubsystem.java";

describe("robot-computer-vision", () => {
	test("the starter has the vision TODO and the solution fills it in", async () => {
		const starter = readFileSync(join(L.dir, "project", ROBOT_SRC, LOCALIZATION), "utf8");
		expect(starter).toContain("TODO");
		expect(starter).not.toContain(".addVisionMeasurement(");

		const project = await L.makeProject();
		try {
			await L.overlaySolution(project);
			const solved = readFileSync(join(project, ROBOT_SRC, LOCALIZATION), "utf8");
			expect(solved).toContain("pose_estimator_.addVisionMeasurement(");
			expect(solved).not.toContain("TODO");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("the project carries the PhotonLib vendordep and the pinned MWLib", () => {
		const photon = JSON.parse(readFileSync(join(L.dir, "project", "vendordeps", "photonlib.json"), "utf8"));
		expect(photon.name).toBe("photonlib");
		expect(photon.version).toBe("v2026.2.2");
		const gradle = readFileSync(join(L.dir, "project", "build.gradle"), "utf8");
		expect(gradle).toContain("mw-lib-java:26.17.3");
	});

	test("the provided pieces the next chunks build on exist", () => {
		for (const file of [
			"FieldTargets.java",
			"subsystems/localization/LocalizationConstants.java",
			"subsystems/simulation/SimulationSubsystem.java",
			"vision/TagVision.java",
			"vision/VisionMeasurement.java",
			"vision/VisionConstants.java",
		]) {
			expect(existsSync(join(L.dir, "project", ROBOT_SRC, file)), file).toBe(true);
		}
	});
});
