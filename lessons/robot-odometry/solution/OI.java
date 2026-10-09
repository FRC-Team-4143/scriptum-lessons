package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;

public abstract class OI {
  private static final XboxController driverController = new XboxController(0);

  public static double getForward() {
    return MathUtil.applyDeadband(-driverController.getLeftY(), 0.1);
  }

  public static double getTurn() {
    return MathUtil.applyDeadband(driverController.getLeftX(), 0.1);
  }
}
