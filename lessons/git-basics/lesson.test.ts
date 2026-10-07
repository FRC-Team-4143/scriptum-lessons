// Runs the git-basics lesson's setup + verify scripts against a real git
// checkout on the host (no Docker needed) - the same round trip
// CheckpointManager drives inside a Scriptum workspace container, just
// without the container. Confirms every checkpoint fails on the fresh
// scenario and passes once solved with the exact git commands the lesson's
// READMEs teach, which is what actually caught the sed/tag bugs while
// authoring this lesson. Ported from Scriptum's scripts/lessons/git-basics.test.ts
// when git-basics moved from the bundled catalog to this repo - see
// Scriptum's docs/decisions/044-remote-catalog-checkpoints.md.
import { describe, expect, test } from "bun:test";
import { cp, mkdtemp, rm } from "node:fs/promises";
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
	const result = Bun.spawnSync(args, { cwd, stdout: "pipe", stderr: "pipe" });
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
	const dir = await mkdtemp(join(tmpdir(), "frc-git-basics-"));
	// The hidden origin the push and pull exercises use, kept with the test's own files.
	process.env.SCRIPTUM_ORIGINS = join(dir, "..", `${basename(dir)}-origins`);
	await cp(moduleDir, dir, { recursive: true });
	const setup = run(dir, ["bash", join(checkpointsDir, "setup.sh")]);
	requireOk(setup, "setup.sh");
	return dir;
}

function verify(project: string, checkpointId: string) {
	return run(project, [
		"bash",
		join(checkpointsDir, "verify", `${checkpointId}.sh`),
		project,
	]);
}

describe("git-basics lesson", () => {
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
});
