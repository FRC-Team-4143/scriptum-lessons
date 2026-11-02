package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.mechanisms.DifferentialDriveMech;
import lesson.LessonLoop;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * Now the robot learns where it IS on the field. The drivetrain mech does that work with WPILib's
 * kinematics and pose estimator (open mechanisms/DifferentialDriveMech.java). This file gets the
 * controller its own class, OI, and uses the pose in autonomous.
 */
public class Robot extends LoggedRobot {
  private final DifferentialDriveMech drive_ =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop_ = new LessonLoop(drive_);

  private static final Translation2d[] WAYPOINTS = {
    new Translation2d(2.0, 1.0), new Translation2d(3.5, -1.0)
  };
  private int next_ = 0;

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
    drive_.arcadeDrive(OI.getForward(), OI.getTurn());
  }

  @Override
  public void autonomousInit() {
    // Start from the origin.
    drive_.resetPose();
    next_ = 0;
  }

  @Override
  public void autonomousPeriodic() {
    // CHALLENGE: turn toward the next waypoint and drive at it, one waypoint at a time.
    if (next_ >= WAYPOINTS.length) {
      drive_.arcadeDrive(0.0, 0.0);
      return;
    }
    Pose2d pose = drive_.getPose();
    Translation2d to_waypoint = WAYPOINTS[next_].minus(pose.getTranslation());
    if (to_waypoint.getNorm() < 0.2) {
      next_++;
      return;
    }
    double heading_error = to_waypoint.getAngle().minus(pose.getRotation()).getRadians();
    double forward = Math.min(0.4, to_waypoint.getNorm() * 0.3) * Math.max(0.0, Math.cos(heading_error));
    drive_.arcadeDrive(forward, -heading_error * 0.5);
  }
}
