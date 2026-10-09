package frc.robot;

import com.marswars.logging.MwLog;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.mechanisms.DifferentialDriveMech;
import lesson.LessonLoop;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * This is last lesson's robot, cleaned up. The math now lives in methods: the pure math in
 * DriveMath.java and the drivetrain commands and readings in DifferentialDriveMech.java. Your job
 * is to write those methods. This file already uses them.
 */
public class Robot extends LoggedRobot {
  private final XboxController controller_ = new XboxController(0);
  private final DifferentialDriveMech drive_ =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop_ = new LessonLoop(drive_);

  // The last 5 speed readings, to smooth out the jitter. DriveMath.average does the averaging.
  private final double[] recent_speeds_ = new double[5];
  private int next_slot_ = 0;

  @Override
  public void robotPeriodic() {
    loop_.doControlLoop();

    // The drivetrain now reports meters and meters per second, using your DriveMath methods.
    MwLog.log("Robot/Distance", drive_.getDistanceMeters());
    MwLog.log("Robot/LinearSpeed", drive_.getLinearSpeed());
    MwLog.log("Robot/AngularSpeed", drive_.getAngularSpeed());

    // Remember this reading, wrapping around the array with %, and log the average of the last 5.
    recent_speeds_[next_slot_] = drive_.getLinearSpeed();
    next_slot_ = (next_slot_ + 1) % recent_speeds_.length;
    MwLog.log("Robot/SmoothedSpeed", DriveMath.average(recent_speeds_));
  }

  @Override
  public void disabledPeriodic() {
    drive_.setLeftDutyCycle(0.0);
    drive_.setRightDutyCycle(0.0);
  }

  @Override
  public void teleopPeriodic() {
    // Arcade drive: the left stick sets forward/backward and turns. The deadband you
    // wrote by hand in lesson 1 is built into WPILib as MathUtil.applyDeadband.
    drive_.arcadeDrive(
        MathUtil.applyDeadband(-controller_.getLeftY(), 0.1),
        MathUtil.applyDeadband(controller_.getLeftX(), 0.1));
  }

  @Override
  public void autonomousInit() {
    drive_.resetEncoders();
  }

  @Override
  public void autonomousPeriodic() {
    if (drive_.getDistanceMeters() < 2.0) {
      drive_.arcadeDrive(0.4, 0.0);
    } else {
      drive_.arcadeDrive(0.0, 0.0);
    }
  }
}
