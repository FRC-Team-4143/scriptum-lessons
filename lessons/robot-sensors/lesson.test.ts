// robot-sensors: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { readFile, rm, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { hasJdk, lessonKit, ROBOT_SRC } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe("robot-sensors starts with the gear ratio wrong", () => {
	const ratio = async (path: string) => {
		const text = await readFile(new URL(path, import.meta.url), "utf8");
		return text.match(/GEAR_RATIO = ([\d.]+);/)?.[1];
	};

	test("the starter says 1.0 and the solution has the real gearbox", async () => {
		expect(await ratio("./project/src/main/java/frc/robot/Constants.java")).toBe("1.0");
		expect(await ratio("./solution/Constants.java")).toBe("8.46");
	});
});

describe.skipIf(!hasJdk)("robot-sensors script checkpoints", () => {
	test("robot-sensors: deadband fails fresh, passes once the constant (in Constants.java) and ifs are written", async () => {
		await L.roundTrip(["deadband"], ["Robot.java", "Constants.java"]);
	}, 30_000);

	test("robot-sensors: the deadband check needs an if on both sticks", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, ["Robot.java", "Constants.java"]);
			const file = join(project, ROBOT_SRC, "Robot.java");
			const text = (await readFile(file, "utf8")).replace(
				/if \(Math\.abs\(turn\) < Constants\.DEADBAND\) \{\s*turn = 0\.0;\s*\}/,
				"",
			);
			await writeFile(file, text, "utf8");
			expect(L.verify(project, "deadband").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("robot-sensors: log-it fails fresh, passes once the three MwLog.log lines are written", async () => {
		await L.roundTrip(["log-it"], ["Robot.java"]);
	}, 30_000);

	test("robot-sensors: the log-it check wants each key spelled exactly, inside robotPeriodic()", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, ["Robot.java"]);
			const file = join(project, ROBOT_SRC, "Robot.java");
			const solved = await readFile(file, "utf8");

			await writeFile(file, solved.replace('"Robot/LinearSpeed"', '"Robot/Linearspeed"'), "utf8");
			const typo = L.verify(project, "log-it");
			expect(typo.exitCode).not.toBe(0);
			expect(typo.text).toContain("capitals");

			await writeFile(file, solved.replace('"Robot/Distance", distance_meters_', '"Robot/Distance", linear_speed'), "utf8");
			expect(L.verify(project, "log-it").exitCode).not.toBe(0);

			const line = 'MwLog.log("Robot/AngularSpeed", angular_speed);';
			const moved = solved.replace(line, "").replace("public void teleopPeriodic() {", `public void teleopPeriodic() {\n    ${line}`);
			await writeFile(file, moved, "utf8");
			expect(L.verify(project, "log-it").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("robot-sensors: the deadband check needs the constant in Constants.java", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, ["Robot.java"]);
			expect(L.verify(project, "deadband").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);
});
