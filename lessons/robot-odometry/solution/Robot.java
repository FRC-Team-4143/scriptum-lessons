package frc.robot;

import frc.robot.mechanisms.DifferentialDriveMech;
import lesson.LessonLoop;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * Now the robot learns where it IS on the field. The drivetrain mech does that work with WPILib's
 * kinematics and pose estimator (open mechanisms/DifferentialDriveMech.java). This file gets the
 * controller its own class, OI, and uses the pose in autonomous.
 */
public class Robot extends LoggedRobot {
  private final DifferentialDriveMech drive =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop = new LessonLoop(drive);

  @Override
  public void robotPeriodic() {
    // The loop reads the sensors, updates the drivetrain's pose, and sends the motor commands.
    loop.doControlLoop();
  }

  @Override
  public void disabledPeriodic() {
    drive.arcadeDrive(0.0, 0.0);
  }

  @Override
  public void teleopPeriodic() {
    drive.arcadeDrive(OI.getForward(), OI.getTurn());
  }

  @Override
  public void autonomousInit() {
    // Start from the origin.
    drive.resetPose();
  }

  @Override
  public void autonomousPeriodic() {
    // STEP 5: Last lesson the robot counted its own distance. Now drive forward at 0.4 until the
    // pose says x is at least 3.0 meters, then stop. Use drive.getPose().getX().
    if (drive.getPose().getX() < 3.0) {
      drive.arcadeDrive(0.4, 0.0);
    } else {
      drive.arcadeDrive(0.0, 0.0);
    }
  }
}
