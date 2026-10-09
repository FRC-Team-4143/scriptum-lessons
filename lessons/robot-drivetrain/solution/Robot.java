package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.mechanisms.DifferentialDriveMech;
import lesson.LessonLoop;
import org.littletonrobotics.junction.LoggedRobot;

public class Robot extends LoggedRobot {
  private final XboxController controller = new XboxController(0);
  private final DifferentialDriveMech drive =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop = new LessonLoop(drive);

  @Override
  public void robotPeriodic() {
    loop.doControlLoop();
  }

  @Override
  public void disabledPeriodic() {
    drive.setDutyCycles(0.0, 0.0);
  }

  @Override
  public void teleopPeriodic() {
    double forward = -controller.getLeftY();
    double turn = controller.getLeftX();

    double leftSpeed = forward + turn;
    double rightSpeed = forward - turn;
    drive.setDutyCycles(leftSpeed, rightSpeed);
  }
}
