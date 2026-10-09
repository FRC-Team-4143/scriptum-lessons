// robot-state-machines: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { rm } from "node:fs/promises";
import { hasJdk, lessonKit, run } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("robot-state-machines script checkpoints", () => {
	test("robot-state-machines: button bindings fail fresh, pass once solved", async () => {
		await L.roundTrip(["bindings"],
			["OI.java", "subsystems/shooter/ShooterCommands.java"],
		);
	}, 30_000);
});

// Compiling needs Gradle and the offline dependency cache (MWLib, WPILib), so it only runs when
// asked: LESSON_GRADLE=1 bun test lessons/robot-state-machines
describe.skipIf(!hasJdk || !process.env.LESSON_GRADLE)("robot-state-machines compiles", () => {
	for (const solution of [false, true]) {
		test(`${solution ? "solution" : "starter"} compiles`, async () => {
			const project = await L.makeProject();
			try {
				// The solution here is flat files, laid over the starter where each belongs.
				if (solution) {
					await L.applySolution(project, [
						"OI.java",
						"subsystems/shooter/ShooterCommands.java",
						"subsystems/shooter/ShooterConstants.java",
						"subsystems/shooter/ShooterSubsystem.java",
					]);
				}
				const result = run(project, ["./gradlew", "compileJava", "--offline", "-Pskip-inspector-wrapper"]);
				expect(result.text).toContain("BUILD SUCCESSFUL");
			} finally {
				await rm(project, { recursive: true, force: true });
			}
		}, 300_000);
	}
});
