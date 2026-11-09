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

// The advanced challenge: each of the things that separate a real controller from the formula has to
// be there, and the shooter has to use it.
describe.skipIf(!hasJdk)("robot-control-theory advanced challenge", () => {
	const PID = "subsystems/shooter/FlywheelPid.java";
	const SHOOTER = "subsystems/shooter/ShooterSubsystem.java";
	async function broken(file: string, from: string, to: string) {
		const project = await L.makeProject();
		try {
			await L.overlayChallenge(project);
			const path = join(project, ROBOT_SRC, file);
			const before = await readFile(path, "utf8");
			expect(before).toContain(from);
			await writeFile(path, before.replace(from, to), "utf8");
			return L.verify(project, "challenge-own-pid");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}

	test("the required solution doesn't pass", async () => {
		const project = await L.makeProject();
		try {
			await L.applySolution(project, [SHOOTER]);
			expect(L.verify(project, "challenge-own-pid").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("no anti-windup doesn't pass", async () => {
		const result = await broken(PID, "if (close && !stuck) {", "if (true) {");
		expect(result.exitCode).not.toBe(0);
		expect(result.text).toContain("windup");
	}, 30_000);

	test("derivative on the error, which kicks when the target moves, doesn't pass", async () => {
		const project = await L.makeProject();
		try {
			await L.overlayChallenge(project);
			const path = join(project, ROBOT_SRC, PID);
			const text = (await readFile(path, "utf8"))
				.replace("slope = (measured - last_measured_) / dt_seconds;", "slope = (-error - last_measured_) / dt_seconds;")
				.replace("last_measured_ = measured;", "last_measured_ = -error;");
			await writeFile(path, text, "utf8");
			const result = L.verify(project, "challenge-own-pid");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("kick");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("no clamp doesn't pass", async () => {
		const result = await broken(PID, "return Math.max(-MAX_VOLTS, Math.min(MAX_VOLTS, volts));", "return volts;");
		expect(result.exitCode).not.toBe(0);
	}, 30_000);

	test("a PID the shooter never uses doesn't pass", async () => {
		const result = await broken(SHOOTER, "my_pid_.calculate(", "my_pid_.calculateOld(");
		expect(result.exitCode).not.toBe(0);
	}, 30_000);
});
