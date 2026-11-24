package frc.robot;

/** Stands in for the student's OI so the shoot check can press buttons directly. */
public abstract class OI {
  public static boolean shoot = false;
  public static boolean index = false;
  public static boolean intake = false;

  public static double getForward() {
    return 0.0;
  }

  public static double getTurn() {
    return 0.0;
  }

  public static boolean getShootButton() {
    return shoot;
  }

  public static boolean getIndexButton() {
    return index;
  }

  public static boolean getIntakeButton() {
    return intake;
  }
}
