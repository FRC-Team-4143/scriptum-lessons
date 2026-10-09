package lesson;

/**
 * Provided for you, no need to edit. Robot.java calls Checks.update() every loop so the lesson's
 * Verify button can see the biggest and smallest values your robot reached.
 */
public final class Checks {
  private static final LessonChecks checks =
      new LessonChecks(
          "Drive/LeftOutput",
          "Subsystem/Shooter/ShotCount",
          // How far the robot REALLY is (not its estimate) from the two field targets, and how far
          // from the scoring spot it was when it last shot.
          "Drive/DistanceToPickup",
          "Drive/DistanceToScoreSpot",
          "Subsystem/Shooter/ShotDistanceFromScoreSpot");

  private Checks() {}

  public static void update() {
    checks.update();
  }
}
