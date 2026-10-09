// robot-computer-vision: every script checkpoint fails against the untouched starter and passes against
// the reference solution (laid over project/ like robot-autonomous's), and breaks again when a part of
// the solution is taken away. (The nt4-value checkpoints need a running simulator, so they are not
// covered here; they read the "Check/..." topics that the provided LocalizationChecks.java and
// AimChecks.java publish.) It also checks the PhotonLib vendordep the vision simulator needs is there.
import { describe, expect, test } from "bun:test";
import { readFileSync, existsSync, writeFileSync } from "node:fs";
import { cp, rm } from "node:fs/promises";
import { join } from "node:path";
import { hasJdk, lessonKit, ROBOT_SRC, run } from "../../common/tests/lesson-kit";

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

const AIM_FILES = [
	"subsystems/drive/DrivetrainConstants.java",
	"subsystems/drive/DrivetrainSubsystem.java",
	"subsystems/drive/DrivetrainCommands.java",
];

/** A copy of the project with the whole solution laid over it, then `change` applied. */
async function solved(change: (project: string) => Promise<void> | void = () => {}): Promise<string> {
	const project = await L.makeProject();
	await L.overlaySolution(project);
	await change(project);
	return project;
}
const edit = (project: string, file: string, change: (text: string) => string) => {
	const path = join(project, ROBOT_SRC, file);
	writeFileSync(path, change(readFileSync(path, "utf8")));
};

describe.skipIf(!hasJdk)("robot-computer-vision script checkpoints", () => {
	test("vision-added and aim-state-written fail fresh, pass once solved", async () => {
		await L.roundTrip(["vision-added", "aim-state-written"]);
	}, 30_000);

	test("aim-state-written needs every part, not just the enum", async () => {
		for (const missing of AIM_FILES) {
			const project = await solved(() => {});
			try {
				// put the starter version of one file back
				await cp(join(L.dir, "project", ROBOT_SRC, missing), join(project, ROBOT_SRC, missing));
				expect(L.verify(project, "aim-state-written").exitCode, `without ${missing}`).not.toBe(0);
			} finally {
				await rm(project, { recursive: true, force: true });
			}
		}
	}, 30_000);

	test("aim-state-written wants the estimate, not the simulator's true pose", async () => {
		const project = await solved();
		try {
			edit(project, "subsystems/drive/DrivetrainSubsystem.java", (t) =>
				t.replace("getPose().getTranslation()).getAngle()", "getTruePose().getTranslation()).getAngle()"),
			);
			expect(L.verify(project, "aim-state-written").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("vision-added wants a real call with the picture's time and standard deviations", async () => {
		const LOC = "subsystems/localization/LocalizationSubsystem.java";
		const call = /pose_estimator_\.addVisionMeasurement\([^;]*;/;
		const cases: Record<string, (t: string) => string> = {
			"commented out (line comments)": (t) => t.replace(call, (m) => m.split("\n").map((l) => `// ${l}`).join("\n")),
			"commented out (block comment lines)": (t) => t.replace(call, (m) => `/*\n * ${m.split("\n").join("\n * ")}\n */`),
			"mentioned only in a trailing comment": (t) => t.replace(call, "// pose_estimator_.addVisionMeasurement(a, b, c);"),
			"the time now instead of the picture's": (t) => t.replace("measurement.getTimestamp()", "Timer.getFPGATimestamp()"),
			"no standard deviations": (t) =>
				t.replace(call, "pose_estimator_.addVisionMeasurement(measurement.getPose(), measurement.getTimestamp());"),
			"not the camera's pose": (t) => t.replace("measurement.getPose(),\n", "new Pose2d(),\n"),
		};
		for (const [what, change] of Object.entries(cases)) {
			const project = await solved();
			try {
				edit(project, LOC, change);
				expect(L.verify(project, "vision-added").exitCode, what).not.toBe(0);
			} finally {
				await rm(project, { recursive: true, force: true });
			}
		}
		// other formatting is fine
		const project = await solved();
		try {
			edit(project, LOC, (t) =>
				t.replace(call, "pose_estimator_.addVisionMeasurement(measurement.getPose(), measurement.getTimestamp(), VecBuilder.fill(0.1, 0.1, 0.1)); // fine"),
			);
			expect(L.verify(project, "vision-added").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 60_000);
});

describe("robot-computer-vision checkpoint list", () => {
	test("is the final set, in order", () => {
		const lesson = JSON.parse(readFileSync(join(L.dir, "lesson.json"), "utf8")) as {
			checkpoints: { id: string; verifier: { type: string; topic?: string } }[];
		};
		expect(lesson.checkpoints.map((c) => c.id)).toEqual([
			"vision-added",
			"estimate-accurate",
			"aim-state-written",
			"aim-converges",
			"aim-no-overshoot",
		]);
		const topics = lesson.checkpoints.filter((c) => c.verifier.type === "nt4-value").map((c) => c.verifier.topic);
		expect(topics).toEqual([
			"/AdvantageKit/RealOutputs/Check/Localization/Accurate",
			"/AdvantageKit/RealOutputs/Check/Aim/Settled",
			"/AdvantageKit/RealOutputs/Check/Aim/OvershootDegrees",
		]);
	});

	test("the checks the nt4-value checkpoints read are published by the provided lesson/ code", () => {
		const src = (f: string) => readFileSync(join(L.dir, "project", "src/main/java/lesson", f), "utf8");
		expect(src("LocalizationChecks.java")).toContain('"Check/Localization/Accurate"');
		expect(src("AimChecks.java")).toContain('"Check/Aim/Settled"');
		expect(src("AimChecks.java")).toContain('"Check/Aim/OvershootDegrees"');
		const checks = src("Checks.java");
		expect(checks).toContain("localization.update()");
		expect(checks).toContain("aim.update()");
	});

	test("the solution overlay only adds files that exist in the starter", () => {
		for (const file of [
			...AIM_FILES,
			"subsystems/localization/LocalizationSubsystem.java",
			"subsystems/localization/LocalizationConstants.java",
		]) {
			expect(existsSync(join(L.dir, "solution", ROBOT_SRC, file)), `solution/${file}`).toBe(true);
			expect(existsSync(join(L.dir, "project", ROBOT_SRC, file)), `project/${file}`).toBe(true);
		}
	});
});

// Compiling needs Gradle and the offline dependency cache (MWLib, PhotonLib, WPILib), so it only
// runs when asked: LESSON_GRADLE=1 bun test lessons/robot-computer-vision
describe.skipIf(!hasJdk || !process.env.LESSON_GRADLE)("robot-computer-vision compiles", () => {
	for (const solution of [false, true]) {
		test(`${solution ? "solution" : "starter"} compiles`, async () => {
			const project = await L.makeProject();
			try {
				if (solution) await L.overlaySolution(project);
				const result = run(project, ["./gradlew", "compileJava", "--offline", "-Pskip-inspector-wrapper"]);
				expect(result.text).toContain("BUILD SUCCESSFUL");
			} finally {
				await rm(project, { recursive: true, force: true });
			}
		}, 300_000);
	}
});
