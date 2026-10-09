// robot-drive-math: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { readFile, rm, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { hasJdk, lessonKit, ROBOT_SRC } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("robot-drive-math script checkpoints", () => {
	test("robot-drive-math: every DriveMath checkpoint fails fresh, passes once solved", async () => {
		await L.roundTrip(["rotations-to-meters", "linear-speed", "angular-speed", "arcade-math", "average"],
			["DriveMath.java"],
		);
	}, 30_000);
});

// The student writes linearSpeed and angularSpeed (signatures included) themselves, while the
// drivetrain and Robot.java already call them.
describe("robot-drive-math: the student creates two methods", () => {
	const read = (...parts: string[]) => readFile(join(L.dir, ...parts), "utf8");
	const mech = ["project", ROBOT_SRC, "mechanisms", "DifferentialDriveMech.java"];

	test("the starter has no linearSpeed or angularSpeed, but the code already calls them", async () => {
		const starter = await read("project", ROBOT_SRC, "DriveMath.java");
		expect(starter).not.toMatch(/static\s+double\s+(linearSpeed|angularSpeed)\s*\(/);
		for (const kept of ["rotationsToMeters", "arcadeToWheelSpeeds", "average"]) {
			expect(starter).toMatch(new RegExp(`static\\s+\\S+\\s+${kept}\\s*\\(`));
		}
		const caller = await read(...mech);
		expect(caller).toContain("DriveMath.linearSpeed(");
		expect(caller).toContain("DriveMath.angularSpeed(");
	});

	test("the drivetrain uses the friction physics, in the starter and the solution", async () => {
		for (const file of [await read(...mech), await read("solution", "DifferentialDriveMech.java")]) {
			expect(file).toContain("STATIC_FRICTION_VOLTS = 0.25");
			expect(file).toContain("SCRUB_VOLTS_PER_METER_PER_SECOND = 4.5");
			expect(file).toContain("minusStaticFriction(left_volts)");
		}
	});

	test.skipIf(!hasJdk)("a misspelled method name does not pass its checkpoint", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, ["DriveMath.java"]);
			const file = join(project, ROBOT_SRC, "DriveMath.java");
			const text = (await readFile(file, "utf8")).replace("double linearSpeed(", "double linearspeed(");
			await writeFile(file, text, "utf8");
			expect(L.verify(project, "linear-speed").exitCode).not.toBe(0);
			expect(L.verify(project, "angular-speed").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);
});
