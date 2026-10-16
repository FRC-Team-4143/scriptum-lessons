// java-debugging: every checkpoint fails against the untouched starter and passes once the reference
// solution (solution/, laid over project/) is in place. Skipped when no JDK is on PATH. The
// pause-a-hang starter loops forever, so its verify script times out after 5 s by design.
import { describe, test } from "bun:test";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("java-debugging checkpoints", () => {
	test("every checkpoint fails fresh, passes once solved", async () => {
		await L.roundTrip([
			"first-breakpoint",
			"step-into",
			"step-out",
			"conditional-breakpoint",
			"string-compare",
			"stack-trace",
			"pause-a-hang",
		]);
	}, 60_000);
});
