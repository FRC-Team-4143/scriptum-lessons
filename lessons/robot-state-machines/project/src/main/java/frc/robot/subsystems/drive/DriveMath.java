package frc.robot.subsystems.drive;

public final class DriveMath {
  private DriveMath() {}

  public static double rotationsToMeters(double wheel_rotations, double wheel_radius_meters) {
    return wheel_rotations * 2.0 * Math.PI * wheel_radius_meters;
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
}
