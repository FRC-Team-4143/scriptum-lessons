package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

/**
 * OI stands for "Operator Interface": the controls a human uses. Everything about the controller
 * lives in this one class, and the rest of the robot code just asks it questions like "how far
 * forward is the driver pushing?" That is how our competition robot is organized too.
 *
 * <p>Notice two keywords. "abstract" means nobody ever makes an OI object. "static" means the
 * fields and methods belong to the class itself, so you call them like OI.getForward().
 */
public abstract class OI {
  private static final XboxController driverController = new XboxController(0);

  // TODO: write  public static double getForward()
  // It returns how far forward the driver is pushing: the left stick Y, with the minus sign.
  // Apply the same deadband you wrote by hand in lesson 1, but with WPILib's built-in version:
  //   MathUtil.applyDeadband(value, 0.1)   (import edu.wpi.first.math.MathUtil)

  // TODO: write  public static double getTurn()
  // It returns how much the driver wants to turn: the right stick X (also with a deadband).
}
