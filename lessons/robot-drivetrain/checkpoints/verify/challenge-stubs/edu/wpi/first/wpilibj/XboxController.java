package edu.wpi.first.wpilibj;

/** Stands in for the real controller: the check sets the sticks. */
public class XboxController {
  public static double left_y = 0.0;
  public static double left_x = 0.0;

  public XboxController(int port) {}

  public double getLeftY() {
    return left_y;
  }

  public double getLeftX() {
    return left_x;
  }

  public double getRightY() {
    return 0.0;
  }

  public double getRightX() {
    return 0.0;
  }
}
