package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;

/**
 * OI stands for "Operator Interface": everything the human drivers do. The rest of the code asks OI
 * questions and never touches the controller directly.
 */
public abstract class OI {
  private static final XboxController driver_controller_ = new XboxController(0);

  /** How far forward the driver is pushing, -1.0 to 1.0. */
  public static double getForward() {
    return MathUtil.applyDeadband(-driver_controller_.getLeftY(), 0.1);
  }

  /** How much the driver wants to turn, -1.0 to 1.0. */
  public static double getTurn() {
    return MathUtil.applyDeadband(driver_controller_.getLeftX(), 0.1);
  }

  public static boolean getShootButton() {
    return driver_controller_.getRightBumperButton();
  }

  public static boolean getIndexButton() {
    return driver_controller_.getLeftBumperButton();
  }
}
