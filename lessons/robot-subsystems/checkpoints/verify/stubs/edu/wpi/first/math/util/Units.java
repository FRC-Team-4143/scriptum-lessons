package edu.wpi.first.math.util;

public final class Units {
  private Units() {}

  public static double inchesToMeters(double inches) {
    return inches * 0.0254;
  }

  public static double metersToInches(double meters) {
    return meters / 0.0254;
  }

  public static double degreesToRadians(double degrees) {
    return Math.toRadians(degrees);
  }

  public static double radiansToDegrees(double radians) {
    return Math.toDegrees(radians);
  }

  public static double rotationsPerMinuteToRadiansPerSecond(double rpm) {
    return rpm * 2.0 * Math.PI / 60.0;
  }

  public static double lbsToKilograms(double lbs) {
    return lbs * 0.45359237;
  }
}
