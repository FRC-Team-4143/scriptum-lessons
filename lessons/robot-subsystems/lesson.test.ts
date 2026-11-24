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
	test("robot-subsystems: registration, OI buttons and shoot logic fail fresh, pass once solved", async () => {
		await L.roundTrip(["shooter-registered", "oi-buttons", "shoot-logic"],
			["RobotContainer.java", "OI.java", "subsystems/shooter/ShooterSubsystem.java", "subsystems/shooter/ShooterConstants.java"],
		);
	}, 30_000);

	// The shoot check runs updateLogic() against stand-ins and judges what the flywheel was told, so
	// any logic that behaves the same passes (Cole, 2026-11-24: students wrote their own versions).
	const SHOOTER_FILES = ["subsystems/shooter/ShooterSubsystem.java", "subsystems/shooter/ShooterConstants.java"];
	const SOLVED_IF = /    if \(OI\.getShootButton\(\)\) \{\s*flywheel_\.setTargetDutyCycle\(CONSTANTS\.SHOOT_DUTY_CYCLE\);\s*\} else \{\s*flywheel_\.setTargetDutyCycle\(0\.0\);\s*\}/;

	const shootLogicWith = async (logic: string) => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, SHOOTER_FILES);
			const file = join(project, ROBOT_SRC, "subsystems/shooter/ShooterSubsystem.java");
			const text = await readFile(file, "utf8");
			expect(text).toMatch(SOLVED_IF);
			await writeFile(file, text.replace(SOLVED_IF, logic), "utf8");
			return L.verify(project, "shoot-logic");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	};

	test.each([
		["a ternary", "    flywheel_.setTargetDutyCycle(OI.getShootButton() ? CONSTANTS.SHOOT_DUTY_CYCLE : 0.0);"],
		["a default, then an if", "    double duty = 0;\n    if (OI.getShootButton()) duty = CONSTANTS.SHOOT_DUTY_CYCLE;\n    flywheel_.setTargetDutyCycle(duty);"],
		["stop first, then an if", "    flywheel_.setTargetDutyCycle(0.0);\n    if (OI.getShootButton()) {\n      flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE);\n    }"],
		["a flipped if", "    if (!OI.getShootButton()) { flywheel_.setTargetDutyCycle(0); } else { flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE); }"],
	])("robot-subsystems: shoot logic written as %s passes", async (_name, logic) => {
		const result = await shootLogicWith(logic);
		expect({ text: result.text, code: result.exitCode }).toEqual({ text: result.text, code: 0 });
	}, 30_000);

	test.each([
		["no else", "    if (OI.getShootButton()) {\n      flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE);\n    }", "released"],
		["the same power both ways", "    if (OI.getShootButton()) {\n      flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE);\n    } else {\n      flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE);\n    }", "released"],
		["the branches swapped", "    if (OI.getShootButton()) {\n      flywheel_.setTargetDutyCycle(0.0);\n    } else {\n      flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE);\n    }", "held"],
		["a velocity instead of a duty cycle", "    if (OI.getShootButton()) {\n      flywheel_.setTargetVelocity(300);\n    } else {\n      flywheel_.setTargetDutyCycle(0.0);\n    }", "duty cycle"],
		["the answer commented out", "    // if (OI.getShootButton()) {\n    //   flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE);\n    // } else {\n    //   flywheel_.setTargetDutyCycle(0.0);\n    // }", "held"],
		["a missing semicolon", "    if (OI.getShootButton()) {\n      flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE)\n    } else {\n      flywheel_.setTargetDutyCycle(0.0);\n    }", "doesn't compile"],
	])("robot-subsystems: shoot logic with %s fails", async (_name, logic, says) => {
		const result = await shootLogicWith(logic);
		expect(result.exitCode).not.toBe(0);
		expect(result.text).toContain(says);
	}, 30_000);

	test("robot-subsystems: shoot logic works before the roller is finished, and asks for the OI import", async () => {
		const project = await L.makeProject();
		try {
			const file = join(project, ROBOT_SRC, "subsystems/shooter/ShooterSubsystem.java");
			let text = (await readFile(file, "utf8")).replace(
				"    // TODO: write the shoot button logic.",
				"    flywheel_.setTargetDutyCycle(OI.getShootButton() ? CONSTANTS.SHOOT_DUTY_CYCLE : 0.0);\n    //",
			);
			await writeFile(file, text, "utf8");
			const missing = L.verify(project, "shoot-logic");
			expect(missing.exitCode).not.toBe(0);
			expect(missing.text).toContain("import frc.robot.OI;");
			text = text.replace("import java.util.List;", "import frc.robot.OI;\nimport java.util.List;");
			await writeFile(file, text, "utf8");
			const result = L.verify(project, "shoot-logic");
			expect({ text: result.text, code: result.exitCode }).toEqual({ text: result.text, code: 0 });
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test.each([
		["getRawButton", "getRightBumperButton()", "getRawButton(6)", 0],
		["an if that returns true", "return driver_controller_.getRightBumperButton();", "if (driver_controller_.getRightBumperButton()) {\n      return true;\n    }\n    return false;", 0],
		["the bumpers swapped", "getRightBumperButton()", "getLeftBumperButton()", 1],
		["always true", "return driver_controller_.getRightBumperButton();", "return true;", 1],
	])("robot-subsystems: OI buttons written with %s", async (_name, from, to, code) => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, ["OI.java"]);
			const file = join(project, ROBOT_SRC, "OI.java");
			const text = await readFile(file, "utf8");
			expect(text).toContain(from);
			await writeFile(file, text.replace(from, to), "utf8");
			const result = L.verify(project, "oi-buttons");
			expect(result.exitCode === 0 ? 0 : 1).toBe(code);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("robot-subsystems: registering the shooter through a variable counts", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, ["RobotContainer.java"]);
			const file = join(project, ROBOT_SRC, "RobotContainer.java");
			const text = (await readFile(file, "utf8")).replace(
				"registerSubsystem(ShooterSubsystem.getInstance());",
				"ShooterSubsystem shooter = ShooterSubsystem.getInstance();\n    registerSubsystem(shooter);",
			);
			await writeFile(file, text, "utf8");
			expect(L.verify(project, "shooter-registered").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
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
