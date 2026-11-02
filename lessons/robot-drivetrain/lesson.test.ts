// robot-drivetrain: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { readFile, rm, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { hasJdk, lessonKit, ROBOT_SRC } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe("robot-drivetrain starts with the right motor not inverted", () => {
	const line = (text: string) => text.split("\n").find((l) => l.includes("RIGHT_MOTORS")) ?? "";

	test("the starter leaves it wrong, so the robot spins until the student fixes it", async () => {
		const text = await readFile(new URL("./project/src/main/java/frc/robot/Constants.java", import.meta.url), "utf8");
		expect(line(text)).toContain("motor(2, false)");
	});

	test("the solution inverts it", async () => {
		const text = await readFile(new URL("./solution/Constants.java", import.meta.url), "utf8");
		expect(line(text)).toContain("motor(2, true)");
	});
});

describe.skipIf(!hasJdk)("robot-drivetrain script checkpoints", () => {
	test("robot-drivetrain: teleop-code fails fresh, passes once solved", async () => {
		await L.roundTrip(["teleop-code"], ["Robot.java"]);
	}, 30_000);

	test("robot-drivetrain: teleop-code needs arcade arithmetic, not just variables", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, ["Robot.java"]);
			const file = join(project, ROBOT_SRC, "Robot.java");
			const text = (await readFile(file, "utf8")).replace(
				"double right_speed = forward - turn;",
				"double right_speed = forward;",
			);
			await writeFile(file, text, "utf8");
			const result = L.verify(project, "teleop-code");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("arithmetic");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);
});
