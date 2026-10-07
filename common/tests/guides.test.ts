// Checks Dozer's guides (the ones in each lesson's guides/ folder and in common/guides/,
// as listed in catalog.json) the way Scriptum will read them, so a typo fails here
// instead of showing an empty tutor or a broken walkthrough. Scriptum's own schema (packages/contracts, "Tutor") is the
// real authority; this mirrors the parts that are easy to get wrong. See
// Scriptum's docs/lessons/tutor.md.
import { describe, expect, test } from "bun:test";
import { existsSync, readdirSync, readFileSync } from "node:fs";
import { resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = resolve(fileURLToPath(new URL("../..", import.meta.url)));
const readJson = (path: string) =>
	JSON.parse(readFileSync(resolve(repoRoot, path), "utf8"));

const PANES = ["editor", "scope", "choreo", "elastic", "preview", "driverStation"];
const LENGTH = /^-?\d+(?:\.\d+)?(?:px|%)$/;

type Target = {
	pane?: string;
	selector?: string;
	text?: string;
	region?: Record<string, unknown>;
};
/** Text written once, or once per level (Scriptum's tutorWorded). */
type Worded = string | { never: string; bit?: string; lot?: string };
/** Every wording of a text, so length limits apply to each. */
const wordings = (w: Worded | undefined): string[] =>
	w === undefined ? [] : typeof w === "string" ? [w] : Object.values(w);
type Step = {
	title: Worded;
	say: Worded;
	assist?: boolean;
	open?: string[];
	close?: string[];
	target?: Target;
	board?: {
		code: string;
		language?: string;
		marks?: { text: string; label: string; nth?: number }[];
	};
	cardAt?: string;
	waitFor?: {
		target?: Target;
		hint?: Worded;
		more?: boolean;
		atLeast?: number;
		gone?: boolean;
		skipIfDone?: boolean;
	};
};

type Guide = {
	id: string;
	title: string;
	topic: string;
	order: number;
	modules?: string[];
	needs?: string[];
	path: string;
};
const catalog = readJson("catalog.json") as {
	lessons: { id: string }[];
	guides: Guide[];
};
const index = { concepts: catalog.guides };
const moduleIds = new Set(catalog.lessons.map((l) => l.id));

function checkTarget(where: string, target: Target) {
	expect(target.pane !== undefined || target.selector !== undefined, `${where}: needs a pane or selector`).toBe(true);
	if (target.pane) expect(PANES, `${where}: pane`).toContain(target.pane);
	if (target.selector) {
		// A selector the browser can't parse would just never match.
		expect(() => checkBalanced(target.selector as string), `${where}: selector`).not.toThrow();
	}
	for (const [key, value] of Object.entries(target.region ?? {})) {
		expect(["left", "top", "right", "bottom", "width", "height"], `${where}: region.${key}`).toContain(key);
		expect(typeof value === "number" || (typeof value === "string" && LENGTH.test(value)), `${where}: region.${key}=${value}`).toBe(true);
	}
}

/** Cheap structural check for the selector mistakes that happen in JSON:
 * unbalanced brackets, parentheses or quotes. */
function checkBalanced(selector: string) {
	const pairs: Record<string, string> = { "[": "]", "(": ")" };
	const stack: string[] = [];
	let quote: string | null = null;
	for (const ch of selector) {
		if (quote) {
			if (ch === quote) quote = null;
		} else if (ch === "'" || ch === '"') {
			quote = ch;
		} else if (pairs[ch]) {
			stack.push(pairs[ch] as string);
		} else if (ch === "]" || ch === ")") {
			if (stack.pop() !== ch) throw new Error(`unbalanced ${ch}`);
		}
	}
	if (quote || stack.length) throw new Error("unbalanced selector");
}

describe("guides", () => {
	test("a shared guide only explains: walk-through steps name one lesson's own files", () => {
		for (const concept of index.concepts) {
			if (!concept.path.startsWith("common/guides/")) continue;
			const { steps } = readJson(concept.path) as { steps: Step[] };
			// Steps that wait for the student (open this file, find that line) are fine when the file and
			// line exist in every lesson using the guide (the next test checks that). The edit itself, a
			// "your turn" step with no wait, is what belongs to one lesson.
			// These point at code that is the same in every lesson using them (main starts empty in both
			// methods lessons; every drive mech has readInputs) and never ask for a change.
			if (["java-methods-edge-cases", "robot-drive-math-io-loop"].includes(concept.id)) continue;
			const walkthrough = steps.filter((s) => s.assist && !s.waitFor && (s.target?.text || s.target?.selector)).length;
			expect(walkthrough, `${concept.id} is shared by ${concept.modules?.join(", ")} but walks through edits`).toBe(0);
		}
	});

	test("a guide's kind matches its steps", () => {
		for (const concept of index.concepts as (Guide & { kind?: string })[]) {
			if (!concept.kind) continue;
			const { steps } = readJson(concept.path) as { steps: Step[] };
			const n = steps.filter((s) => s.assist).length;
			const want = n === 0 ? "concept" : n === steps.length ? "assist" : "mixed";
			expect(concept.kind, concept.id).toBe(want);
		}
	});

	test("have unique, kebab-case ids, a title, a topic and a whole-number order", () => {
		const ids = index.concepts.map((c) => c.id);
		expect(new Set(ids).size).toBe(ids.length);
		for (const concept of index.concepts) {
			expect(concept.id).toMatch(/^[a-z0-9]+(?:-[a-z0-9]+)*$/);
			expect(concept.title.length).toBeGreaterThan(0);
			expect(concept.topic.length).toBeGreaterThan(0);
			expect(Number.isInteger(concept.order)).toBe(true);
			expect(existsSync(resolve(repoRoot, concept.path)), concept.path).toBe(true);
			for (const pane of concept.needs ?? []) expect(PANES).toContain(pane);
		}
	});

	test("every guide belongs to a lesson, since that's the only way to reach it", () => {
		// The Dozer button lists only the loaded lesson's guides, so a guide no
		// lesson names can never be started.
		for (const concept of index.concepts) {
			expect(concept.modules?.length, `${concept.id} lists no lessons`).toBeGreaterThan(0);
			for (const id of concept.modules ?? []) expect(moduleIds, `${concept.id}: lesson ${id}`).toContain(id);
		}
	});

	test("a guide lives in its lesson's folder, or in common/guides when several lessons use it", () => {
		for (const concept of index.concepts) {
			const shared = concept.path.startsWith("common/guides/");
			if (shared) {
				expect((concept.modules ?? []).length, `${concept.id}: shared by only one lesson, so move it into that lesson`).toBeGreaterThan(1);
			} else {
				expect(concept.path, concept.id).toBe(`lessons/${concept.modules?.[0]}/guides/${concept.id}.json`);
				expect(concept.modules?.length, concept.id).toBe(1);
			}
		}
	});
});

describe.each(index.concepts.map((c) => c.id))("tutor/%s.json", (id) => {
	const { steps } = readJson(index.concepts.find((c) => c.id === id)?.path as string) as { steps: Step[] };

	test("has 1-40 steps, each with a title and words", () => {
		expect(steps.length).toBeGreaterThan(0);
		expect(steps.length).toBeLessThanOrEqual(40);
		steps.forEach((step, i) => {
			expect(wordings(step.title).length, `step ${i + 1} title`).toBeGreaterThan(0);
			for (const t of wordings(step.title)) {
				expect(t.length, `step ${i + 1} title`).toBeGreaterThan(0);
				expect(t.length, `step ${i + 1} title`).toBeLessThanOrEqual(80);
			}
			expect(wordings(step.say).length, `step ${i + 1} say`).toBeGreaterThan(0);
			for (const t of wordings(step.say)) {
				expect(t.length, `step ${i + 1} say`).toBeGreaterThan(0);
				expect(t.length, `step ${i + 1} say`).toBeLessThanOrEqual(1200);
			}
			// A worded text must start with the wording every level can fall back to.
			for (const w of [step.title, step.say]) {
				if (typeof w === "object") expect(typeof w.never, `step ${i + 1}: worded text needs "never"`).toBe("string");
			}
		});
	});

	test("targets, panes and boards are well formed", () => {
		steps.forEach((step, i) => {
			const where = `step ${i + 1}`;
			for (const pane of [...(step.open ?? []), ...(step.close ?? [])]) expect(PANES, where).toContain(pane);
			if (step.target) checkTarget(`${where} target`, step.target);
			if (step.cardAt) expect(["top-left", "top-right", "bottom-left", "bottom-right"], `${where} cardAt`).toContain(step.cardAt);
			if (step.waitFor) {
				const { target, more, atLeast, gone } = step.waitFor;
				// No target: an "I did it" step, which needs words telling them what to do.
				if (!target) {
					expect(step.waitFor.hint, `${where}: an "I did it" step needs a hint`).toBeDefined();
					expect(more || gone || atLeast !== undefined, `${where}: more/atLeast/gone need a target`).toBeFalsy();
				} else {
					expect(target.selector, `${where} waitFor needs a selector`).toBeDefined();
					checkTarget(`${where} waitFor`, target);
				}
				expect([more, gone, atLeast !== undefined].filter(Boolean).length, `${where}: one of more/atLeast/gone`).toBeLessThanOrEqual(1);
				if (step.waitFor.skipIfDone) expect(target, `${where}: skipIfDone needs a target`).toBeDefined();
			}
			if (step.board) {
				expect(["java", "shell", "text", undefined]).toContain(step.board.language);
				for (const mark of step.board.marks ?? []) {
					// Every label should land somewhere in the code it explains.
					let at = -1;
					for (let n = 0; n < (mark.nth ?? 1); n++) at = step.board.code.indexOf(mark.text, at + 1);
					expect(at, `${where}: mark "${mark.text}" isn't in the code`).toBeGreaterThanOrEqual(0);
				}
			}
		});
	});
});

// A step that points at a line, or waits for a file's tab, must point at something that is
// in the lesson's starter project. A guide reused in a lesson whose files differ (the
// subsystems lesson has no "TODO (STEP 1)" line) can never advance, and nothing else notices.
function filesUnder(dir: string): string[] {
	if (!existsSync(dir)) return [];
	return readdirSync(dir, { withFileTypes: true }).flatMap((e) =>
		e.isDirectory() ? filesUnder(resolve(dir, e.name)) : [resolve(dir, e.name)],
	);
}
const normalize = (s: string) => s.replace(/\s+/g, " ").trim();

describe("guides only point at things the lesson has", () => {
	for (const guide of index.concepts) {
		const { steps } = readJson(guide.path) as { steps: Step[] };
		for (const moduleId of guide.modules ?? []) {
			test(`${guide.id} in ${moduleId}`, () => {
				const files = filesUnder(resolve(repoRoot, "lessons", moduleId, "project"));
				const names = new Set(files.map((f) => f.split("/").pop()));
				const lines = files.flatMap((f) => {
					try {
						return readFileSync(f, "utf8").split("\n").map(normalize);
					} catch {
						return [];
					}
				});
				steps.forEach((step, i) => {
					for (const t of [step.target, step.waitFor?.target]) {
						if (!t?.text) continue;
						const where = `step ${i + 1} "${step.title}" (${t.text})`;
						if (t.pane === "editor" && t.selector?.includes(".tab")) {
							// (Only .java tabs: other files are ones the student makes in the editor lessons.)
							if (!t.text.endsWith(".java")) continue;
							expect(names.has(t.text), `${where}: no file called that in ${moduleId}`).toBe(true);
						} else if (t.selector?.includes(".view-line")) {
							const want = normalize(t.text);
							expect(
								lines.some((l) => l.includes(want)),
								`${where}: no line like that in ${moduleId}'s project`,
							).toBe(true);
						}
					}
				});
			});
		}
	}
});

// The robot lessons are where most students have never programmed, so every step there is
// worded for them: a plain-language "never" version alongside the shorter original.
describe("robot guides are worded for students who have never coded", () => {
	for (const guide of index.concepts.filter((g) => g.id.startsWith("robot-") || g.id === "build-and-run")) {
		test(guide.id, () => {
			const { steps } = readJson(guide.path) as { steps: Step[] };
			steps.forEach((step, i) => {
				expect(typeof step.say, `step ${i + 1} "${wordings(step.title)[0]}" has only one wording`).toBe("object");
			});
		});
	}
});
