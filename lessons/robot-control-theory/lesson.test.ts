// robot-control-theory: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { readFile, rm, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { hasJdk, lessonKit, ROBOT_SRC } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("robot-control-theory script checkpoints", () => {
	test("robot-control-theory: bang-bang, feedforward and PID checks fail fresh, pass once solved", async () => {
		await L.roundTrip(["bang-bang-written", "feedforward-written", "pid-selected"],
			[
				"subsystems/shooter/ShooterSubsystem.java",
				"subsystems/shooter/ShooterConstants.java",
			],
		);
	}, 30_000);

	test("robot-control-theory: feedback-free PID gains (kP of zero) fail pid-selected", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, [
				"subsystems/shooter/ShooterConstants.java",
			]);
			const file = join(project, ROBOT_SRC, "subsystems/shooter/ShooterConstants.java");
			const text = (await readFile(file, "utf8")).replace(
				/FLYWHEEL_KP = [0-9.]+;/,
				"FLYWHEEL_KP = 0.0;",
			);
			await writeFile(file, text, "utf8");
			const result = L.verify(project, "pid-selected");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("kP");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);
});
