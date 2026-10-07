// Checks the help Dozer gives stuck students (lessons/<id>/help.json and
// common/help/errors.json) the way Scriptum reads them (decision 059 in Scriptum), so a
// typo fails here instead of Dozer quietly having no hints.
import { describe, expect, test } from "bun:test";
import { existsSync, readdirSync, readFileSync, statSync } from "node:fs";
import { resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = resolve(fileURLToPath(new URL("../..", import.meta.url)));
const readJson = (path: string) => JSON.parse(readFileSync(resolve(repoRoot, path), "utf8"));

type Worded = string | { never: string; bit?: string; lot?: string };
const catalog = readJson("catalog.json") as { lessons: { id: string }[]; guides: { id: string; modules?: string[] }[] };
const moduleIds = new Set(catalog.lessons.map((l) => l.id));
const guideIds = new Set(catalog.guides.map((g) => g.id));
const guideModules = new Map(catalog.guides.map((g) => [g.id, g.modules ?? []]));

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
		plan?: { checkpoint: string; guides: string[]; intro?: Worded }[];
	};

	// The guided path ("Teach me, step by step"): one stop per checkpoint, in the lesson's order,
	// running guides this lesson actually offers.
	test("has a plan that follows the lesson's checkpoints and uses its own guides", () => {
		const meta = readJson(`lessons/${id}/lesson.json`) as { checkpoints?: { id: string }[] };
		const order = (meta.checkpoints ?? []).map((c) => c.id);
		const stops = (help.plan ?? []).map((p) => p.checkpoint);
		const seen = new Set<string>();
		for (const stop of help.plan ?? []) {
			expect(order, `plan: checkpoint ${stop.checkpoint}`).toContain(stop.checkpoint);
			expect(seen.has(stop.checkpoint), `plan: ${stop.checkpoint} listed twice`).toBe(false);
			seen.add(stop.checkpoint);
			expect(stop.guides.length, `plan ${stop.checkpoint}`).toBeGreaterThan(0);
			expect(stop.guides.length).toBeLessThanOrEqual(8);
			for (const g of stop.guides) {
				expect(guideIds, `plan ${stop.checkpoint}: guide ${g}`).toContain(g);
				expect(guideModules.get(g), `plan ${stop.checkpoint}: ${g} isn't offered in ${id}`).toContain(id);
			}
			if (stop.intro) checkWorded(`plan ${stop.checkpoint} intro`, stop.intro);
		}
		expect(stops, "plan stops follow the checkpoint order").toEqual(order.filter((c) => seen.has(c)));
	});

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

// --- "mistakes": slips Dozer looks for in the student's code (Scriptum: control/src/inspect.ts) ---

/** Comments become spaces, keeping offsets and line breaks (same as Scriptum's). */
function blankComments(src: string): string {
	let out = "";
	let i = 0;
	while (i < src.length) {
		const c = src[i] as string;
		const next = src[i + 1];
		if (c === "/" && next === "/") {
			while (i < src.length && src[i] !== "\n") {
				out += " ";
				i++;
			}
		} else if (c === "/" && next === "*") {
			out += "  ";
			i += 2;
			while (i < src.length && !(src[i] === "*" && src[i + 1] === "/")) {
				out += src[i] === "\n" ? "\n" : " ";
				i++;
			}
			if (i < src.length) {
				out += "  ";
				i += 2;
			}
		} else if (c === '"') {
			out += c;
			i++;
			while (i < src.length && src[i] !== '"' && src[i] !== "\n") {
				if (src[i] === "\\") {
					out += src[i] as string;
					i++;
				}
				if (i < src.length) out += src[i] as string;
				i++;
			}
			if (i < src.length) {
				out += src[i] as string;
				i++;
			}
		} else {
			out += c;
			i++;
		}
	}
	return out;
}
function methodBody(src: string, name: string): string | null {
	const m = new RegExp(`\\b${name}\\s*\\([^)]*\\)\\s*(?:throws[^{]*)?\\{`).exec(src);
	if (!m) return null;
	const start = m.index + m[0].length;
	let depth = 1;
	for (let i = start; i < src.length; i++) {
		if (src[i] === "{") depth++;
		else if (src[i] === "}" && --depth === 0) return src.slice(start, i);
	}
	return null;
}
function filesUnder(dir: string): string[] {
	if (!existsSync(dir)) return [];
	return readdirSync(dir).flatMap((n) => {
		const f = resolve(dir, n);
		return statSync(f).isDirectory() ? filesUnder(f) : [f];
	});
}

type Mistake = { id: string; file: string; inMethod?: string; find: string; when: "found" | "missing"; say: Worded };
const withMistakes = lessonFiles
	.map((id) => ({ id, help: readJson(`lessons/${id}/help.json`) as { checkpoints?: Record<string, { mistakes?: Mistake[] }> } }))
	.flatMap(({ id, help }) => Object.entries(help.checkpoints ?? {}).flatMap(([cp, h]) => (h.mistakes ?? []).map((m) => ({ lesson: id, cp, m }))));

describe("mistakes Dozer looks for in the student's code", () => {
	test("there are some", () => {
		expect(withMistakes.length).toBeGreaterThan(0);
	});

	test.each(withMistakes.map((x) => [`${x.lesson} / ${x.cp} / ${x.m.id}`, x] as const))("%s is well formed", (_name, { lesson, m }) => {
		expect(m.id).toMatch(/^[a-z0-9-]+$/);
		expect(["found", "missing"]).toContain(m.when);
		expect(() => new RegExp(m.find, "m"), "find compiles").not.toThrow();
		checkWorded(`${m.id} say`, m.say);
		// The file it searches exists in the lesson (its solution, or its starter).
		const files = [...filesUnder(resolve(repoRoot, "lessons", lesson, "solution")), ...filesUnder(resolve(repoRoot, "lessons", lesson, "project"))];
		expect(files.some((f) => f.endsWith(`/${m.file}`)), `${m.file} isn't in ${lesson}`).toBe(true);
	});

	// A correct solution must never be told it has made a mistake.
	test.each(withMistakes.map((x) => [`${x.lesson} / ${x.cp} / ${x.m.id}`, x] as const))("%s doesn't fire on the lesson's own solution", (_name, { lesson, m }) => {
		const solution = filesUnder(resolve(repoRoot, "lessons", lesson, "solution")).find((f) => f.endsWith(`/${m.file}`));
		if (!solution) return; // nothing to compare against
		let text = blankComments(readFileSync(solution, "utf8"));
		if (m.inMethod) {
			const body = methodBody(text, m.inMethod);
			expect(body, `${m.inMethod} isn't in the solution's ${m.file}`).not.toBeNull();
			text = body as string;
		}
		const hit = new RegExp(m.find, "m").test(text);
		expect(m.when === "found" ? hit : !hit, `${m.id} would tell a correct solution it has a mistake`).toBe(false);
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
