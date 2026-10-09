package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;

public abstract class OI {
  private static final XboxController driver_controller_ = new XboxController(0);

  public static double getForward() {
    return MathUtil.applyDeadband(-driver_controller_.getLeftY(), 0.1);
  }

  public static double getTurn() {
    return MathUtil.applyDeadband(driver_controller_.getLeftX(), 0.1);
  }
}
