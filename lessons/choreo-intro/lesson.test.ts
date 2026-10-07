// The has-constraint checkpoint: a limit the student chose counts, the starter
// Stop point and the usual whole-path Keep In Rectangle do not.
import { describe, expect, test } from "bun:test";
import { mkdir, rm, writeFile } from "node:fs/promises";
import { mkdtemp } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);
const hasJq = Boolean(Bun.which("jq"));

async function withPath(constraints: { type: string; enabled: boolean }[], check: (project: string) => void) {
	const project = await mkdtemp(join(tmpdir(), "choreo-"));
	try {
		const deploy = join(project, "src/main/deploy/choreo");
		await mkdir(deploy, { recursive: true });
		await writeFile(
			join(deploy, "Path.traj"),
			JSON.stringify({ snapshot: { constraints: constraints.map((c) => ({ enabled: c.enabled, data: { type: c.type } })) } }),
		);
		check(project);
	} finally {
		await rm(project, { recursive: true, force: true });
	}
}

describe.skipIf(!hasJq)("choreo-intro has-constraint", () => {
	test("a velocity limit counts", async () => {
		await withPath([{ type: "StopPoint", enabled: true }, { type: "MaxVelocity", enabled: true }], (p) => {
			expect(L.verify(p, "has-constraint").exitCode).toBe(0);
		});
	});

	test("the field boundary on its own does not, even switched on", async () => {
		await withPath([{ type: "StopPoint", enabled: true }, { type: "KeepInRectangle", enabled: true }], (p) => {
			const result = L.verify(p, "has-constraint");
			expect(result.exitCode).not.toBe(0);
			expect(result.text).toContain("Keep In Rectangle");
		});
	});

	test("a switched-off limit does not count", async () => {
		await withPath([{ type: "MaxAngularVelocity", enabled: false }], (p) => {
			expect(L.verify(p, "has-constraint").exitCode).not.toBe(0);
		});
	});
});
