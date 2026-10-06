// Runs each "Meet the Editor" checkpoint's verifier against the real starter
// (every one should fail) and against a copy where the student did the job
// (it should pass), plus the common slips, which should fail with a helpful message.
import { describe, expect, test } from "bun:test";
import { cp, mkdtemp, mkdir, readFile, rename, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = resolve(fileURLToPath(new URL("..", import.meta.url)));
const hasJdk = Boolean(Bun.which("javac") && Bun.which("java"));

async function project(): Promise<string> {
	const dir = await mkdtemp(join(tmpdir(), "frc-editor-basics-"));
	await cp(resolve(repoRoot, "modules/editor-basics"), dir, { recursive: true });
	return dir;
}

function verify(dir: string, id: string) {
	const r = Bun.spawnSync(["bash", join(repoRoot, "checkpoints/editor-basics/verify", `${id}.sh`), dir], {
		stdout: "pipe",
		stderr: "pipe",
	});
	return { ok: r.exitCode === 0, text: `${r.stdout}${r.stderr}`.trim() };
}

async function withProject(fn: (dir: string) => Promise<void>) {
	const dir = await project();
	try {
		await fn(dir);
	} finally {
		await rm(dir, { recursive: true, force: true });
	}
}

describe("editor-basics", () => {
	test("every checkpoint fails on the starter", async () => {
		await withProject(async (dir) => {
			for (const id of ["make-folder", "make-file", "rename-file", "delete-file"]) {
				expect(verify(dir, id).ok, id).toBe(false);
			}
		});
	});

	test("make-folder", async () => {
		await withProject(async (dir) => {
			await mkdir(join(dir, "My Team"));
			expect(verify(dir, "make-folder").text).toContain("exactly my-team");
			await mkdir(join(dir, "my-team"));
			expect(verify(dir, "make-folder").ok).toBe(true);
		});
	});

	test("make-file", async () => {
		await withProject(async (dir) => {
			expect(verify(dir, "make-file").text).toContain("my-team folder first");
			await mkdir(join(dir, "my-team"));
			await writeFile(join(dir, "about.txt"), "We build robots.\n");
			expect(verify(dir, "make-file").text).toContain("outside the my-team folder");
			await writeFile(join(dir, "my-team", "about.txt"), "\n");
			expect(verify(dir, "make-file").text).toContain("empty");
			await writeFile(join(dir, "my-team", "about.txt"), "We build robots.\n");
			expect(verify(dir, "make-file").ok).toBe(true);
		});
	});

	test("rename-file keeps the words, and spots team.txt.txt", async () => {
		await withProject(async (dir) => {
			await rename(join(dir, "untitled.txt"), join(dir, "team.txt.txt"));
			expect(verify(dir, "rename-file").text).toContain("team.txt.txt");
			await rename(join(dir, "team.txt.txt"), join(dir, "team.txt"));
			expect(verify(dir, "rename-file").ok).toBe(true);
			await writeFile(join(dir, "team.txt"), "something else\n");
			expect(verify(dir, "rename-file").ok).toBe(false);
		});
	});

	test("delete-file", async () => {
		await withProject(async (dir) => {
			await rm(join(dir, "delete-me.txt"));
			expect(verify(dir, "delete-file").ok).toBe(true);
			await rm(join(dir, "src"), { recursive: true });
			expect(verify(dir, "delete-file").ok).toBe(false);
		});
	});

	test.skipIf(!hasJdk)("edit-and-run wants a name instead of the dots", async () => {
		await withProject(async (dir) => {
			expect(verify(dir, "edit-and-run").text).toContain("three dots");
			const main = join(dir, "src", "Main.java");
			const src = await readFile(main, "utf8");
			await writeFile(main, src.replace("...", "Ada Lovelace"));
			expect(verify(dir, "edit-and-run").text).toBe("Hello, my name is Ada Lovelace");
			await writeFile(main, src.replace("...", "Ada").replace(";", ""));
			expect(verify(dir, "edit-and-run").text).toContain("doesn't run yet");
		});
	});
});
