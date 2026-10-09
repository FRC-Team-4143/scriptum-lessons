package frc.robot;

import com.marswars.logging.MwLog;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.mechanisms.DifferentialDriveMech;
import lesson.LessonLoop;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * Write your code here. This robot already drives with tank drive. Now it learns to feel how far
 * and how fast it has gone, using the sensors inside the drive motors.
 */
public class Robot extends LoggedRobot {
  private final XboxController controller = new XboxController(0);
  private final DifferentialDriveMech drive =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop = new LessonLoop(drive);

  // Filled in every loop by robotPeriodic() so autonomousPeriodic() can use it too.
  private double distanceMeters = 0.0;

  @Override
  public void robotPeriodic() {
    loop.doControlLoop();

    // The drivetrain reports wheel ROTATIONS. One rotation moves the robot one wheel circumference.
    double metersPerRotation = 2.0 * Math.PI * Constants.WHEEL_RADIUS_METERS;

    // STEP 1: change the readings from rotations into meters.
    double leftMeters = drive.getLeftPositionRotations() * metersPerRotation;
    double rightMeters = drive.getRightPositionRotations() * metersPerRotation;
    double leftMetersPerSecond = drive.getLeftVelocityRps() * metersPerRotation;
    double rightMetersPerSecond = drive.getRightVelocityRps() * metersPerRotation;

    // STEP 2: describe the whole robot, not just each side.
    double linearSpeed = (leftMetersPerSecond + rightMetersPerSecond) / 2.0;
    double angularSpeed = (rightMetersPerSecond - leftMetersPerSecond) / Constants.TRACK_WIDTH_METERS;
    distanceMeters = (leftMeters + rightMeters) / 2.0;

    // Send the numbers to AdvantageScope.
    MwLog.log("Robot/LinearSpeed", linearSpeed);
    MwLog.log("Robot/AngularSpeed", angularSpeed);
    MwLog.log("Robot/Distance", distanceMeters);
  }

  @Override
  public void disabledPeriodic() {
    drive.setDutyCycles(0.0, 0.0);
  }

  @Override
  public void teleopPeriodic() {
    double forward = -controller.getLeftY();
    double turn = controller.getRightX();
    if (Math.abs(forward) < Constants.DEADBAND) {
      forward = 0.0;
    }
    if (Math.abs(turn) < Constants.DEADBAND) {
      turn = 0.0;
    }
    drive.setDutyCycles(forward + turn, forward - turn);
  }

  @Override
  public void autonomousInit() {
    // "Reset" means: forget everything driven so far and start counting from zero again. Without
    // it, each time you run Auto the distance would pick up where the last run left off.
    drive.resetEncoders();
  }

  @Override
  public void autonomousPeriodic() {
    // STEP 3: BANG-BANG CONTROL. Drive forward at 0.4 until the robot has gone 2.0 meters, then
    // stop.
    // "Bang-bang" means the motors are either fully on or fully off, nothing in between.
    if (distanceMeters < 2.0) {
      drive.setDutyCycles(0.4, 0.4);
    } else {
      drive.setDutyCycles(0.0, 0.0);
    }
  }
}
