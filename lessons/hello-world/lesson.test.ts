// hello-world: the starter's println is commented out, so its checkpoint fails until the
// student uncomments it, replaces YOUR NAME and adds the missing ;. Skipped when no JDK is on PATH.
import { describe, expect, test } from "bun:test";
import { rm, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

const main = (body: string) => `public class Main {
  public static void main(String[] args) {
    ${body}
  }
}
`;

describe.skipIf(!hasJdk)("hello-world checkpoints", () => {
	test("fails untouched and with the placeholder, passes with a name", async () => {
		const project = await L.makeProject();
		const write = (body: string) =>
			writeFile(join(project, "src", "Main.java"), main(body), "utf8");
		try {
			expect(L.verify(project, "prints-hello-world").exitCode).not.toBe(0);

			await write(`System.out.println("Hello, World! My name is Sam")`);
			expect(L.verify(project, "prints-hello-world").text).toContain("a ; is missing");

			await write(`System.out.println("Hello, World! My name is YOUR NAME");`);
			expect(L.verify(project, "prints-hello-world").exitCode).not.toBe(0);

			await write(`System.out.println("Hello, World! My name is ");`);
			expect(L.verify(project, "prints-hello-world").exitCode).not.toBe(0);

			await write(`System.out.println("Hi there!");`);
			expect(L.verify(project, "prints-hello-world").exitCode).not.toBe(0);

			await write(`System.out.println("Hello, World! My name is Sam");`);
			const passed = L.verify(project, "prints-hello-world");
			expect(passed.exitCode).toBe(0);
			expect(passed.text).toContain("My name is Sam");
		} finally {
			await rm(project, { recursive: true, force: true });
		}
	});
});
