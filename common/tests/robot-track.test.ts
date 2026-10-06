// The FRC Robot track as a whole: each robot lesson has its metadata, README, build files
// and checkpoints that resolve, and the lessons chain one after another. (A lesson's own
// round trips are in lessons/<id>/lesson.test.ts.)
import { describe, expect, test } from "bun:test";
import { existsSync, readFileSync } from "node:fs";
import { join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = resolve(fileURLToPath(new URL("../..", import.meta.url)));

type Lesson = {
	id: string;
	kind: string;
	subdir: string;
	requires?: string[];
	checkpoints?: { verifier: { type: string; path?: string; topic?: string } }[];
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
		expect(requires("robot-autonomous")).toContain("robot-state-machines");
	});
});
