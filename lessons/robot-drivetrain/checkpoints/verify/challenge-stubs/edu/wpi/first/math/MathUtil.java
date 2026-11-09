package edu.wpi.first.math;

/** The two helpers a student might reach for. */
public final class MathUtil {
  private MathUtil() {}

  public static double clamp(double value, double low, double high) {
    return Math.max(low, Math.min(high, value));
  }

  public static double applyDeadband(double value, double deadband) {
    if (Math.abs(value) <= deadband) {
      return 0.0;
    }
    return Math.copySign((Math.abs(value) - deadband) / (1.0 - deadband), value);
  }
}
