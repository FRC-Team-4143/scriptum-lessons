package edu.wpi.first.wpilibj;

/** Stands in for the real controller: the OI check presses the bumpers. */
public class XboxController {
  public static boolean left_bumper = false;
  public static boolean right_bumper = false;

  public XboxController(int port) {}

  public boolean getLeftBumperButton() {
    return left_bumper;
  }

  public boolean getRightBumperButton() {
    return right_bumper;
  }

  public boolean getLeftBumper() {
    return left_bumper;
  }

  public boolean getRightBumper() {
    return right_bumper;
  }

  /** Button numbers as on a real Xbox controller: 5 is the left bumper, 6 the right. */
  public boolean getRawButton(int button) {
    return (button == 5 && left_bumper) || (button == 6 && right_bumper);
  }

  public double getRawAxis(int axis) {
    return 0.0;
  }

  public boolean getAButton() {
    return false;
  }

  public boolean getBButton() {
    return false;
  }

  public boolean getXButton() {
    return false;
  }

  public boolean getYButton() {
    return false;
  }

  public double getLeftX() {
    return 0.0;
  }

  public double getLeftY() {
    return 0.0;
  }

  public double getRightX() {
    return 0.0;
  }

  public double getRightY() {
    return 0.0;
  }

  public double getLeftTriggerAxis() {
    return 0.0;
  }

  public double getRightTriggerAxis() {
    return 0.0;
  }

  public int getPOV() {
    return -1;
  }
}
