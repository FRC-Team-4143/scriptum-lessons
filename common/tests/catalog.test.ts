// The shape of the repo itself: catalog.json is current, every lesson is a complete
// folder, and nothing sits in a place Scriptum won't look.
import { describe, expect, test } from "bun:test";
import { existsSync, readdirSync, statSync } from "node:fs";
import { join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { readFileSync } from "node:fs";
import { buildCatalog } from "../scripts/build-catalog";

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
		const allowed = new Set([".bun-version", ".git", ".github", ".gitignore", "README.md", "catalog.json", "common", "lessons", "node_modules", "package.json", "bun.lock"]);
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
