// java-big-o: every checkpoint fails against the untouched starter and passes once the reference
// solution (solution/, laid over project/) is in place. Skipped when no JDK is on PATH.
import { describe, test } from "bun:test";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("java-big-o checkpoints", () => {
	test("every checkpoint fails fresh, passes once solved", async () => {
		await L.roundTrip(["classify", "latest-and-best", "fast-duplicates", "teams-in-both"]);
	}, 60_000);
});
