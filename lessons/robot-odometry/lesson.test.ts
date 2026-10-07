// robot-odometry: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { rm } from "node:fs/promises";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("robot-odometry script checkpoints", () => {
	test("robot-odometry: oi-class fails fresh, passes once OI answers the controller questions and Robot uses it", async () => {
		await L.roundTrip(["oi-class"],
			["OI.java", "Robot.java"],
		);
	}, 30_000);

	test("robot-odometry: a Robot that still uses XboxController fails oi-class", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, ["OI.java"]);
			const result = L.verify(project, "oi-class");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("XboxController");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);
});
