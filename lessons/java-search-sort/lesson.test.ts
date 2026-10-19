// java-search-sort: every checkpoint fails against the untouched starter and passes once the reference
// solution (solution/, laid over project/) is in place. Skipped when no JDK is on PATH.
import { describe, test } from "bun:test";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("java-search-sort checkpoints", () => {
	test("every checkpoint fails fresh, passes once solved", async () => {
		await L.roundTrip(["linear-search", "binary-search", "sort-by-hand", "rankings"]);
	}, 60_000);
});
