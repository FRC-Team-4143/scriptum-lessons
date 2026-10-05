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
  private final XboxController controller = new XboxController(0);
  private final DifferentialDriveMech drive =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop = new LessonLoop(drive);

  // The last 5 speed readings, to smooth out the jitter. DriveMath.average does the averaging.
  private final double[] recentSpeeds = new double[5];
  private int nextSlot = 0;

  @Override
  public void robotPeriodic() {
    loop.doControlLoop();

    // The drivetrain now reports meters and meters per second, using your DriveMath methods.
    MwLog.log("Robot/Distance", drive.getDistanceMeters());
    MwLog.log("Robot/LinearSpeed", drive.getLinearSpeed());
    MwLog.log("Robot/AngularSpeed", drive.getAngularSpeed());

    // Remember this reading, wrapping around the array with %, and log the average of the last 5.
    recentSpeeds[nextSlot] = drive.getLinearSpeed();
    nextSlot = (nextSlot + 1) % recentSpeeds.length;
    MwLog.log("Robot/SmoothedSpeed", DriveMath.average(recentSpeeds));
  }

  @Override
  public void disabledPeriodic() {
    drive.setLeftDutyCycle(0.0);
    drive.setRightDutyCycle(0.0);
  }

  @Override
  public void teleopPeriodic() {
    // Arcade drive: the left stick sets forward/backward, the right stick turns. The deadband you
    // wrote by hand in lesson 1 is built into WPILib as MathUtil.applyDeadband.
    drive.arcadeDrive(
        MathUtil.applyDeadband(-controller.getLeftY(), 0.1),
        MathUtil.applyDeadband(controller.getRightX(), 0.1));
  }

  @Override
  public void autonomousInit() {
    drive.resetEncoders();
  }

  @Override
  public void autonomousPeriodic() {
    if (drive.getDistanceMeters() < 2.0) {
      drive.arcadeDrive(0.4, 0.0);
    } else {
      drive.arcadeDrive(0.0, 0.0);
    }
  }
}
