package lesson;

/**
 * Provided for you, no need to edit. Robot.java calls Checks.update() every loop so the lesson's
 * Verify button can see the biggest and smallest values your robot reached.
 */
public final class Checks {
  private static final LessonChecks checks =
      new LessonChecks(
          "Drive/LeftOutput",
          "Drive/PoseX",
          "Drive/PoseY",
          "Drive/PoseYawDeg",
          "Subsystem/Shooter/ShotCount");

  private Checks() {}

  public static void update() {
    checks.update();
  }
}
