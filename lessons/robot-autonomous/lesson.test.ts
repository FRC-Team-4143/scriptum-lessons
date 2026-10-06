// robot-autonomous: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("robot-autonomous script checkpoints", () => {
	test("robot-autonomous: chooser check fails fresh, passes once both autos are built and added", async () => {
		await L.roundTrip(["autos-in-chooser", "square-loop"],
			["RobotContainer.java", "autos/Autos.java"],
		);
	}, 30_000);
});
