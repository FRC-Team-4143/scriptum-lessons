// hello-world: the untouched starter already prints "Hello, World!", so its checkpoint passes
// fresh and fails once the greeting is broken. Skipped when no JDK is on PATH.
import { describe, expect, test } from "bun:test";
import { rm, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("hello-world checkpoints", () => {
	test("the untouched starter passes, a broken println fails", async () => {
		const project = await L.makeProject();
		try {
			expect(L.verify(project, "prints-hello-world").exitCode).toBe(0);
			await writeFile(
				join(project, "src", "Main.java"),
				`public class Main {
  public static void main(String[] args) {
    System.out.println("Hi there!");
  }
}
`,
				"utf8",
			);
			expect(L.verify(project, "prints-hello-world").exitCode).not.toBe(0);
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});
});
