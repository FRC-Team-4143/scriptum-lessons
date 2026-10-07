// Helpers for a lesson's own tests (lessons/<id>/lesson.test.ts):
//
//   const L = lessonKit(import.meta.url);
//   const project = await L.makeProject();       // a throwaway copy of the starter
//   L.verify(project, "some-checkpoint");        // runs that checkpoint's verify script
//
// A lesson's folder holds everything its tests need: project/ (the starter),
// checkpoints/verify/ (the scripts), and solution/ (reference files that make
// every checkpoint pass).
import { expect } from "bun:test";
import { cp, mkdtemp, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { basename, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

export const repoRoot = resolve(fileURLToPath(new URL("../..", import.meta.url)));
export const hasJdk = Boolean(Bun.which("javac") && Bun.which("java"));
/** Where a robot project's Java lives. */
export const ROBOT_SRC = "src/main/java/frc/robot";

export function run(cwd: string, args: string[]): { exitCode: number; text: string } {
	const result = Bun.spawnSync(args, { cwd, stdout: "pipe", stderr: "pipe" });
	return {
		exitCode: result.exitCode ?? 1,
		text: `${result.stdout.toString()}${result.stderr.toString()}`,
	};
}

export function lessonKit(testFileUrl: string) {
	const dir = resolve(fileURLToPath(new URL(".", testFileUrl)));
	const id = basename(dir);

	/** A throwaway copy of the starter project, as a student would get it. */
	const makeProject = async (): Promise<string> => {
		const project = await mkdtemp(join(tmpdir(), `frc-${id}-`));
		await cp(join(dir, "project"), project, { recursive: true });
		return project;
	};

	const verify = (project: string, checkpointId: string) =>
		run(project, ["bash", join(dir, "checkpoints", "verify", `${checkpointId}.sh`), project]);

	/** Robot lessons keep solution files flat, by name; each `file` is where it goes
	 * under src/main/java/frc/robot (e.g. "subsystems/shooter/ShooterCommands.java"). */
	const applySolution = async (project: string, files: string[]): Promise<void> => {
		for (const file of files) {
			await cp(join(dir, "solution", basename(file)), join(project, ROBOT_SRC, file));
		}
	};

	/** Other lessons keep solution/ in the same shape as project/, and it is laid over it. */
	const overlaySolution = (project: string): Promise<void> =>
		cp(join(dir, "solution"), project, { recursive: true });

	/** Every checkpoint fails against the starter and passes once the solution is applied. */
	const roundTrip = async (checkpointIds: string[], files?: string[]): Promise<void> => {
		const project = await makeProject();
		try {
			for (const checkpoint of checkpointIds) {
				expect(verify(project, checkpoint).exitCode, `${checkpoint} should fail on the starter`).not.toBe(0);
			}
			if (files) await applySolution(project, files);
			else await overlaySolution(project);
			for (const checkpoint of checkpointIds) {
				const result = verify(project, checkpoint);
				expect({ checkpoint, text: result.text, code: result.exitCode }).toEqual({
					checkpoint,
					text: result.text,
					code: 0,
				});
			}
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	};

	return { dir, id, makeProject, verify, applySolution, overlaySolution, roundTrip };
}
