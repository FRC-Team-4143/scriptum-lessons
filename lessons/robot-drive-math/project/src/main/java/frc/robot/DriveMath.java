package frc.robot;

/**
 * Pure math for the drivetrain. Nothing in here touches a motor or a sensor: numbers go in and
 * numbers come out, so each method is easy to test.
 */
public final class DriveMath {
  private DriveMath() {}

  /**
   * Converts wheel rotations into meters driven. One full rotation of a wheel moves the robot one
   * circumference: 2 * pi * radius.
   *
   * @param wheel_rotations how many times the wheel turned
   * @param wheel_radius_meters the wheel radius in meters
   * @return the distance driven in meters
   */
  public static double rotationsToMeters(double wheel_rotations, double wheel_radius_meters) {
    // TODO: Math.PI is built in
    return 0.0;
  }

  // YOUR TURN: write linearSpeed and angularSpeed here.
  //
  // Robot.java and DifferentialDriveMech.java already call both of them, so they show red errors
  // until the names match exactly (capital letters count). Both are public and static, take
  // doubles and return a double, like rotationsToMeters above.
  //
  //   linearSpeed: takes the left and right side speeds (meters per second) and returns the
  //   robot's forward speed. If both sides move at the same speed, the robot moves at that speed;
  //   if they differ, it moves at the average.
  //
  //   angularSpeed: takes the left and right side speeds (meters per second) and the track width
  //   (meters, the distance between the wheels) and returns how fast the robot turns, in radians
  //   per second. Left (counterclockwise) is positive, so the robot turns left when the right side
  //   is faster. The answer is (right - left) divided by the track width.

  /**
   * Arcade drive math. Turns "forward" and "turn" into one speed for each side:
   *
   * <pre>
   *   left  = forward + turn
   *   right = forward - turn
   * </pre>
   *
   * A motor cannot go past 1.0. If either side ends up bigger than 1.0 (in either direction),
   * divide BOTH sides by the bigger one so the robot still turns the way you asked.
   *
   * @param forward -1.0 (backward) to 1.0 (forward)
   * @param turn -1.0 (left) to 1.0 (right)
   * @return {left, right}
   */
  public static double[] arcadeToWheelSpeeds(double forward, double turn) {
    // TODO
    return new double[] {0.0, 0.0};
  }

  /**
   * The average of the numbers in an array: add them all up with a for loop, then divide by how
   * many there are.
   *
   * @param values the numbers to average
   * @return their average
   */
  public static double average(double[] values) {
    // TODO: declare double total = 0.0; then use a for loop over every index of values to add each
    // one to total, and finally return total divided by values.length.
    return 0.0;
  }
}
