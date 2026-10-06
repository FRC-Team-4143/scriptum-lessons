// Checks the tutor's concepts (tutor.json + tutor/<id>.json) the way Scriptum
// will read them, so a typo fails here instead of showing an empty tutor or a
// broken walkthrough. Scriptum's own schema (packages/contracts, "Tutor") is the
// real authority; this mirrors the parts that are easy to get wrong. See
// Scriptum's docs/lessons/tutor.md.
import { describe, expect, test } from "bun:test";
import { existsSync, readdirSync, readFileSync } from "node:fs";
import { resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = resolve(fileURLToPath(new URL("..", import.meta.url)));
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
type Step = {
	title: string;
	say: string;
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
		hint?: string;
		more?: boolean;
		atLeast?: number;
		gone?: boolean;
	};
};

const index = readJson("tutor.json") as {
	schemaVersion: number;
	concepts: {
		id: string;
		title: string;
		topic: string;
		order: number;
		modules?: string[];
		needs?: string[];
	}[];
};
const moduleIds = new Set(
	(readJson("modules.json") as { modules: { id: string }[] }).modules.map((m) => m.id),
);

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

describe("tutor.json", () => {
	test("lists unique, kebab-case concepts that each have a file", () => {
		expect(index.schemaVersion).toBe(1);
		const ids = index.concepts.map((c) => c.id);
		expect(new Set(ids).size).toBe(ids.length);
		for (const concept of index.concepts) {
			expect(concept.id).toMatch(/^[a-z0-9]+(?:-[a-z0-9]+)*$/);
			expect(concept.title.length).toBeGreaterThan(0);
			expect(concept.topic.length).toBeGreaterThan(0);
			expect(Number.isInteger(concept.order)).toBe(true);
			expect(existsSync(resolve(repoRoot, `tutor/${concept.id}.json`))).toBe(true);
			for (const id of concept.modules ?? []) expect(moduleIds, `${concept.id}: module ${id}`).toContain(id);
			for (const pane of concept.needs ?? []) expect(PANES).toContain(pane);
		}
	});

	test("every concept belongs to a lesson, since that's the only way to reach it", () => {
		// The Dozer button lists only the loaded lesson's guides, so a concept no
		// lesson names can never be started.
		for (const concept of index.concepts) {
			expect(concept.modules?.length, `${concept.id} lists no lessons`).toBeGreaterThan(0);
		}
	});

	test("every file in tutor/ is listed", () => {
		const listed = new Set(index.concepts.map((c) => `${c.id}.json`));
		for (const file of readdirSync(resolve(repoRoot, "tutor"))) {
			expect(listed, `tutor/${file}`).toContain(file);
		}
	});
});

describe.each(index.concepts.map((c) => c.id))("tutor/%s.json", (id) => {
	const { steps } = readJson(`tutor/${id}.json`) as { steps: Step[] };

	test("has 1-40 steps, each with a title and words", () => {
		expect(steps.length).toBeGreaterThan(0);
		expect(steps.length).toBeLessThanOrEqual(40);
		steps.forEach((step, i) => {
			expect(step.title?.length, `step ${i + 1} title`).toBeGreaterThan(0);
			expect(step.title.length, `step ${i + 1} title`).toBeLessThanOrEqual(80);
			expect(step.say?.length, `step ${i + 1} say`).toBeGreaterThan(0);
			expect(step.say.length, `step ${i + 1} say`).toBeLessThanOrEqual(1200);
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
