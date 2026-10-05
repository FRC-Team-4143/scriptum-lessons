package frc.robot;

public final class DriveMath {
  private DriveMath() {}

  public static double rotationsToMeters(double wheelRotations, double wheelRadiusMeters) {
    return wheelRotations * 2.0 * Math.PI * wheelRadiusMeters;
  }

  public static double linearSpeed(double leftMetersPerSecond, double rightMetersPerSecond) {
    return (leftMetersPerSecond + rightMetersPerSecond) / 2.0;
  }

  public static double angularSpeed(
      double leftMetersPerSecond, double rightMetersPerSecond, double trackWidthMeters) {
    return (rightMetersPerSecond - leftMetersPerSecond) / trackWidthMeters;
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
