package lesson;

/**
 * Provided for you, no need to edit. Robot.java calls Checks.update() every loop so the lesson's
 * Verify button can see the biggest and smallest values your robot reached.
 */
public final class Checks {
  private static final LessonChecks checks =
      new LessonChecks(
          "Drive/LeftOutput", "Subsystem/Shooter/FlywheelVelocity", "Subsystem/Shooter/ShotCount");

  private static final AimChecks aim = new AimChecks();
  private static final LocalizationChecks localization = new LocalizationChecks();

  private Checks() {}

  public static void update() {
    checks.update();
    localization.update();
    aim.update();
  }
}
