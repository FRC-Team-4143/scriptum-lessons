// robot-subsystems: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { readFile, rm, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { hasJdk, lessonKit, ROBOT_SRC } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("robot-subsystems script checkpoints", () => {
	test("robot-subsystems: registration and OI buttons fail fresh, pass once solved", async () => {
		await L.roundTrip(["shooter-registered", "oi-buttons"],
			["RobotContainer.java", "OI.java"],
		);
	}, 30_000);

	test("robot-subsystems: a commented-out registration does not count", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, ["RobotContainer.java"]);
			const file = join(project, ROBOT_SRC, "RobotContainer.java");
			const text = (await readFile(file, "utf8")).replace(
				"registerSubsystem(ShooterSubsystem.getInstance());",
				"// registerSubsystem(ShooterSubsystem.getInstance());",
			);
			await writeFile(file, text, "utf8");
			expect(L.verify(project, "shooter-registered").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);
});
