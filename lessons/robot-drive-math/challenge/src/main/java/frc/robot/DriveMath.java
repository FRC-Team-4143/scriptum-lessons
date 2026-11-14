package frc.robot;

public final class DriveMath {
  private DriveMath() {}

  public static double radiansToMeters(double wheel_radians, double wheel_radius_meters) {
    return wheel_radians * wheel_radius_meters;
  }

  public static double linearSpeed(double left_meters_per_second, double right_meters_per_second) {
    return (left_meters_per_second + right_meters_per_second) / 2.0;
  }

  public static double angularSpeed(
      double left_meters_per_second, double right_meters_per_second, double track_width_meters) {
    return (right_meters_per_second - left_meters_per_second) / track_width_meters;
  }

  public static double[] arcadeToWheelSpeeds(double forward, double turn) {
    double left = forward + turn;
    double right = forward - turn;
    double biggest = Math.max(Math.abs(left), Math.abs(right));
    if (biggest > 1.0) {
      left /= biggest;
      right /= biggest;
    }
    return new double[] {left, right};
  }

  public static double average(double[] values) {
    double total = 0.0;
    for (int i = 0; i < values.length; i++) {
      total += values[i];
    }
    return total / values.length;
  }

  /**
   * Smooths a list of stick values, one loop at a time, so the robot never jerks. Moving away from
   * zero changes by at most speedUpStep per loop, moving toward zero by at most slowDownStep, and
   * reversing always stops on zero first.
   */
  public static double[] smoothStick(double[] targets, double speedUpStep, double slowDownStep) {
    double[] smoothed = new double[targets.length];
    double current = 0.0;
    for (int i = 0; i < targets.length; i++) {
      double target = targets[i];
      if (current * target < 0.0) {
        // Opposite directions: slow down, and stop on zero if this step would cross it.
        double smaller = Math.abs(current) - slowDownStep;
        current = smaller <= 0.0 ? 0.0 : Math.copySign(smaller, current);
      } else if (Math.abs(target) > Math.abs(current)) {
        double bigger = Math.min(Math.abs(target), Math.abs(current) + speedUpStep);
        current = Math.copySign(bigger, target);
      } else {
        double smaller = Math.max(Math.abs(target), Math.abs(current) - slowDownStep);
        current = Math.copySign(smaller, current);
      }
      smoothed[i] = current;
    }
    return smoothed;
  }
}
