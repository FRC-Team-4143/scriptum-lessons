package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.mechanisms.DifferentialDriveMech;
import lesson.LessonLoop;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * Write your code here. The robot has a drivetrain ("drive") and a controller. WPILib calls the
 * methods below at set times, for example teleopPeriodic() runs every 20 ms while the robot is
 * enabled in teleop mode.
 */
public class Robot extends LoggedRobot {
  private final XboxController controller = new XboxController(0);
  private final DifferentialDriveMech drive =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop = new LessonLoop(drive);

  @Override
  public void robotPeriodic() {
    // MWLib reads the sensors and sends the motor commands for us. Leave this line alone.
    loop.doControlLoop();
  }

  @Override
  public void disabledPeriodic() {
    drive.setDutyCycles(0.0, 0.0);
  }

  @Override
  public void teleopPeriodic() {
    // Write your drive code here. The README walks you through it.
    //
    // The one method you need is  drive.setDutyCycles(left, right)  where each number goes from
    // -1.0 (full backward) to 1.0 (full forward).
  }
}
