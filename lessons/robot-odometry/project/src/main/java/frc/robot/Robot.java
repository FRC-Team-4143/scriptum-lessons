package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.mechanisms.DifferentialDriveMech;
import lesson.LessonLoop;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * Now the robot learns where it IS on the field. The drivetrain mech does that work with WPILib's
 * kinematics and pose estimator (open mechanisms/DifferentialDriveMech.java). This file gets the
 * controller its own class, OI, and uses the pose in autonomous.
 */
public class Robot extends LoggedRobot {
  // TODO (STEP 1): delete this field. The OI class will read the controller for you.
  private final XboxController controller_ = new XboxController(0);

  private final DifferentialDriveMech drive_ =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop_ = new LessonLoop(drive_);

  @Override
  public void robotPeriodic() {
    // The loop reads the sensors, updates the drivetrain's pose, and sends the motor commands.
    loop_.doControlLoop();
  }

  @Override
  public void disabledPeriodic() {
    drive_.arcadeDrive(0.0, 0.0);
  }

  @Override
  public void teleopPeriodic() {
    // TODO (STEP 1): use OI.getForward() and OI.getTurn() instead of the controller.
    drive_.arcadeDrive(
        MathUtil.applyDeadband(-controller_.getLeftY(), 0.1),
        MathUtil.applyDeadband(controller_.getLeftX(), 0.1));
  }

  @Override
  public void autonomousInit() {
    // Start from the origin.
    drive_.resetPose();
  }

  @Override
  public void autonomousPeriodic() {
    // STEP 5: Last lesson the robot counted its own distance. Now drive forward at 0.4 until the
    // pose says x is at least 3.0 meters, then stop. Use drive_.getPose().getX().
    drive_.arcadeDrive(0.0, 0.0); // TODO
  }
}
