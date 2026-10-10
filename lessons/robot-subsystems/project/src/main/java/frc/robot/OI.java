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

  /** True while the driver holds the shoot button. */
  public static boolean getShootButton() {
    // TODO: return true while the right bumper is held. XboxController has a method for it:
    // driver_controller_.getRightBumperButton()
    return false;
  }

  /** True while the driver holds the index button. */
  public static boolean getIndexButton() {
    // TODO: return true while the left bumper is held (getLeftBumperButton()).
    return false;
  }
}
