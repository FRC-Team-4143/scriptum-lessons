// robot-state-machines: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// and AimChecks.java publish - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { rm } from "node:fs/promises";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

const AIM_FILES = [
	"subsystems/drive/DrivetrainConstants.java",
	"subsystems/drive/DrivetrainSubsystem.java",
	"subsystems/drive/DrivetrainCommands.java",
];

describe.skipIf(!hasJdk)("robot-state-machines script checkpoints", () => {
	test("robot-state-machines: button bindings fail fresh, pass once solved", async () => {
		await L.roundTrip(["bindings"],
			["OI.java", "subsystems/shooter/ShooterCommands.java"],
		);
	}, 30_000);

	test("robot-state-machines: the aim state fails fresh, passes once solved", async () => {
		await L.roundTrip(["aim-state-written"], AIM_FILES);
	}, 30_000);

	test("robot-state-machines: aim-state-written needs every part, not just the enum", async () => {
		for (const missing of AIM_FILES) {
			const project = await L.makeProject();
			try {
				await L.applySolution(project, AIM_FILES.filter((f) => f !== missing));
				expect(L.verify(project, "aim-state-written").exitCode, `without ${missing}`).not.toBe(0);
			} finally {
				await rm(project, { recursive: true, force: true });
			}
		}
	}, 30_000);
});
