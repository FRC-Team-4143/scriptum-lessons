// robot-drive-math: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("robot-drive-math script checkpoints", () => {
	test("robot-drive-math: every DriveMath checkpoint fails fresh, passes once solved", async () => {
		await L.roundTrip(["rotations-to-meters", "linear-speed", "angular-speed", "arcade-math", "average"],
			["DriveMath.java"],
		);
	}, 30_000);
});
