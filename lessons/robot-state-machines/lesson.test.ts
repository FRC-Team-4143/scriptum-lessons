// robot-state-machines: every script checkpoint fails against the untouched starter and passes against the
// reference solution in solution/. (nt4-value checkpoints need a running simulator, so they
// are not covered here; they read the "Check/..." topics that the provided LessonChecks.java
// publishes - see the lesson's README.)
import { describe, expect, test } from "bun:test";
import { hasJdk, lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

describe.skipIf(!hasJdk)("robot-state-machines script checkpoints", () => {
	test("robot-state-machines: button bindings fail fresh, pass once solved", async () => {
		await L.roundTrip(["bindings"],
			["OI.java", "subsystems/shooter/ShooterCommands.java"],
		);
	}, 30_000);
});
