// Builds catalog.json, the index Scriptum reads, from the lesson folders.
//
//   bun run index            write catalog.json
//   bun run index --check    fail if catalog.json is out of date (the tests do this)
//
// A lesson is one folder, lessons/<id>/, holding everything about it:
//   lesson.json      title, kind, track, order, requires, checkpoints, shared guides it uses
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
import { existsSync, readdirSync, readFileSync, statSync, writeFileSync } from "node:fs";
import { join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const ID = /^[a-z0-9]+(?:-[a-z0-9]+)*$/;

type Json = Record<string, unknown>;

export type Built = { text: string; problems: string[] };

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

export function buildCatalog(root: string): Built {
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
		lessons.push({ id, order: Number(file.order), file });
	}
	const lessonIds = new Set(lessons.map((l) => l.id));
	const byOrder = [...lessons].sort((a, b) => a.order - b.order || a.id.localeCompare(b.id));

	// --- lessons, with folder-relative paths made catalog-root-relative ---
	const outLessons = byOrder.map(({ id, file }) => {
		const dir = `lessons/${id}`;
		const checkpoints = ((file.checkpoints as Json[] | undefined) ?? []).map((cp) => {
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
		return {
			...pick(file, ["order", "track", "title", "description", "kind"]),
			id,
			subdir: `${dir}/project`,
			...(setup ? { setupScript: `${dir}/${setup}` } : {}),
			...pick(file, ["requires", "showScope", "restrictTools"]),
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
	return { text, problems };
}

if (import.meta.main) {
	const root = resolve(process.argv.find((a) => !a.startsWith("-") && a !== process.argv[0] && a !== process.argv[1]) ?? fileURLToPath(new URL("../..", import.meta.url)));
	const { text, problems } = buildCatalog(root);
	if (problems.length > 0) {
		console.error(`catalog.json can't be built:\n${problems.map((p) => `  - ${p}`).join("\n")}`);
		process.exit(1);
	}
	const target = join(root, "catalog.json");
	if (process.argv.includes("--check")) {
		const current = existsSync(target) ? readFileSync(target, "utf8") : "";
		if (current !== text) {
			console.error("catalog.json is out of date. Run `bun run index` and commit it.");
			process.exit(1);
		}
		console.log("catalog.json is up to date.");
	} else {
		writeFileSync(target, text);
		console.log(`Wrote catalog.json (${text.length} bytes).`);
	}
}
