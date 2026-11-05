// Runs the git lesson's setup + verify scripts against a real git
// checkout on the host (no Docker needed) - the same round trip
// CheckpointManager drives inside a Scriptum workspace container, just
// without the container. Confirms every checkpoint fails on the fresh
// scenario and passes once solved with the exact git commands the lesson's
// READMEs teach, which is what actually caught the sed/tag bugs while
// authoring this lesson.
import { describe, expect, test } from "bun:test";
import { cp, mkdtemp, rm, stat } from "node:fs/promises";
import { tmpdir } from "node:os";
import { basename, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const lessonDir = resolve(fileURLToPath(new URL(".", import.meta.url)));
const moduleDir = resolve(lessonDir, "project");
const checkpointsDir = resolve(lessonDir, "checkpoints");

const GIT_ENV = [
	"-c",
	"user.name=Student",
	"-c",
	"user.email=student@test.local",
];

function run(cwd: string, args: string[]): { exitCode: number; text: string } {
	const result = Bun.spawnSync(args, {
		cwd,
		env: process.env,
		stdout: "pipe",
		stderr: "pipe",
	});
	const text = `${result.stdout.toString()}${result.stderr.toString()}`;
	return { exitCode: result.exitCode ?? 1, text };
}

function git(
	cwd: string,
	...args: string[]
): { exitCode: number; text: string } {
	return run(cwd, ["git", ...GIT_ENV, ...args]);
}

function requireOk(
	result: { exitCode: number; text: string },
	label: string,
): void {
	if (result.exitCode !== 0) {
		throw new Error(`${label} failed:\n${result.text}`);
	}
}

async function makeProject(): Promise<string> {
	const dir = await mkdtemp(join(tmpdir(), "frc-git-"));
	// The hidden origin the push and pull exercises use, kept with the test's own files.
	process.env.SCRIPTUM_ORIGINS = join(dir, "..", `${basename(dir)}-origins`);
	await cp(moduleDir, dir, { recursive: true });
	const setup = run(dir, ["bash", join(checkpointsDir, "setup.sh")]);
	requireOk(setup, "setup.sh");
	return dir;
}

const lessonJson = (await Bun.file(join(lessonDir, "lesson.json")).json()) as {
	checkpoints: { id: string; resetPath?: string }[];
};

// What Scriptum's "Reset this exercise" does for a checkpoint: put the
// folder's starting files back, then run the setup script for just it.
async function resetExercise(project: string, folder: string) {
	await rm(join(project, folder), { recursive: true, force: true });
	await cp(join(moduleDir, folder), join(project, folder), { recursive: true });
	return run(project, ["bash", join(checkpointsDir, "setup.sh"), folder]);
}

// Every ref in the exercise and in its hidden origin, with the commit it
// points at: equal snapshots mean byte-identical history.
function refs(project: string, folder: string): string {
	const local = git(join(project, folder), "for-each-ref", "--format=%(objectname) %(refname)");
	requireOk(local, `refs of ${folder}`);
	const origin = join(process.env.SCRIPTUM_ORIGINS as string, `${folder}.git`);
	const remote = git(origin, "for-each-ref", "--format=%(objectname) %(refname)");
	requireOk(remote, `origin refs of ${folder}`);
	const status = git(join(project, folder), "status", "--porcelain", "--branch");
	return `${local.text}--origin--\n${remote.text}--status--\n${status.text}`;
}

function verify(project: string, checkpointId: string) {
	return run(project, [
		"bash",
		join(checkpointsDir, "verify", `${checkpointId}.sh`),
		project,
	]);
}

describe("git lesson", () => {
	test("every checkpoint fails on the freshly set up scenarios", async () => {
		const project = await makeProject();
		try {
			for (const id of [
				"first-commit",
				"feature-branch",
				"merge",
				"merge-conflict",
				"rebase",
				"push",
				"pull",
				"stash",
			]) {
				expect(verify(project, id).exitCode).not.toBe(0);
			}
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("first-commit passes once the student edits roster.txt and commits", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "01-first-commit");
			await Bun.write(join(dir, "roster.txt"), "# Team Roster\nStudent One\n");
			requireOk(git(dir, "add", "-A"), "git add");
			requireOk(
				git(dir, "commit", "-m", "Add my name to the roster"),
				"git commit",
			);
			expect(verify(project, "first-commit").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("first-commit still fails for a too-short commit message", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "01-first-commit");
			await Bun.write(join(dir, "roster.txt"), "# Team Roster\nStudent One\n");
			requireOk(git(dir, "add", "-A"), "git add");
			requireOk(git(dir, "commit", "-m", "wip"), "git commit");
			expect(verify(project, "first-commit").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("every exercise has one hidden local origin, so nothing can publish to GitHub", async () => {
		const project = await makeProject();
		try {
			for (const name of [
				"01-first-commit",
				"02-feature-branch",
				"03-merge",
				"04-merge-conflict",
				"05-rebase",
				"06-push",
				"07-pull",
				"08-stash",
			]) {
				const remotes = git(join(project, name), "remote", "-v");
				requireOk(remotes, `remotes of ${name}`);
				const lines = remotes.text.trim().split("\n");
				expect(lines).toHaveLength(2);
				expect(lines[0]).toContain(`${basename(project)}-origins/${name}.git`);
				const origin = lines[0]?.split(/\s+/)[1] ?? "";
				expect((await stat(origin)).isDirectory()).toBe(true);
			}
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("feature-branch names the branch when capitals are why it didn't count", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "02-feature-branch");
			requireOk(
				git(dir, "checkout", "-b", "issue-12-Trynumber4", "develop"),
				"create branch",
			);
			const result = verify(project, "feature-branch");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("issue-12-Trynumber4 doesn't count");
			expect(result.text).toContain("lowercase");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("a missing exercise folder says so instead of a raw cd error", async () => {
		const project = await makeProject();
		try {
			await rm(join(project, "06-push"), { recursive: true, force: true });
			const result = verify(project, "push");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("The 06-push folder is missing");
			expect(result.text).not.toContain("line 4");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("feature-branch passes for a correctly named branch with 2+ commits off develop", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "02-feature-branch");
			requireOk(git(dir, "checkout", "develop"), "checkout develop");
			requireOk(
				git(dir, "checkout", "-b", "issue-12-turbo-boost"),
				"create branch",
			);
			await Bun.write(
				join(dir, "FEATURES.md"),
				"# Features\n\n- Tank drive with arcade controls\n- Turbo boost\n",
			);
			requireOk(git(dir, "add", "-A"), "add");
			requireOk(
				git(dir, "commit", "-m", "Add turbo boost to features"),
				"commit 1",
			);
			await Bun.write(
				join(dir, "FEATURES.md"),
				"# Features\n\n- Tank drive with arcade controls\n- Turbo boost\n- Cooldown timer\n",
			);
			requireOk(git(dir, "add", "-A"), "add");
			requireOk(
				git(dir, "commit", "-m", "Add cooldown timer note"),
				"commit 2",
			);
			expect(verify(project, "feature-branch").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("feature-branch fails if the student commits to main", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "02-feature-branch");
			requireOk(git(dir, "checkout", "-b", "issue-12-turbo-boost"), "branch");
			await Bun.write(join(dir, "FEATURES.md"), "# Features\n\n- x\n- y\n");
			requireOk(git(dir, "add", "-A"), "add");
			requireOk(git(dir, "commit", "-m", "first"), "commit 1");
			requireOk(
				git(dir, "commit", "--allow-empty", "-m", "second"),
				"commit 2",
			);
			requireOk(git(dir, "checkout", "main"), "checkout main");
			await Bun.write(join(dir, "FEATURES.md"), "oops");
			requireOk(git(dir, "add", "-A"), "add");
			requireOk(git(dir, "commit", "-m", "accidentally on main"), "bad commit");
			requireOk(git(dir, "checkout", "issue-12-turbo-boost"), "back");
			expect(verify(project, "feature-branch").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("merge passes once issue-7-auto-distance is merged into develop", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "03-merge");
			requireOk(git(dir, "checkout", "develop"), "checkout develop");
			requireOk(
				git(dir, "merge", "--no-edit", "issue-7-auto-distance"),
				"merge",
			);
			expect(verify(project, "merge").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("merge-conflict passes once the conflict is resolved and committed", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "04-merge-conflict");
			requireOk(git(dir, "checkout", "develop"), "checkout develop");
			// Expected to exit non-zero: this merge conflicts by design.
			git(dir, "merge", "issue-9-max-speed");
			await Bun.write(
				join(dir, "Constants.java"),
				"public final class Constants {\n    public static final double MAX_SPEED = 5.0;\n}\n",
			);
			requireOk(git(dir, "add", "-A"), "add");
			requireOk(git(dir, "commit", "--no-edit"), "commit merge");
			expect(verify(project, "merge-conflict").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("merge-conflict fails while conflict markers are still present", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "04-merge-conflict");
			requireOk(git(dir, "checkout", "develop"), "checkout develop");
			git(dir, "merge", "issue-9-max-speed");
			expect(verify(project, "merge-conflict").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("rebase passes once the feature branch is rebased onto develop", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "05-rebase");
			requireOk(git(dir, "checkout", "issue-15-led-colors"), "checkout");
			requireOk(git(dir, "rebase", "develop"), "rebase");
			expect(verify(project, "rebase").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("rebase fails if the student merges develop in instead of rebasing", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "05-rebase");
			requireOk(git(dir, "checkout", "issue-15-led-colors"), "checkout");
			requireOk(git(dir, "merge", "--no-edit", "develop"), "merge");
			expect(verify(project, "rebase").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("push passes once the branch is on origin, and not before", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "06-push");
			expect(verify(project, "push").exitCode).not.toBe(0);
			requireOk(git(dir, "push", "origin", "issue-18-team-colors"), "push");
			expect(verify(project, "push").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("push fails if only another branch was pushed", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "06-push");
			requireOk(git(dir, "push", "origin", "develop"), "push develop");
			expect(verify(project, "push").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("pull passes once develop has the teammate's commit (pull, or fetch then merge)", async () => {
		for (const how of [["pull"], ["fetch"]]) {
			const project = await makeProject();
			try {
				const dir = join(project, "07-pull");
				requireOk(git(dir, ...how), how[0] as string);
				if (how[0] === "fetch") {
					// Fetching alone downloads the commit but doesn't bring it in.
					expect(verify(project, "pull").exitCode).not.toBe(0);
					requireOk(git(dir, "merge", "origin/develop"), "merge");
				}
				expect(verify(project, "pull").exitCode).toBe(0);
				expect(await Bun.file(join(dir, "Schedule.txt")).text()).toContain("Saturday");
			} finally {
				await rm(project, { recursive: true, force: true });
			}
		}
	});

	test("stash refuses to switch branches until the work is shelved, then passes after the whole round trip", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "08-stash");
			// Git won't let the student carry unfinished work over to develop.
			expect(git(dir, "checkout", "develop").exitCode).not.toBe(0);
			requireOk(git(dir, "stash"), "stash");
			requireOk(git(dir, "checkout", "develop"), "checkout develop");
			await Bun.write(
				join(dir, "Notes.md"),
				"# Pit Notes\nThe pit crew meets at 8am.\nThe new motor is not ordered yet.\n",
			);
			requireOk(git(dir, "commit", "-am", "Fix typo in pit notes"), "fix");
			// Mid-way (still on develop, work on the shelf) isn't done yet.
			expect(verify(project, "stash").exitCode).not.toBe(0);
			requireOk(git(dir, "checkout", "issue-21-new-motor"), "back");
			requireOk(git(dir, "stash", "pop"), "pop");
			expect(verify(project, "stash").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("stash fails if the unfinished work was committed instead", async () => {
		const project = await makeProject();
		try {
			const dir = join(project, "08-stash");
			requireOk(git(dir, "commit", "-am", "wip"), "commit wip");
			requireOk(git(dir, "checkout", "develop"), "checkout develop");
			await Bun.write(join(dir, "Notes.md"), "# Pit Notes\nThe pit crew meets at 8am.\nThe new motor is not ordered yet.\n");
			requireOk(git(dir, "commit", "-am", "Fix typo"), "fix");
			requireOk(git(dir, "checkout", "issue-21-new-motor"), "back");
			expect(verify(project, "stash").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});

	test("every checkpoint resets exactly its own exercise folder", () => {
		const folders = lessonJson.checkpoints.map((c) => c.resetPath);
		expect(folders).toEqual([
			"01-first-commit",
			"02-feature-branch",
			"03-merge",
			"04-merge-conflict",
			"05-rebase",
			"06-push",
			"07-pull",
			"08-stash",
		]);
	});

	test("resetting one exercise gives the same history a full load gives it", async () => {
		const fresh = await makeProject();
		const expected = new Map<string, string>();
		for (const c of lessonJson.checkpoints) {
			expected.set(c.resetPath as string, refs(fresh, c.resetPath as string));
		}
		await rm(fresh, { recursive: true, force: true });

		const project = await makeProject();
		try {
			for (const c of lessonJson.checkpoints) {
				const folder = c.resetPath as string;
				const result = await resetExercise(project, folder);
				requireOk(result, `setup.sh ${folder}`);
				expect(result.text).toContain(`${folder} is back to how it started`);
				expect(refs(project, folder)).toBe(expected.get(folder) as string);
				expect(verify(project, c.id).exitCode).not.toBe(0);
			}
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("resetting a tangled rebase leaves the other exercises alone", async () => {
		const project = await makeProject();
		try {
			// Finish exercise 1 and push exercise 6.
			const first = join(project, "01-first-commit");
			await Bun.write(join(first, "roster.txt"), "# Team Roster\nStudent One\n");
			requireOk(git(first, "commit", "-am", "Add my name to the roster"), "commit");
			requireOk(git(join(project, "06-push"), "push", "origin", "issue-18-team-colors"), "push");

			// Tangle exercise 5: merge develop in, then wander off on a new branch.
			const rebase = join(project, "05-rebase");
			requireOk(git(rebase, "merge", "--no-edit", "develop"), "merge");
			requireOk(git(rebase, "checkout", "-b", "lost"), "branch");
			expect(verify(project, "rebase").exitCode).not.toBe(0);

			requireOk(await resetExercise(project, "05-rebase"), "reset 05-rebase");

			expect(git(rebase, "rev-parse", "--verify", "-q", "lost").exitCode).not.toBe(0);
			requireOk(git(rebase, "rebase", "develop"), "rebase");
			expect(verify(project, "rebase").exitCode).toBe(0);
			expect(verify(project, "first-commit").exitCode).toBe(0);
			expect(verify(project, "push").exitCode).toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);

	test("setup.sh refuses a folder that isn't one of the exercises", async () => {
		const project = await makeProject();
		try {
			const before = refs(project, "01-first-commit");
			for (const bad of ["09-nope", "../01-first-commit", ""]) {
				const result = run(project, ["bash", join(checkpointsDir, "setup.sh"), bad]);
				expect(result.exitCode).not.toBe(0);
				expect(result.text).toContain("isn't one of this lesson's exercises");
			}
			expect(refs(project, "01-first-commit")).toBe(before);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	}, 30_000);
});
