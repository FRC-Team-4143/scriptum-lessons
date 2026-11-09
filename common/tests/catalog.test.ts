// The shape of the repo itself: catalog.json is current, every lesson is a complete
// folder, and nothing sits in a place Scriptum won't look.
import { describe, expect, test } from "bun:test";
import { existsSync, readdirSync, statSync } from "node:fs";
import { join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { readFileSync } from "node:fs";
import { buildCatalog, checkRoadmap } from "../scripts/build-catalog";

const repoRoot = resolve(fileURLToPath(new URL("../..", import.meta.url)));

describe("catalog.json", () => {
	test("can be built from the lesson folders without problems", () => {
		expect(buildCatalog(repoRoot).problems).toEqual([]);
	});

	test("is up to date (run `bun run index` after changing a lesson.json or adding a guide)", () => {
		const { text } = buildCatalog(repoRoot);
		expect(readFileSync(join(repoRoot, "catalog.json"), "utf8") === text).toBe(true);
	});
});

describe("repo layout", () => {
	test("has only lessons/, common/ and the catalog at the top", () => {
		const allowed = new Set([".bun-version", ".claude", ".git", ".github", ".gitignore", "CLAUDE.md", "README.md", "catalog.json", "common", "lessons", "node_modules", "package.json", "bun.lock"]);
		const strays = readdirSync(repoRoot).filter((name) => !allowed.has(name));
		expect(strays, "move these into lessons/<id>/ or common/").toEqual([]);
	});

	const lessonIds = readdirSync(join(repoRoot, "lessons")).filter((n) => statSync(join(repoRoot, "lessons", n)).isDirectory());

	test.each(lessonIds)("lessons/%s is a complete lesson folder", (id) => {
		const dir = join(repoRoot, "lessons", id);
		expect(existsSync(join(dir, "lesson.json"))).toBe(true);
		expect(statSync(join(dir, "project")).isDirectory()).toBe(true);
		const known = new Set(["lesson.json", "lesson.test.ts", "project", "checkpoints", "guides", "help.json", "solution"]);
		const unknown = readdirSync(dir).filter((name) => !known.has(name));
		expect(unknown, "files Scriptum would never read: a misspelled folder?").toEqual([]);
	});
});

describe("roadmap fields", () => {
	type Lesson = { id: string; order: number; file: Record<string, unknown> };
	const lesson = (id: string, order: number, file: Record<string, unknown>): Lesson => ({ id, order, file: { order, ...file } });
	const problems = (lessons: Lesson[]) => {
		const out: string[] = [];
		checkRoadmap(lessons, (message) => out.push(message));
		return out;
	};

	test("every lesson needs a roadmapUnit, or null for an extra", () => {
		expect(problems([lesson("a", 1, { roadmapUnit: "Tools" }), lesson("b", 2, { roadmapUnit: null })])).toEqual([]);
		expect(problems([lesson("a", 1, {})])).toEqual([expect.stringContaining('missing "roadmapUnit"')]);
		expect(problems([lesson("a", 1, { roadmapUnit: "" })])).toEqual([expect.stringContaining("must be a unit title")]);
	});

	test("a unit has at most one milestone, and an extra can't be one", () => {
		expect(
			problems([lesson("a", 1, { roadmapUnit: "Drive", milestone: true }), lesson("b", 2, { roadmapUnit: "Drive", milestone: true })]),
		).toEqual([expect.stringContaining('"Drive" already has a milestone, a')]);
		expect(problems([lesson("a", 1, { roadmapUnit: null, milestone: true })])).toEqual([expect.stringContaining("a milestone needs")]);
	});

	test("a roadmap lesson can't require an extra or a lesson in a later unit", () => {
		// Autonomous's tools sort early, but the unit sits at its milestone, so
		// a requirement on them from an earlier unit is caught.
		const lessons = [
			lesson("tool", 5, { roadmapUnit: "Auto" }),
			lesson("drive", 20, { roadmapUnit: "Drive", milestone: true, requires: ["tool", "extra"] }),
			lesson("auto", 28, { roadmapUnit: "Auto", milestone: true, requires: ["drive", "tool"] }),
			lesson("extra", 40, { roadmapUnit: null }),
		];
		expect(problems(lessons)).toEqual([
			'lessons/drive: requires "tool", which is in a later roadmap unit',
			'lessons/drive: requires "extra", which is off the roadmap ("roadmapUnit": null)',
		]);
	});
});
