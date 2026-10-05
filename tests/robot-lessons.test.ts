// Round-trips the FRC Robot track's script checkpoints: every script verifier must fail against the
// untouched starter and pass against the reference solution in tests/solutions/<module>/. The
// nt4-value checkpoints are not covered here (they need a running simulator); they read the
// "Check/..." topics that the provided LessonChecks.java publishes - see each module's README.
import { describe, expect, test } from "bun:test";
import { cp, mkdtemp, readFile, rm, writeFile } from "node:fs/promises";
import { existsSync } from "node:fs";
import { tmpdir } from "node:os";
import { join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = resolve(fileURLToPath(new URL("..", import.meta.url)));
const hasJdk = Boolean(Bun.which("javac") && Bun.which("java"));
const ROBOT_SRC = "src/main/java/frc/robot";

const ROBOT_MODULES = [
	"robot-drivetrain",
	"robot-sensors",
	"robot-methods",
	"robot-oop-wpilib",
	"robot-subsystems",
	"robot-state-machines",
	"robot-control-theory",
	"robot-autonomous",
];

function run(cwd: string, args: string[]): { exitCode: number; text: string } {
	const result = Bun.spawnSync(args, { cwd, stdout: "pipe", stderr: "pipe" });
	return {
		exitCode: result.exitCode ?? 1,
		text: `${result.stdout.toString()}${result.stderr.toString()}`,
	};
}

async function makeProject(moduleId: string): Promise<string> {
	const dir = await mkdtemp(join(tmpdir(), `frc-${moduleId}-`));
	await cp(resolve(repoRoot, "modules", moduleId), dir, { recursive: true });
	return dir;
}

function verify(project: string, moduleId: string, checkpointId: string) {
	return run(project, [
		"bash",
		join(repoRoot, "checkpoints", moduleId, "verify", `${checkpointId}.sh`),
		project,
	]);
}

async function applySolution(
	project: string,
	moduleId: string,
	files: string[],
): Promise<void> {
	// Each entry is the path inside src/main/java/frc/robot; the solution file is stored by basename.
	for (const file of files) {
		const base = file.split("/").pop() as string;
		await cp(
			resolve(repoRoot, "tests/solutions", moduleId, base),
			join(project, ROBOT_SRC, file),
		);
	}
}

async function expectRoundTrip(
	moduleId: string,
	checkpointIds: string[],
	solutionFiles: string[],
): Promise<void> {
	const project = await makeProject(moduleId);
	try {
		for (const id of checkpointIds) {
			expect(verify(project, moduleId, id).exitCode).not.toBe(0);
		}
		await applySolution(project, moduleId, solutionFiles);
		for (const id of checkpointIds) {
			const result = verify(project, moduleId, id);
			expect({ id, text: result.text, code: result.exitCode }).toEqual({
				id,
				text: result.text,
				code: 0,
			});
		}
	} finally {
		await rm(project, { recursive: true, force: true });
	}
}

describe("FRC Robot track manifest", () => {
	test("every robot module has metadata, README, build files and checkpoints that resolve", async () => {
		const index = JSON.parse(
			await readFile(join(repoRoot, "modules.json"), "utf8"),
		);
		const ids = new Set<string>(index.modules.map((m: { id: string }) => m.id));
		expect(ids.has("robot-starter")).toBe(false);

		for (const id of ROBOT_MODULES) {
			expect(ids.has(id)).toBe(true);
			const meta = JSON.parse(
				await readFile(join(repoRoot, "modules-meta", `${id}.json`), "utf8"),
			);
			expect(meta.kind).toBe("robot");
			expect(existsSync(join(repoRoot, meta.subdir, "README.md"))).toBe(true);
			expect(existsSync(join(repoRoot, meta.subdir, "build.gradle"))).toBe(true);
			expect(
				existsSync(
					join(repoRoot, meta.subdir, "src/main/deploy/robots/SimBot.json"),
				),
			).toBe(true);
			for (const required of meta.requires ?? []) {
				expect(ids.has(required)).toBe(true);
			}
			for (const checkpoint of meta.checkpoints ?? []) {
				if (checkpoint.verifier.type === "script") {
					expect(existsSync(join(repoRoot, checkpoint.verifier.path))).toBe(true);
				} else {
					expect(checkpoint.verifier.topic).toStartWith("/AdvantageKit/RealOutputs/");
				}
			}
		}
	});

	test("the chain runs robot-drivetrain through robot-autonomous", async () => {
		const requires = async (id: string): Promise<string[]> =>
			JSON.parse(
				await readFile(join(repoRoot, "modules-meta", `${id}.json`), "utf8"),
			).requires ?? [];
		expect(await requires("robot-sensors")).toContain("robot-drivetrain");
		expect(await requires("robot-methods")).toContain("robot-sensors");
		expect(await requires("robot-oop-wpilib")).toContain("robot-methods");
		expect(await requires("robot-subsystems")).toContain("robot-oop-wpilib");
		expect(await requires("robot-state-machines")).toContain("robot-subsystems");
		expect(await requires("robot-control-theory")).toContain("robot-state-machines");
		expect(await requires("robot-autonomous")).toContain("robot-control-theory");
	});
});

describe.skipIf(!hasJdk)("FRC Robot script checkpoints", () => {
	test("robot-drivetrain: followers and teleop-code fail fresh, pass once solved", async () => {
		await expectRoundTrip(
			"robot-drivetrain",
			["followers", "teleop-code"],
			["Constants.java", "Robot.java"],
		);
	}, 30_000);

	test("robot-sensors: deadband fails fresh, passes once the constant and ifs are written", async () => {
		await expectRoundTrip("robot-sensors", ["deadband"], ["Robot.java"]);
	}, 30_000);

	test("robot-drivetrain: teleop-code needs arcade arithmetic, not just variables", async () => {
		const project = await makeProject("robot-drivetrain");
		try {
			await applySolution(project, "robot-drivetrain", ["Robot.java"]);
			const file = join(project, ROBOT_SRC, "Robot.java");
			const text = (await readFile(file, "utf8")).replace(
				"double rightSpeed = forward - turn;",
				"double rightSpeed = forward;",
			);
			await writeFile(file, text, "utf8");
			const result = verify(project, "robot-drivetrain", "teleop-code");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("arithmetic");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("robot-sensors: the deadband check needs an if on both sticks", async () => {
		const project = await makeProject("robot-sensors");
		try {
			await applySolution(project, "robot-sensors", ["Robot.java"]);
			const file = join(project, ROBOT_SRC, "Robot.java");
			const text = (await readFile(file, "utf8")).replace(
				/if \(Math\.abs\(turn\) < DEADBAND\) \{\s*turn = 0\.0;\s*\}/,
				"",
			);
			await writeFile(file, text, "utf8");
			expect(verify(project, "robot-sensors", "deadband").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("robot-methods: every DriveMath checkpoint fails fresh, passes once solved", async () => {
		await expectRoundTrip(
			"robot-methods",
			["rotations-to-meters", "linear-speed", "angular-speed", "arcade-math", "average"],
			["DriveMath.java"],
		);
	}, 30_000);

	test("robot-oop-wpilib: oi-class fails fresh, passes once OI answers the controller questions and Robot uses it", async () => {
		await expectRoundTrip(
			"robot-oop-wpilib",
			["oi-class"],
			["OI.java", "Robot.java"],
		);
	}, 30_000);

	test("robot-oop-wpilib: a Robot that still uses XboxController fails oi-class", async () => {
		const project = await makeProject("robot-oop-wpilib");
		try {
			await applySolution(project, "robot-oop-wpilib", ["OI.java"]);
			const result = verify(project, "robot-oop-wpilib", "oi-class");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("XboxController");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("robot-subsystems: registration and OI buttons fail fresh, pass once solved", async () => {
		await expectRoundTrip(
			"robot-subsystems",
			["shooter-registered", "oi-buttons"],
			["RobotContainer.java", "OI.java"],
		);
	}, 30_000);

	test("robot-subsystems: a commented-out registration does not count", async () => {
		const project = await makeProject("robot-subsystems");
		try {
			await applySolution(project, "robot-subsystems", ["RobotContainer.java"]);
			const file = join(project, ROBOT_SRC, "RobotContainer.java");
			const text = (await readFile(file, "utf8")).replace(
				"registerSubsystem(ShooterSubsystem.getInstance());",
				"// registerSubsystem(ShooterSubsystem.getInstance());",
			);
			await writeFile(file, text, "utf8");
			expect(verify(project, "robot-subsystems", "shooter-registered").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("robot-state-machines: button bindings fail fresh, pass once solved", async () => {
		await expectRoundTrip(
			"robot-state-machines",
			["bindings"],
			["OI.java", "subsystems/shooter/ShooterCommands.java"],
		);
	}, 30_000);

	test("robot-autonomous: chooser check fails fresh, passes once both autos are built and added", async () => {
		await expectRoundTrip(
			"robot-autonomous",
			["autos-in-chooser", "square-loop"],
			["RobotContainer.java", "autos/Autos.java"],
		);
	}, 30_000);

	test("robot-control-theory: bang-bang, feedforward and PID checks fail fresh, pass once solved", async () => {
		await expectRoundTrip(
			"robot-control-theory",
			["bang-bang-written", "feedforward-written", "pid-selected"],
			[
				"subsystems/shooter/ShooterSubsystem.java",
				"subsystems/shooter/ShooterConstants.java",
			],
		);
	}, 30_000);

	test("robot-control-theory: feedback-free PID gains (kP of zero) fail pid-selected", async () => {
		const project = await makeProject("robot-control-theory");
		try {
			await applySolution(project, "robot-control-theory", [
				"subsystems/shooter/ShooterConstants.java",
			]);
			const file = join(project, ROBOT_SRC, "subsystems/shooter/ShooterConstants.java");
			const text = (await readFile(file, "utf8")).replace(
				/FLYWHEEL_KP = [0-9.]+;/,
				"FLYWHEEL_KP = 0.0;",
			);
			await writeFile(file, text, "utf8");
			const result = verify(project, "robot-control-theory", "pid-selected");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("kP");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);
});
