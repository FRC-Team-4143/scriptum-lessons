// Checks the help Dozer gives stuck students (lessons/<id>/help.json and
// common/help/errors.json) the way Scriptum reads them (decision 059 in Scriptum), so a
// typo fails here instead of Dozer quietly having no hints.
import { describe, expect, test } from "bun:test";
import { existsSync, readdirSync, readFileSync } from "node:fs";
import { resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = resolve(fileURLToPath(new URL("../..", import.meta.url)));
const readJson = (path: string) => JSON.parse(readFileSync(resolve(repoRoot, path), "utf8"));

type Worded = string | { never: string; bit?: string; lot?: string };
const catalog = readJson("catalog.json") as { lessons: { id: string }[]; guides: { id: string }[] };
const moduleIds = new Set(catalog.lessons.map((l) => l.id));
const guideIds = new Set(catalog.guides.map((g) => g.id));

function checkWorded(where: string, text: unknown) {
	if (typeof text === "string") {
		expect(text.length, where).toBeGreaterThan(0);
		return;
	}
	const w = text as Record<string, unknown>;
	expect(typeof w.never, `${where}: needs a "never" wording`).toBe("string");
	for (const key of Object.keys(w)) expect(["never", "bit", "lot"], `${where}: ${key}`).toContain(key);
}

// Lessons that have a help.json of their own.
const lessonFiles = catalog.lessons
	.map((l) => l.id)
	.filter((id) => existsSync(resolve(repoRoot, "lessons", id, "help.json")));

describe.each(lessonFiles)("lessons/%s/help.json", (id) => {
	const help = readJson(`lessons/${id}/help.json`) as {
		about?: Worded;
		checkpoints?: Record<string, { why?: Worded; guide?: string; concept?: { guide: string; label: string }; hints?: Worded[] }>;
	};

	test("is for a real lesson, and only lists its checkpoints", () => {
		expect(moduleIds, id).toContain(id);
		const meta = readJson(`lessons/${id}/lesson.json`) as { checkpoints?: { id: string }[] };
		const ids = new Set((meta.checkpoints ?? []).map((c) => c.id));
		for (const checkpoint of Object.keys(help.checkpoints ?? {})) {
			expect(ids, `${id}: checkpoint ${checkpoint}`).toContain(checkpoint);
		}
	});

	test("words every hint for each level, and links guides that exist", () => {
		if (help.about) checkWorded("about", help.about);
		for (const [checkpoint, h] of Object.entries(help.checkpoints ?? {})) {
			if (h.why) checkWorded(`${checkpoint} why`, h.why);
			expect((h.hints ?? []).length, `${checkpoint}: 1-5 hints`).toBeGreaterThan(0);
			expect((h.hints ?? []).length).toBeLessThanOrEqual(5);
			(h.hints ?? []).forEach((hint, i) => checkWorded(`${checkpoint} hint ${i + 1}`, hint));
			if (h.guide) expect(guideIds, `${checkpoint}: guide`).toContain(h.guide);
			if (h.concept) expect(guideIds, `${checkpoint}: concept guide`).toContain(h.concept.guide);
		}
	});
});

describe("common/help/errors.json", () => {
	const { errors } = readJson("common/help/errors.json") as {
		errors: { id: string; match: string; title: string; explain: Worded; hints?: Worded[]; guide?: string }[];
	};

	test("has unique ids and patterns that compile", () => {
		expect(new Set(errors.map((e) => e.id)).size).toBe(errors.length);
		for (const e of errors) {
			expect(e.id).toMatch(/^[a-z0-9-]+$/);
			expect(() => new RegExp(e.match), e.id).not.toThrow();
			checkWorded(`${e.id} explain`, e.explain);
			(e.hints ?? []).forEach((hint, i) => checkWorded(`${e.id} hint ${i + 1}`, hint));
			if (e.guide) expect(guideIds, `${e.id}: guide`).toContain(e.guide);
		}
	});

	// Real lines from javac / Gradle / a crashing robot, each matched by exactly one error.
	const samples: Record<string, string> = {
		semicolon: "/workspace/project/src/main/java/frc/robot/Robot.java:42: error: ';' expected",
		paren: "Main.java:7: error: ')' expected",
		"end-of-file": "Main.java:12: error: reached end of file while parsing",
		"cannot-find-symbol": "Robot.java:30: error: cannot find symbol",
		"incompatible-types": "Main.java:5: error: incompatible types: String cannot be converted to int",
		"missing-return": "Main.java:9: error: missing return statement",
		"not-initialized": "Main.java:6: error: variable total might not have been initialized",
		"unclosed-string": "Main.java:4: error: unclosed string literal",
		"public-class-file": "Robots.java:1: error: class Robot is public, should be declared in a file named Robot.java",
		"illegal-start": "Main.java:8: error: illegal start of expression",
		"already-defined": "Main.java:6: error: variable speed is already defined in method main(String[])",
		"lossy-conversion": "Main.java:5: error: incompatible types: possible lossy conversion from double to int",
		"null-pointer": "Unhandled exception: java.lang.NullPointerException: Cannot invoke \"Motor.set(double)\" because \"left\" is null",
		"index-out-of-bounds": "java.lang.ArrayIndexOutOfBoundsException: Index 3 out of bounds for length 3",
		"divide-by-zero": "java.lang.ArithmeticException: / by zero",
	};

	test.each(Object.entries(samples))("%s matches its sample line", (id, line) => {
		const hits = errors.filter((e) => new RegExp(e.match).test(line)).map((e) => e.id);
		expect(hits).toContain(id);
	});
});
