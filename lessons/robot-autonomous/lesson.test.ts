// robot-autonomous (Autonomous): the starter ships a Differential Choreo project with the
// field targets as named poses and NO paths, and a PickupAndScore behavior tree that only drives the
// first path; solution/ adds the two paths (ToPickup and ToScore), the finished tree and the AIMING
// step in RobotContainer. The script checkpoints (paths drawn / named / going to the right places,
// the tree, the aim step) are round-tripped below, with negative cases.
// The nt4-value checkpoints need a running simulator, so they are not covered here; they read the
// "Check/..." topics that the provided LessonChecks.java and ShooterSubsystem publish.
import { describe, expect, test } from "bun:test";
import { existsSync, readdirSync, readFileSync, renameSync, rmSync as rmSyncQuiet, writeFileSync } from "node:fs";
import { rm } from "node:fs/promises";
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
		const names: Record<string, string> = { START: "Start", PICKUP: "Pickup", SCORE_SPOT: "ScoreSpot", GOAL: "Goal" };
		for (const [constant, pose] of Object.entries(names)) {
			const m = java.match(new RegExp(`${constant} =\\s*new Pose2d\\(([-\\d.]+), ([-\\d.]+), Rotation2d.fromDegrees\\(([-\\d.]+)\\)\\)`));
			expect(m, `${constant} in FieldTargets.java`).not.toBeNull();
			const p = chor.variables.poses[pose];
			expect(Number(m![1])).toBeCloseTo(p.x.val, 4);
			expect(Number(m![2])).toBeCloseTo(p.y.val, 4);
			expect((Number(m![3]) * Math.PI) / 180).toBeCloseTo(p.heading.val, 4);
		}
	});

	test("from the scoring spot the turn to face the goal is real, but not a U-turn", () => {
		const { ScoreSpot, Goal } = chor.variables.poses;
		const toGoal = Math.atan2(Goal.y.val - ScoreSpot.y.val, Goal.x.val - ScoreSpot.x.val);
		let turn = ((toGoal - ScoreSpot.heading.val) * 180) / Math.PI;
		turn = ((((turn + 180) % 360) + 360) % 360) - 180;
		expect(Math.abs(turn)).toBeGreaterThan(60);
		expect(Math.abs(turn)).toBeLessThan(150);
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
		// no event markers are required: the shot comes after the aim step, not at a marker
		expect(toScore.events).toEqual([]);
	});

	test("the solution lays over the starter without adding anything else", async () => {
		const project = await L.makeProject();
		await L.overlaySolution(project);
		expect(existsSync(join(choreo(project), "ToPickup.traj"))).toBe(true);
		expect(existsSync(join(choreo(project), "ToScore.traj"))).toBe(true);
		const tree = readFileSync(join(project, "src/main/deploy/autos/trees/PickupAndScore.xml"), "utf8");
		expect(tree).toContain('<FollowPath trajectory="ToPickup"/>');
		expect(tree).toContain('<FollowPath trajectory="ToScore"/>');
	});
});

describe("robot-autonomous behavior trees", () => {
	const autos = join(L.dir, "project/src/main/deploy/autos");
	const java = (file: string) => readFileSync(join(L.dir, "project/src/main/java/frc/robot", file), "utf8");

	test("nodes.xml lists exactly the robot nodes AutoNodes registers", () => {
		const ids = [...readFileSync(join(autos, "nodes.xml"), "utf8").matchAll(/<(?:Action|Condition) ID="([^"]+)"/g)].map((m) => m[1]);
		expect(ids.sort()).toEqual(["FollowPath", "IsSystemState", "SetWantedState"]);
		const nodes = java("autos/AutoNodes.java");
		expect(nodes).toContain('"FollowPath"');
		expect(nodes).toContain("RobotStateNodes.register(");
	});

	test("nodes.xml names the robot states a tree may ask for, and RobotStates has them", () => {
		const states = [...java("RobotStates.java").matchAll(/^\s{2}([A-Z_]+),?$/gm)].map((m) => m[1]);
		const nodes = readFileSync(join(autos, "nodes.xml"), "utf8");
		const askable = nodes.match(/Robot state to request: ([A-Z_, ]+?) or ([A-Z_]+)</);
		expect(askable).not.toBeNull();
		for (const state of [...askable![1].split(", "), askable![2]]) expect(states).toContain(state);
		// the step state is picked by the robot, never asked for
		expect(askable![0]).not.toContain("AIMING");
		expect(states).toContain("AIMING");
	});

	test("the Arbor project points at the trees and the simulator's live view", () => {
		const project = readJson(join(autos, "bt_project.json"));
		expect(project.nodes).toBe("nodes.xml");
		expect(project.trees).toBe("trees");
		expect(project.live.url).toBe("ws://localhost:1670");
	});

	test("the starter tree is PickupAndScore and only drives the first path", () => {
		const tree = readFileSync(join(autos, "trees/PickupAndScore.xml"), "utf8");
		expect(tree).toContain('main_tree_to_execute="PickupAndScore"');
		expect(tree).toContain('<BehaviorTree ID="PickupAndScore">');
		expect(tree).toContain('<FollowPath trajectory="ToPickup"/>');
		expect(tree).not.toContain("ToScore");
	});

	test("the starter has no command-based autos left", () => {
		const dir = join(L.dir, "project/src/main/java/frc/robot/autos");
		expect(readdirSync(dir)).toEqual(["AutoNodes.java"]);
	});
});

// The script checkpoints read the student's files (jq over the .traj files, grep over the tree XML
// and RobotContainer.java).
describe.skipIf(!hasJdk)("robot-autonomous script checkpoints", () => {
	const SCRIPTS = ["paths-drawn", "path-names", "paths-goal", "auto-tree", "aim-step"];
	const TREE = "src/main/deploy/autos/trees/PickupAndScore.xml";
	const CONTAINER = "src/main/java/frc/robot/RobotContainer.java";
	const traj = (project: string, name: string) => join(choreo(project), `${name}.traj`);

	/** A solved project that `break` then spoils; verify() says whether `checkpoint` notices. */
	const spoiled = async (checkpoint: string, breakIt: (project: string) => void) => {
		const project = await L.makeProject();
		try {
			await L.overlaySolution(project);
			expect(L.verify(project, checkpoint).exitCode, `${checkpoint} passes before spoiling`).toBe(0);
			breakIt(project);
			return L.verify(project, checkpoint);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	};
	const editJson = (file: string, edit: (json: any) => void) => {
		const json = readJson(file);
		edit(json);
		writeFileSync(file, JSON.stringify(json));
	};
	const editText = (file: string, edit: (text: string) => string) =>
		writeFileSync(file, edit(readFileSync(file, "utf8")));
	/** PickupAndScore.xml with only these steps inside the Sequence. */
	const treeWith = (steps: string[]) => (p: string) =>
		editText(join(p, TREE), (t) => t.replace(/<Sequence>[\s\S]*<\/Sequence>/, `<Sequence>\n${steps.join("\n")}\n</Sequence>`));
	const PICKUP = '<FollowPath trajectory="ToPickup"/>';
	const SCORE = '<FollowPath trajectory="ToScore"/>';
	const WAIT = '<Sleep msec="500"/>';
	const AIM = '<Timeout msec="3000"><SetWantedState state="AIM"/></Timeout>';
	const SHOOT = '<SetWantedState state="SHOOT"/>';
	const KEEP = '<Sleep msec="2000"/>';
	const STOP = '<SetWantedState state="AUTO"/>';

	test("every script checkpoint fails on the starter and passes with the solution", async () => {
		await L.roundTrip(SCRIPTS);
	}, 60_000);

	test("paths-drawn needs two generated, differential paths with three waypoints each", async () => {
		const gone = await spoiled("paths-drawn", (p) => rmSyncQuiet(traj(p, "ToScore")));
		expect(gone.exitCode).not.toBe(0);
		const ungenerated = await spoiled("paths-drawn", (p) =>
			editJson(traj(p, "ToScore"), (j) => (j.trajectory.samples = [])),
		);
		expect(ungenerated.exitCode).not.toBe(0);
		expect(ungenerated.text).toContain("ToScore");
		const short = await spoiled("paths-drawn", (p) =>
			editJson(traj(p, "ToPickup"), (j) => j.snapshot.waypoints.pop()),
		);
		expect(short.exitCode).not.toBe(0);
		const swerve = await spoiled("paths-drawn", (p) =>
			editJson(traj(p, "ToPickup"), (j) => (j.trajectory.sampleType = "Swerve")),
		);
		expect(swerve.exitCode).not.toBe(0);
	}, 60_000);

	test("path-names needs ToPickup and ToScore, spelled the way the tree drives them", async () => {
		const renamed = await spoiled("path-names", (p) => renameSync(traj(p, "ToScore"), traj(p, "Score")));
		expect(renamed.exitCode).not.toBe(0);
		const insideName = await spoiled("path-names", (p) =>
			editJson(traj(p, "ToScore"), (j) => (j.name = "Score")),
		);
		expect(insideName.exitCode).not.toBe(0);
		const misspelled = await spoiled("path-names", (p) =>
			editText(join(p, TREE), (t) => t.replace('trajectory="ToScore"', 'trajectory="toScore"')),
		);
		expect(misspelled.exitCode).not.toBe(0);
		const unknown = await spoiled("path-names", treeWith([PICKUP, SCORE, '<FollowPath trajectory="Nope"/>']));
		expect(unknown.exitCode).not.toBe(0);
		expect(unknown.text).toContain("Nope");
		// a commented-out path does not count
		const commented = await spoiled("path-names", (p) =>
			editText(join(p, TREE), (t) => t.replace(SCORE, `<!-- ${SCORE} -->`)),
		);
		expect(commented.exitCode).not.toBe(0);
	}, 60_000);

	test("paths-goal needs the paths to start, join and end at the field targets", async () => {
		const moveEnd = (name: string, which: "first" | "last", dx: number) => (p: string) =>
			editJson(traj(p, name), (j) => {
				const s = j.trajectory.samples;
				s[which === "first" ? 0 : s.length - 1].x += dx;
			});
		// ToPickup that stops a meter short of the pickup
		const shortOfPickup = await spoiled("paths-goal", moveEnd("ToPickup", "last", -1.0));
		expect(shortOfPickup.exitCode).not.toBe(0);
		expect(shortOfPickup.text).toContain("Pickup");
		// ToPickup that starts somewhere other than Start
		expect((await spoiled("paths-goal", moveEnd("ToPickup", "first", 1.0))).exitCode).not.toBe(0);
		// ToScore that does not start where ToPickup ended
		const gap = await spoiled("paths-goal", moveEnd("ToScore", "first", 0.5));
		expect(gap.exitCode).not.toBe(0);
		expect(gap.text).toContain("ToScore should start where ToPickup ended");
		// ToScore that ends short of the scoring spot
		const shortOfScore = await spoiled("paths-goal", moveEnd("ToScore", "last", -1.0));
		expect(shortOfScore.exitCode).not.toBe(0);
		expect(shortOfScore.text).toContain("ScoreSpot");
		// a little slack is fine: a path that ends 0.1 m off still passes
		expect((await spoiled("paths-goal", moveEnd("ToScore", "last", 0.1))).exitCode).toBe(0);
	}, 60_000);

	test("auto-tree needs both paths, then aim in a Timeout, then shoot, wait and stop, in that order", async () => {
		const cases: Record<string, string[]> = {
			"aim after the shot": [PICKUP, SCORE, SHOOT, KEEP, AIM, STOP],
			"no aim step": [PICKUP, SCORE, SHOOT, KEEP, STOP],
			"aim without a Timeout": [PICKUP, SCORE, '<SetWantedState state="AIM"/>', SHOOT, KEEP, STOP],
			"no shot": [PICKUP, SCORE, AIM, KEEP, STOP],
			"no Sleep after the shot": [PICKUP, SCORE, AIM, SHOOT, STOP],
			"never back to AUTO": [PICKUP, SCORE, AIM, SHOOT, KEEP],
			"AUTO before the Sleep": [PICKUP, SCORE, AIM, SHOOT, STOP, KEEP],
			"scoring path first": [SCORE, PICKUP, AIM, SHOOT, KEEP, STOP],
			"only the first path": [PICKUP, AIM, SHOOT, KEEP, STOP],
		};
		for (const [what, steps] of Object.entries(cases)) {
			const result = await spoiled("auto-tree", treeWith(steps));
			expect(result.exitCode, what).not.toBe(0);
		}
		// what Arbor writes is fine too: other attribute orders, names, line breaks, no pickup wait
		const arbor = await spoiled(
			"auto-tree",
			treeWith([
				'<FollowPath name="first" trajectory="ToPickup" />',
				'<FollowPath\n  trajectory="ToScore"\n  name="second"/>',
				'<Timeout name="limit" msec="2500">\n  <SetWantedState name="aim" state="AIM"></SetWantedState>\n</Timeout>',
				'<SetWantedState state="SHOOT" name="shoot"/>',
				'<Sleep name="keep" msec="1500"/>',
				STOP,
			]),
		);
		expect(arbor.exitCode, arbor.text).toBe(0);
		// a commented-out aim does not count
		const commented = await spoiled("auto-tree", treeWith([PICKUP, SCORE, `<!-- ${AIM} -->`, SHOOT, KEEP, STOP]));
		expect(commented.exitCode).not.toBe(0);
		// the tree must be saved as PickupAndScore
		const renamed = await spoiled("auto-tree", (p) =>
			renameSync(join(p, TREE), join(p, "src/main/deploy/autos/trees/Mine.xml")),
		);
		expect(renamed.exitCode).not.toBe(0);
	}, 120_000);

	test("aim-step needs AIMING until isAimed, then AIM, in handleStateTransition", async () => {
		const aimCase = (body: string) => (p: string) =>
			editText(join(p, CONTAINER), (t) => t.replace(/case AIM:\n[\s\S]*?break;/, `case AIM:\n${body}\n        break;`));
		const cases: Record<string, string> = {
			"straight to AIM": "system_state_ = RobotStates.AIM;",
			"always AIMING": "system_state_ = RobotStates.AIMING;",
			"no isAimed": "if (true) { system_state_ = RobotStates.AIM; } else { system_state_ = RobotStates.AIMING; }",
			"commented out": "// if (drive_.isAimed()) { system_state_ = RobotStates.AIM; } else { system_state_ = RobotStates.AIMING; }\nsystem_state_ = RobotStates.AIM;",
		};
		for (const [what, body] of Object.entries(cases)) {
			const result = await spoiled("aim-step", aimCase(body));
			expect(result.exitCode, what).not.toBe(0);
		}
		// another fine way to write it
		const ternaryFree = await spoiled(
			"aim-step",
			aimCase("if (!drive_.isAimed()) {\n  system_state_ = RobotStates.AIMING;\n} else {\n  system_state_ = RobotStates.AIM;\n}"),
		);
		expect(ternaryFree.exitCode).toBe(0);
	}, 90_000);
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
