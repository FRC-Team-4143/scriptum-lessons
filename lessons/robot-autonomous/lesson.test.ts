// robot-autonomous (Choreo Autonomous): the starter ships a Differential Choreo project with the
// field targets as named poses and NO paths; solution/ adds the two paths (ToPickup and ToScore)
// and the finished Autos.java. The checkpoints are all nt4-value ones (they need a running
// simulator, so they are not covered here; they read the "Check/..." topics that the provided
// LessonChecks.java publishes). What can be checked without a simulator is checked below.
//
// TODO (next chunks): script checkpoints for "paths drawn" / "auto sequence" with round trips.
import { describe, expect, test } from "bun:test";
import { existsSync, readdirSync, readFileSync } from "node:fs";
import { join } from "node:path";
import { hasJdk, lessonKit, run } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);
const choreo = (root: string) => join(root, "src/main/deploy/choreo");
const readJson = (file: string) => JSON.parse(readFileSync(file, "utf8"));
const IN_TO_M = 0.0254;

describe("robot-autonomous Choreo project", () => {
	const chor = readJson(join(choreo(join(L.dir, "project")), "robot.chor"));

	test("the project is Differential and matches the drivetrain constants", () => {
		expect(chor.type).toBe("Differential");
		expect(chor.config.differentialTrackWidth.val).toBeCloseTo(24 * IN_TO_M, 4);
		expect(chor.config.radius.val).toBeCloseTo(3 * IN_TO_M, 4);
		expect(chor.config.gearing.val).toBeCloseTo(8.45, 4);
		expect(chor.config.mass.val).toBeCloseTo(50, 4);
		const constants = readFileSync(
			join(L.dir, "project/src/main/java/frc/robot/subsystems/drive/DrivetrainConstants.java"),
			"utf8",
		);
		expect(constants).toContain("Units.inchesToMeters(24.0)");
		expect(constants).toContain("GEAR_RATIO = 8.45");
		expect(constants).toContain("ROBOT_MASS_KG = 50.0");
	});

	test("FieldTargets.java has the same poses as the .chor", () => {
		const java = readFileSync(join(L.dir, "project/src/main/java/frc/robot/FieldTargets.java"), "utf8");
		const names: Record<string, string> = { START: "Start", PICKUP: "Pickup", SCORE_SPOT: "ScoreSpot" };
		for (const [constant, pose] of Object.entries(names)) {
			const m = java.match(new RegExp(`${constant} =\\s*new Pose2d\\(([-\\d.]+), ([-\\d.]+), Rotation2d.fromDegrees\\(([-\\d.]+)\\)\\)`));
			expect(m, `${constant} in FieldTargets.java`).not.toBeNull();
			const p = chor.variables.poses[pose];
			expect(Number(m![1])).toBeCloseTo(p.x.val, 4);
			expect(Number(m![2])).toBeCloseTo(p.y.val, 4);
			expect((Number(m![3]) * Math.PI) / 180).toBeCloseTo(p.heading.val, 4);
		}
	});

	test("the starter has no paths drawn", () => {
		const dir = choreo(join(L.dir, "project"));
		expect(readdirSync(dir).filter((f) => f.endsWith(".traj"))).toEqual([]);
	});

	test("the solution's paths are Differential, chain Start -> Pickup -> ScoreSpot, and are loadable", () => {
		const dir = choreo(join(L.dir, "solution"));
		const poses = chor.variables.poses;
		const expectEnds = (name: string, from: any, to: any) => {
			const traj = readJson(join(dir, `${name}.traj`));
			expect(traj.version).toBe(3); // the schema ChoreoLib 2026.0.1 loads
			expect(traj.trajectory.sampleType).toBe("Differential");
			const s = traj.trajectory.samples;
			expect(s.length).toBeGreaterThan(10);
			expect(s[0].t).toBe(0);
			expect(s[0].x).toBeCloseTo(from.x.val, 2);
			expect(s[0].y).toBeCloseTo(from.y.val, 2);
			expect(s[s.length - 1].x).toBeCloseTo(to.x.val, 2);
			expect(s[s.length - 1].y).toBeCloseTo(to.y.val, 2);
			// ChoreoLib's differential samples: left/right wheel speeds and a turning speed.
			for (const key of ["vl", "vr", "omega", "heading"]) expect(typeof s[0][key]).toBe("number");
			return traj;
		};
		expectEnds("ToPickup", poses.Start, poses.Pickup);
		const toScore = expectEnds("ToScore", poses.Pickup, poses.ScoreSpot);
		// the "Shoot" event marker the solution's Autos.java binds
		expect(toScore.events.map((e: any) => e.name)).toContain("Shoot");
	});

	test("the solution lays over the starter without adding anything else", async () => {
		const project = await L.makeProject();
		await L.overlaySolution(project);
		expect(existsSync(join(choreo(project), "ToPickup.traj"))).toBe(true);
		expect(existsSync(join(choreo(project), "ToScore.traj"))).toBe(true);
		const autos = readFileSync(join(project, "src/main/java/frc/robot/autos/Autos.java"), "utf8");
		expect(autos).toContain('loadTrajectory("ToPickup")');
		expect(autos).toContain('loadTrajectory("ToScore")');
	});
});

// Compiling needs Gradle and the offline dependency cache (MWLib, ChoreoLib, WPILib), so it only
// runs when asked: LESSON_GRADLE=1 bun test lessons/robot-autonomous
describe.skipIf(!hasJdk || !process.env.LESSON_GRADLE)("robot-autonomous compiles", () => {
	for (const solved of [false, true]) {
		test(`${solved ? "solution" : "starter"} compiles`, async () => {
			const project = await L.makeProject();
			if (solved) await L.overlaySolution(project);
			const result = run(project, ["./gradlew", "compileJava", "--offline", "-Pskip-inspector-wrapper"]);
			expect(result.text).toContain("BUILD SUCCESSFUL");
		}, 300_000);
	}
});
