// The FRC Robot track as a whole: each robot lesson has its metadata, README, build files
// and checkpoints that resolve, and the lessons chain one after another. (A lesson's own
// round trips are in lessons/<id>/lesson.test.ts.)
import { describe, expect, test } from "bun:test";
import { existsSync, readFileSync } from "node:fs";
import { join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { cp, mkdtemp, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { hasJdk, run } from "./lesson-kit";

const repoRoot = resolve(fileURLToPath(new URL("../..", import.meta.url)));

type Lesson = {
	id: string;
	kind: string;
	subdir: string;
	requires?: string[];
	checkpoints?: { id: string; optional?: boolean; verifier: { type: string; path?: string; topic?: string } }[];
};
const lessons = (JSON.parse(readFileSync(join(repoRoot, "catalog.json"), "utf8")) as { lessons: Lesson[] }).lessons;
const byId = new Map(lessons.map((l) => [l.id, l]));

const ROBOT_LESSONS = [
	"robot-drivetrain",
	"robot-sensors",
	"robot-drive-math",
	"robot-odometry",
	"robot-subsystems",
	"robot-control-theory",
	"robot-state-machines",
	"robot-computer-vision",
	"robot-autonomous",
];

describe("FRC Robot track", () => {
	test("every robot lesson has a README, build files and checkpoints that resolve", () => {
		expect(byId.has("robot-starter")).toBe(false);
		for (const id of ROBOT_LESSONS) {
			const lesson = byId.get(id);
			expect(lesson, id).toBeDefined();
			if (!lesson) continue;
			expect(lesson.kind).toBe("robot");
			expect(existsSync(join(repoRoot, lesson.subdir, "README.md"))).toBe(true);
			expect(existsSync(join(repoRoot, lesson.subdir, "build.gradle"))).toBe(true);
			expect(existsSync(join(repoRoot, lesson.subdir, "src/main/deploy/robots/SimBot.json"))).toBe(true);
			for (const required of lesson.requires ?? []) expect(byId.has(required)).toBe(true);
			for (const checkpoint of lesson.checkpoints ?? []) {
				if (checkpoint.verifier.type === "script") {
					expect(existsSync(join(repoRoot, checkpoint.verifier.path as string))).toBe(true);
				} else {
					expect(checkpoint.verifier.topic).toStartWith("/AdvantageKit/RealOutputs/");
				}
			}
		}
	});

	test("the chain runs robot-drivetrain through robot-autonomous", () => {
		const requires = (id: string) => byId.get(id)?.requires ?? [];
		expect(requires("robot-sensors")).toContain("robot-drivetrain");
		expect(requires("robot-drive-math")).toContain("robot-sensors");
		expect(requires("robot-odometry")).toContain("robot-drive-math");
		expect(requires("robot-subsystems")).toContain("robot-odometry");
		expect(requires("robot-control-theory")).toContain("robot-subsystems");
		expect(requires("robot-state-machines")).toContain("robot-control-theory");
		expect(requires("robot-computer-vision")).toContain("robot-state-machines");
		expect(requires("robot-autonomous")).toContain("robot-computer-vision");
	});
});

// Every robot lesson ends with one optional, harder checkpoint. See README.md, "Robot modules".
describe("FRC Robot advanced challenges", () => {
	const scripts = (id: string) => (byId.get(id)?.checkpoints ?? []).filter((c) => c.verifier.type === "script");
	const lessonDir = (id: string) => join(repoRoot, "lessons", id);

	/** A throwaway copy of the starter, with challenge/ laid over it if asked. */
	async function project(id: string, finished: boolean): Promise<string> {
		const dir = await mkdtemp(join(tmpdir(), `challenge-${id}-`));
		await cp(join(lessonDir(id), "project"), dir, { recursive: true });
		if (finished) await cp(join(lessonDir(id), "challenge"), dir, { recursive: true });
		return dir;
	}
	const verify = (id: string, dir: string, path: string | undefined) =>
		run(dir, ["bash", join(repoRoot, path as string), dir]);

	test.each(ROBOT_LESSONS)("%s has one, last, optional and described in a hidden CHALLENGE.md", (id) => {
		const checkpoints = byId.get(id)?.checkpoints ?? [];
		const optional = checkpoints.filter((c) => c.optional);
		expect(optional.length).toBe(1);
		expect(optional[0]?.id).toStartWith("challenge-");
		expect(checkpoints.at(-1)?.id).toBe(optional[0]?.id);
		expect(existsSync(join(lessonDir(id), "challenge"))).toBe(true);
		// Scriptum keeps the challenge out of sight until the lesson is done, so its
		// instructions aren't in the README and the Explorer hides the file.
		expect(readFileSync(join(lessonDir(id), "project", "CHALLENGE.md"), "utf8")).toStartWith("# Advanced challenge\n");
		expect(readFileSync(join(lessonDir(id), "project", "README.md"), "utf8")).not.toContain("Advanced challenge");
		expect(readFileSync(join(lessonDir(id), "project", ".vscode", "settings.json"), "utf8")).toContain('"CHALLENGE.md": true');
	});

	test.each(ROBOT_LESSONS)("%s leaves it out of Dozer's plan", (id) => {
		const help = JSON.parse(readFileSync(join(lessonDir(id), "help.json"), "utf8")) as { plan?: { checkpoint: string }[] };
		const stops = (help.plan ?? []).map((stop) => stop.checkpoint);
		expect(stops.filter((stop) => stop.startsWith("challenge-"))).toEqual([]);
	});

	describe.skipIf(!hasJdk)("with a JDK", () => {
		test.each(ROBOT_LESSONS)("%s: a finished challenge still passes every script checkpoint", async (id) => {
			const dir = await project(id, true);
			try {
				for (const checkpoint of scripts(id)) {
					const result = verify(id, dir, checkpoint.verifier.path);
					expect({ checkpoint: checkpoint.id, text: result.text, code: result.exitCode }).toEqual({
						checkpoint: checkpoint.id,
						text: result.text,
						code: 0,
					});
				}
			} finally {
				await rm(dir, { recursive: true, force: true });
			}
		}, 60_000);

		test.each(ROBOT_LESSONS)("%s: a script challenge fails on the starter", async (id) => {
			const dir = await project(id, false);
			try {
				for (const checkpoint of scripts(id).filter((c) => c.optional)) {
					expect(verify(id, dir, checkpoint.verifier.path).exitCode).not.toBe(0);
				}
			} finally {
				await rm(dir, { recursive: true, force: true });
			}
		}, 60_000);
	});

	// Compiling needs Gradle and its dependency cache, so it only runs when asked:
	// LESSON_GRADLE=1 bun test common/tests/robot-track.test.ts
	describe.skipIf(!hasJdk || !process.env.LESSON_GRADLE)("challenge/ compiles", () => {
		test.each(ROBOT_LESSONS)("%s", async (id) => {
			const dir = await project(id, true);
			try {
				const result = run(dir, ["./gradlew", "compileJava", "--offline", "-Pskip-inspector-wrapper"]);
				expect(result.text).toContain("BUILD SUCCESSFUL");
			} finally {
				await rm(dir, { recursive: true, force: true });
			}
		}, 300_000);
	});
});
