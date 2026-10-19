// java-unit-testing: every checkpoint fails against the untouched starter and passes once the reference
// solution (solution/, laid over project/) is in place. Skipped when no JDK is on PATH. Each verify
// compiles the tests against the real code and every mutant, so this takes longer than most.
import { describe, test } from "bun:test";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("java-unit-testing checkpoints", () => {
	test("every checkpoint fails fresh, passes once solved", async () => {
		await L.roundTrip(["deadband-tests", "clamp-tests", "conversion-tests", "find-the-bug"]);
	}, 30_000);
});
