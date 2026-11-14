// Builds catalog.json, the index Scriptum reads, from the lesson folders.
//
//   bun run index                    write catalog.json and common/lesson-versions.json
//   bun run index --check            fail if either is out of date (the tests do this)
//   bun run index --compatible <id>  accept changed checkers for <id> without a new version
//
// Lesson versions: lesson.json's "version" (a whole number, 1 when missing) says which
// starter code a lesson's checkers expect. Scriptum records the version a student
// loaded; while the lesson keeps that version their Verify uses the newest checkers,
// and once it moves on they keep the checkers they loaded with and are offered a
// restart. So raise "version" when old projects would no longer pass the new
// checkers (a renamed method, a changed scaffold, a new required step), and leave it
// alone for typo fixes and checker bug fixes. common/lesson-versions.json remembers a
// hash of each lesson's checkers; changing them without raising "version" stops the
// build until you either raise it or say the change is compatible (--compatible <id>).
//
// A lesson is one folder, lessons/<id>/, holding everything about it:
//   lesson.json      title, kind, track, order, roadmapUnit, milestone, requires, recommends,
//                    checkpoints, shared guides it uses
//   project/         the starter files students get
//   checkpoints/     verify scripts, setup.sh
//   guides/<id>.json this lesson's Dozer guides (title, summary, topic, order, steps)
//   help.json        hints for stuck students
// Guides several lessons use live in common/guides/ and are listed in each lesson's
// lesson.json ("guides"). The catalog.json this writes is what lets Scriptum find
// them all, since GitHub's raw files can't be listed.
//
// The same script is kept in Scriptum's scripts/ for its bundled demo catalog;
// change both together.
import { createHash } from "node:crypto";
import { existsSync, readdirSync, readFileSync, statSync, writeFileSync } from "node:fs";
import { join, relative, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const ID = /^[a-z0-9]+(?:-[a-z0-9]+)*$/;

type Json = Record<string, unknown>;

export type Built = { text: string; problems: string[]; versionsText: string };

export type BuildOptions = {
	/** Lessons whose checker change is declared compatible (--compatible). */
	compatible?: string[];
};

/** Where each lesson's version and checker hash are remembered. */
export const VERSIONS_FILE = "common/lesson-versions.json";

type VersionLock = Record<string, { version: number; checkers: string }>;

const filesUnder = (path: string): string[] =>
	existsSync(path)
		? readdirSync(path)
				.sort()
				.flatMap((name) => {
					const full = join(path, name);
					return statSync(full).isDirectory() ? filesUnder(full) : [full];
				})
		: [];

/** A hash of everything that decides whether a project passes: the checkpoint list
 * from lesson.json and every file under checkpoints/. */
export function checkersHash(lessonDir: string, checkpoints: unknown): string {
	const hash = createHash("sha256");
	hash.update(JSON.stringify(checkpoints ?? []));
	const root = join(lessonDir, "checkpoints");
	for (const file of filesUnder(root)) {
		hash.update(`\0${relative(root, file)}\0`);
		hash.update(readFileSync(file));
	}
	return hash.digest("hex").slice(0, 16);
}

const readJson = (path: string): Json => JSON.parse(readFileSync(path, "utf8"));

const dirs = (path: string): string[] =>
	existsSync(path)
		? readdirSync(path)
				.filter((name) => statSync(join(path, name)).isDirectory())
				.sort()
		: [];

const jsonFiles = (path: string): string[] =>
	existsSync(path)
		? readdirSync(path)
				.filter((name) => name.endsWith(".json"))
				.sort()
		: [];

/** Keeps only the keys that are set, in the order given, so output is stable. */
function pick(source: Json, keys: string[]): Json {
	const out: Json = {};
	for (const key of keys) if (source[key] !== undefined) out[key] = source[key];
	return out;
}

/**
 * The roadmap is built only from `roadmapUnit`, `milestone` and `order` (track
 * is grouping only), so every lesson says where it goes:
 *   "roadmapUnit": "Motors and Drivetrains"   a stop in that unit (the unit's title)
 *   "roadmapUnit": null                       off the roadmap: an optional extra
 * A unit has at most one `"milestone": true` lesson, its big node. Units go in
 * the order of their milestone's `order`, or their lowest `order` without one.
 * A roadmap lesson can't `require` an extra or a lesson in a later unit, or the
 * roadmap would send students to a locked stop.
 */
export function checkRoadmap(lessons: { id: string; order: number; file: Json }[], problem: (message: string) => void) {
	const units = new Map<string, { at: number; milestone?: string }>();
	for (const { id, order, file } of lessons) {
		const dir = `lessons/${id}/lesson.json`;
		const unit = file.roadmapUnit;
		if (unit === undefined) {
			problem(`${dir}: missing "roadmapUnit" (a unit title like "Coding as a Team", or null for an extra)`);
			continue;
		}
		if (unit !== null && (typeof unit !== "string" || unit.trim() === "")) {
			problem(`${dir}: "roadmapUnit" must be a unit title, like "Coding as a Team", or null for an extra`);
			continue;
		}
		if (file.milestone !== undefined && typeof file.milestone !== "boolean") {
			problem(`${dir}: "milestone" must be true or false`);
		}
		if (unit === null) {
			if (file.milestone === true) problem(`${dir}: a milestone needs a "roadmapUnit"`);
			continue;
		}
		const entry = units.get(unit) ?? { at: order };
		if (file.milestone === true) {
			if (entry.milestone) problem(`${dir}: "${unit}" already has a milestone, ${entry.milestone}`);
			entry.milestone = id;
		}
		units.set(unit, entry);
	}
	for (const { order, file } of lessons) {
		const entry = typeof file.roadmapUnit === "string" ? units.get(file.roadmapUnit) : undefined;
		if (entry && !entry.milestone) entry.at = Math.min(entry.at, order);
	}
	for (const [unit, entry] of units) {
		if (entry.milestone) entry.at = Number(lessons.find((l) => l.id === entry.milestone)?.order);
		units.set(unit, entry);
	}
	const unitAt = (id: string) => {
		const unit = lessons.find((l) => l.id === id)?.file.roadmapUnit;
		return typeof unit === "string" ? units.get(unit)?.at : undefined;
	};
	for (const { id, file } of lessons) {
		const at = unitAt(id);
		if (at === undefined) continue;
		for (const required of (file.requires as string[] | undefined) ?? []) {
			if (!lessons.some((l) => l.id === required)) continue;
			const requiredAt = unitAt(required);
			if (requiredAt === undefined) {
				problem(`lessons/${id}: requires "${required}", which is off the roadmap ("roadmapUnit": null)`);
			} else if (requiredAt > at) {
				problem(`lessons/${id}: requires "${required}", which is in a later roadmap unit`);
			}
		}
	}
}

export function buildCatalog(root: string, options: BuildOptions = {}): Built {
	const problems: string[] = [];
	const problem = (message: string) => problems.push(message);

	type Lesson = { id: string; order: number; file: Json };
	const lessons: Lesson[] = [];
	for (const id of dirs(join(root, "lessons"))) {
		const path = join(root, "lessons", id, "lesson.json");
		if (!ID.test(id)) problem(`lessons/${id}: folder names are lowercase kebab-case`);
		if (!existsSync(path)) {
			problem(`lessons/${id}: no lesson.json`);
			continue;
		}
		const file = readJson(path);
		for (const key of ["title", "kind", "order"]) {
			if (file[key] === undefined) problem(`lessons/${id}/lesson.json: missing "${key}"`);
		}
		if (!Number.isInteger(file.order)) problem(`lessons/${id}/lesson.json: "order" must be a whole number`);
		if (!existsSync(join(root, "lessons", id, "project"))) problem(`lessons/${id}: no project/ folder`);
		if (file.version !== undefined && !(Number.isInteger(file.version) && Number(file.version) >= 1)) {
			problem(`lessons/${id}/lesson.json: "version" must be a whole number, 1 or more`);
		}
		lessons.push({ id, order: Number(file.order), file });
	}
	const versionOf = (file: Json) => (Number.isInteger(file.version) ? Number(file.version) : 1);

	// --- versions: changed checkers need a new version, or an explicit "compatible" ---
	const lockPath = join(root, VERSIONS_FILE);
	const lock: VersionLock = existsSync(lockPath) ? (readJson(lockPath) as VersionLock) : {};
	const compatible = new Set(options.compatible ?? []);
	for (const id of compatible) {
		if (!lessons.some((l) => l.id === id)) problem(`--compatible ${id}: no such lesson`);
	}
	const nextLock: VersionLock = {};
	for (const { id, file } of [...lessons].sort((a, b) => a.id.localeCompare(b.id))) {
		const version = versionOf(file);
		const checkers = checkersHash(join(root, "lessons", id), file.checkpoints);
		const was = lock[id];
		if (was && version < was.version) {
			problem(`lessons/${id}/lesson.json: "version" went down from ${was.version} to ${version}; versions only go up`);
		} else if (was && version === was.version && checkers !== was.checkers && !compatible.has(id)) {
			problem(
				`lessons/${id}: its checkers changed but "version" is still ${version}. If a project started from the old starter code would now fail Verify, raise "version" in lesson.json; if it would still pass, run \`bun run index --compatible ${id}\`.`,
			);
		}
		nextLock[id] = { version, checkers };
	}
	const versionsText = `${JSON.stringify(nextLock, null, "\t")}\n`;
	const lessonIds = new Set(lessons.map((l) => l.id));
	checkRoadmap(lessons, problem);
	const byOrder = [...lessons].sort((a, b) => a.order - b.order || a.id.localeCompare(b.id));

	// --- lessons, with folder-relative paths made catalog-root-relative ---
	const outLessons = byOrder.map(({ id, file }) => {
		const dir = `lessons/${id}`;
		const checkpoints = ((file.checkpoints as Json[] | undefined) ?? []).map((cp) => {
			// resetPath: the one project folder Scriptum's "Reset this exercise"
			// restores for this checkpoint (then runs setupScript with it as $1).
			// It stays project-relative, unlike the verifier path below.
			if (cp.resetPath !== undefined) {
				const reset = String(cp.resetPath);
				const plain = /^[A-Za-z0-9._-]+(\/[A-Za-z0-9._-]+)*$/.test(reset) && !reset.split("/").some((s) => s === "." || s === "..");
				if (!plain) problem(`${dir}: checkpoint "${cp.id}" has resetPath "${reset}", which must be a plain folder path inside project/`);
				else if (!existsSync(join(root, dir, "project", reset))) problem(`${dir}: checkpoint "${cp.id}" resets project/${reset}, which doesn't exist`);
			}
			const verifier = cp.verifier as Json;
			if (verifier?.type === "script") {
				const rel = String(verifier.path ?? "");
				if (!existsSync(join(root, dir, rel))) problem(`${dir}: checkpoint "${cp.id}" runs ${rel}, which doesn't exist`);
				return { ...cp, verifier: { ...verifier, path: `${dir}/${rel}` } };
			}
			return cp;
		});
		const setup = typeof file.setupScript === "string" ? file.setupScript : undefined;
		if (setup && !existsSync(join(root, dir, setup))) problem(`${dir}: setupScript ${setup} doesn't exist`);
		for (const required of (file.requires as string[] | undefined) ?? []) {
			if (!lessonIds.has(required)) problem(`${dir}: requires "${required}", which isn't a lesson`);
		}
		for (const recommended of (file.recommends as string[] | undefined) ?? []) {
			if (!lessonIds.has(recommended)) problem(`${dir}: recommends "${recommended}", which isn't a lesson`);
		}
		return {
			...pick(file, ["order", "track", "title", "description", "kind"]),
			id,
			version: versionOf(file),
			subdir: `${dir}/project`,
			...(setup ? { setupScript: `${dir}/${setup}` } : {}),
			...pick(file, ["requires", "recommends"]),
			// null (off the roadmap) is left out: Scriptum reads no roadmapUnit as an extra.
			...(typeof file.roadmapUnit === "string" ? { roadmapUnit: file.roadmapUnit } : {}),
			...(file.milestone === true ? { milestone: true } : {}),
			...pick(file, ["showScope", "restrictTools"]),
			checkpoints,
		};
	});
	// Put id first, like every other record.
	const lessonOut = outLessons.map((l) => {
		const { id, ...rest } = l as Json & { id: string };
		return { id, ...rest };
	});

	// --- guides ---
	type GuideSource = { id: string; path: string; file: Json; owners: string[] };
	const guides = new Map<string, GuideSource>();
	const addGuide = (id: string, path: string, owner: string | null) => {
		if (!ID.test(id)) problem(`${path}: guide file names are lowercase kebab-case`);
		if (guides.has(id)) {
			problem(`${path}: a guide called "${id}" already exists at ${guides.get(id)?.path}`);
			return;
		}
		const file = readJson(join(root, path));
		for (const key of ["title", "topic", "order", "steps"]) {
			if (file[key] === undefined) problem(`${path}: missing "${key}"`);
		}
		guides.set(id, { id, path, file, owners: owner ? [owner] : [] });
	};
	for (const id of lessonIds) {
		for (const name of jsonFiles(join(root, "lessons", id, "guides"))) {
			addGuide(name.replace(/\.json$/, ""), `lessons/${id}/guides/${name}`, id);
		}
	}
	const shared = new Set<string>();
	for (const name of jsonFiles(join(root, "common", "guides"))) {
		const id = name.replace(/\.json$/, "");
		shared.add(id);
		addGuide(id, `common/guides/${name}`, null);
	}
	for (const { id, file } of lessons) {
		for (const use of (file.guides as string[] | undefined) ?? []) {
			if (!shared.has(use)) {
				problem(`lessons/${id}/lesson.json: uses the shared guide "${use}", which isn't in common/guides/`);
				continue;
			}
			guides.get(use)?.owners.push(id);
		}
	}
	for (const id of shared) {
		if ((guides.get(id)?.owners.length ?? 0) === 0) {
			problem(`common/guides/${id}.json: no lesson lists it in "guides"`);
		}
	}
	const orderOf = new Map(lessons.map((l) => [l.id, l.order]));
	const outGuides = [...guides.values()]
		.map((g): Json & { id: string } => ({
			id: g.id,
			...pick(g.file, ["title", "summary", "topic", "order"]),
			modules: [...g.owners].sort((a, b) => (orderOf.get(a) ?? 0) - (orderOf.get(b) ?? 0) || a.localeCompare(b)),
			...pick(g.file, ["needs", "kind"]),
			path: g.path,
		}))
		.sort((a, b) => Number(a.order) - Number(b.order) || a.id.localeCompare(b.id));

	const text = `${JSON.stringify({ schemaVersion: 3, lessons: lessonOut, guides: outGuides }, null, "\t")}\n`;
	return { text, problems, versionsText };
}

if (import.meta.main) {
	const args = process.argv.slice(2);
	const compatible: string[] = [];
	const positional: string[] = [];
	for (let i = 0; i < args.length; i++) {
		if (args[i] === "--compatible") compatible.push(args[++i] ?? "");
		else if (!args[i].startsWith("-")) positional.push(args[i]);
	}
	const root = resolve(positional[0] ?? fileURLToPath(new URL("../..", import.meta.url)));
	const { text, problems, versionsText } = buildCatalog(root, { compatible });
	if (problems.length > 0) {
		console.error(`catalog.json can't be built:\n${problems.map((p) => `  - ${p}`).join("\n")}`);
		process.exit(1);
	}
	const target = join(root, "catalog.json");
	const versionsTarget = join(root, VERSIONS_FILE);
	if (args.includes("--check")) {
		const current = existsSync(target) ? readFileSync(target, "utf8") : "";
		const currentVersions = existsSync(versionsTarget) ? readFileSync(versionsTarget, "utf8") : "";
		if (current !== text || currentVersions !== versionsText) {
			console.error(`catalog.json or ${VERSIONS_FILE} is out of date. Run \`bun run index\` and commit them.`);
			process.exit(1);
		}
		console.log("catalog.json is up to date.");
	} else {
		writeFileSync(target, text);
		writeFileSync(versionsTarget, versionsText);
		console.log(`Wrote catalog.json (${text.length} bytes) and ${VERSIONS_FILE}.`);
	}
}
