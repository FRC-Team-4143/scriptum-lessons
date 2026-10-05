// Compiles and runs each Java Basics checkpoint's hidden check class against
// the real starter (expect every checkpoint to fail) and a known-solved
// version of the same file (expect every checkpoint to pass) - the same
// round trip CheckpointManager drives inside a Scriptum workspace
// container, using the host's own javac/java instead of Docker. Skipped
// entirely when no JDK is on PATH. Ported from Scriptum's
// scripts/lessons/java-basics.test.ts when these modules moved from the
// bundled catalog to this repo - see Scriptum's
// docs/decisions/044-remote-catalog-checkpoints.md.
import { describe, expect, test } from "bun:test";
import { cp, mkdtemp, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = resolve(fileURLToPath(new URL("..", import.meta.url)));

const hasJdk = Boolean(Bun.which("javac") && Bun.which("java"));

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

function verify(project: string, checkpointsDir: string, checkpointId: string) {
	return run(project, [
		"bash",
		join(
			repoRoot,
			"checkpoints",
			checkpointsDir,
			"verify",
			`${checkpointId}.sh`,
		),
		project,
	]);
}

async function writeSrc(
	project: string,
	fileName: string,
	content: string,
): Promise<void> {
	await writeFile(join(project, "src", fileName), content, "utf8");
}

async function expectRoundTrip(
	moduleId: string,
	checkpointIds: string[],
	solvedMainJava: string,
): Promise<void> {
	const project = await makeProject(moduleId);
	try {
		for (const id of checkpointIds) {
			expect(verify(project, moduleId, id).exitCode).not.toBe(0);
		}

		await writeSrc(project, "Main.java", solvedMainJava);

		for (const id of checkpointIds) {
			expect(verify(project, moduleId, id).exitCode).toBe(0);
		}
	} finally {
		await rm(project, { recursive: true, force: true });
	}
}

describe.skipIf(!hasJdk)("Java Basics checkpoints", () => {
	test("hello-world: the untouched starter passes, a broken println fails", async () => {
		const project = await makeProject("hello-world");
		try {
			expect(
				verify(project, "hello-world", "prints-hello-world").exitCode,
			).toBe(0);

			await writeSrc(
				project,
				"Main.java",
				`public class Main {
  public static void main(String[] args) {
    System.out.println("Hi there!");
  }
}
`,
			);
			expect(
				verify(project, "hello-world", "prints-hello-world").exitCode,
			).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("java-variables: every checkpoint fails fresh, passes once solved", async () => {
		await expectRoundTrip(
			"java-variables",
			[
				"local-variables",
				"team-name-constant",
				"alliance-enum",
				"match-period-enum",
				"max-speed-constant",
			],
			`public class Main {
    public static final String TEAM_NAME = "Team 4143";
    public static final double MAX_SPEED = 5.0;
    enum ALLIANCE { RED, BLUE }
    enum MATCH_PERIOD { AUTONOMOUS, TELEOP, ENDGAME }
    public static void main(String[] args) {
        int team_number = 4143;
        double pi = 3.14;
        boolean robot_is_on = true;
        String message = "I am a string!";
        System.out.println("Team number: " + team_number);
        System.out.println("Pi: " + pi);
        System.out.println("Robot is on: " + robot_is_on);
        System.out.println("Message: " + message);
    }
}
`,
		);
	});

	test("java-operators: every checkpoint fails fresh, passes once solved", async () => {
		await expectRoundTrip(
			"java-operators",
			["add-ints", "inches-to-meters", "is-even"],
			`public class Main {
    public static int addInts(int a, int b) { return a + b; }
    public static double inchesToMeters(double inches) { return inches * 0.0254; }
    public static boolean isEven(int value) { return value % 2 == 0; }
    public static void main(String[] args) {}
}
`,
		);
	});

	test("java-conditions: every checkpoint fails fresh, passes once solved", async () => {
		await expectRoundTrip(
			"java-conditions",
			["battery-status", "drive-mode-name"],
			`public class Main {
    public static String batteryStatus(double percent) {
        if (percent == 0) return "EMPTY";
        if (percent < 50) return "LOW";
        if (percent < 90) return "MEDIUM";
        return "FULL";
    }
    public static String driveModeName(int mode) {
        switch (mode) {
            case 1: return "Tank";
            case 2: return "Arcade";
            case 3: return "Swerve";
            case 4: return "Field-Oriented Swerve";
            default: return "Unknown mode";
        }
    }
    public static void main(String[] args) {}
}
`,
		);
	});

	test("java-loops: every checkpoint fails fresh, passes once solved", async () => {
		await expectRoundTrip(
			"java-loops",
			["sum-loop", "count-divisible-by-three"],
			`public class Main {
    public static int sumTo(int n) {
        int total = 0;
        for (int i = 1; i <= n; i++) total += i;
        return total;
    }
    public static int countDivisibleByThree(int n) {
        int count = 0;
        for (int i = 1; i <= n; i++) if (i % 3 == 0) count++;
        return count;
    }
    public static void main(String[] args) {}
}
`,
		);
	});

	test("java-arrays: every checkpoint fails fresh, passes once solved", async () => {
		await expectRoundTrip(
			"java-arrays",
			["array-max", "array-average", "array-double-all", "array-contains"],
			`public class Main {
    public static int max(int[] values) {
        int largest = values[0];
        for (int i = 1; i < values.length; i++) if (values[i] > largest) largest = values[i];
        return largest;
    }
    public static double average(int[] values) {
        int total = 0;
        for (int i = 0; i < values.length; i++) total += values[i];
        return (double) total / values.length;
    }
    public static int[] doubleAll(int[] values) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) result[i] = values[i] * 2;
        return result;
    }
    public static boolean contains(int[] values, int target) {
        for (int i = 0; i < values.length; i++) if (values[i] == target) return true;
        return false;
    }
    public static void main(String[] args) {}
}
`,
		);
	});

	test("java-methods: every checkpoint fails fresh, passes once solved", async () => {
		await expectRoundTrip(
			"java-methods",
			["stop-motor-void", "sum-three", "team-name"],
			`public class Main {
    public static void stopMotor() { System.out.println("MOTOR STOPPED"); }
    public static double sumThree(double a, double b, double c) { return a + b + c; }
    public static String teamName() { return "MARS/WARS"; }
    public static void main(String[] args) {}
}
`,
		);
	});

	test("java-classes-objects: every checkpoint fails fresh, passes once solved", async () => {
		const project = await makeProject("java-classes-objects");
		try {
			for (const id of [
				"constructor-direction",
				"enable-required",
				"disable",
				"independent-objects",
			]) {
				expect(verify(project, "java-classes-objects", id).exitCode).not.toBe(
					0,
				);
			}

			await writeSrc(
				project,
				"MotorController.java",
				`public class MotorController {
    private double current_motor_speed_;
    private double target_motor_speed_;
    private boolean motor_enabled_;
    private double motor_direction_;

    public MotorController(boolean is_reversed) {
        target_motor_speed_ = 0.0;
        motor_enabled_ = false;
        motor_direction_ = (is_reversed) ? -1.0 : 1.0;
    }

    public void setMotorSpeed(double speed) {
        if (!motor_enabled_) return;
        target_motor_speed_ = speed * motor_direction_;
    }

    public double getMotorSpeed() { return target_motor_speed_; }
    public void enableMotor() { motor_enabled_ = true; }
    public void disableMotor() { target_motor_speed_ = 0.0; motor_enabled_ = false; }
}
`,
			);
			for (const id of [
				"constructor-direction",
				"enable-required",
				"disable",
				"independent-objects",
			]) {
				expect(verify(project, "java-classes-objects", id).exitCode).toBe(0);
			}
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});
});
