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
  private final XboxController controller_ = new XboxController(0);
  private final DifferentialDriveMech drive_ =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop_ = new LessonLoop(drive_);

  private static final double KP = 1.0;
  private static final double KD = 0.2;
  private static final double MAX_POWER = 0.7;
  private static final double CLOSE_ENOUGH_METERS = 0.01;
  private double linear_speed_ = 0.0;

  // Filled in every loop by robotPeriodic() so autonomousPeriodic() can use it too.
  private double distance_meters_ = 0.0;

  @Override
  public void robotPeriodic() {
    loop_.doControlLoop();

    // The drivetrain reports wheel angles in RADIANS. Radians times the wheel radius is meters.
    double radius = Constants.WHEEL_RADIUS_METERS;

    // STEP 1: change the readings from radians into meters.
    double left_meters = drive_.getLeftPositionRad() * radius;
    double right_meters = drive_.getRightPositionRad() * radius;
    double left_meters_per_second = drive_.getLeftVelocityRadPerSec() * radius;
    double right_meters_per_second = drive_.getRightVelocityRadPerSec() * radius;

    // STEP 2: describe the whole robot, not just each side.
    double linear_speed = (left_meters_per_second + right_meters_per_second) / 2.0;
    double angular_speed =
        (right_meters_per_second - left_meters_per_second) / Constants.TRACK_WIDTH_METERS;
    distance_meters_ = (left_meters + right_meters) / 2.0;

    // Send the numbers to AdvantageScope.
    linear_speed_ = linear_speed;
    MwLog.log("Robot/LinearSpeed", linear_speed);
    MwLog.log("Robot/AngularSpeed", angular_speed);
    MwLog.log("Robot/Distance", distance_meters_);
  }

  @Override
  public void disabledPeriodic() {
    drive_.setDutyCycles(0.0, 0.0);
  }

  @Override
  public void teleopPeriodic() {
    double forward = -controller_.getLeftY();
    double turn = controller_.getLeftX();
    if (Math.abs(forward) < Constants.DEADBAND) {
      forward = 0.0;
    }
    if (Math.abs(turn) < Constants.DEADBAND) {
      turn = 0.0;
    }
    drive_.setDutyCycles(forward + turn, forward - turn);
  }

  @Override
  public void autonomousInit() {
    // "Reset" means: forget everything driven so far and start counting from zero again. Without
    // it, each time you run Auto the distance would pick up where the last run left off.
    drive_.resetEncoders();
  }

  @Override
  public void autonomousPeriodic() {
    // CHALLENGE: push harder the farther the mark is, and brake against the robot's own speed so
    // it doesn't coast past. A negative power pushes backward, which is the only real brake.
    if (distance_meters_ > 2.0 - CLOSE_ENOUGH_METERS && distance_meters_ < 2.0 + CLOSE_ENOUGH_METERS) {
      drive_.setDutyCycles(0.0, 0.0);
    } else {
      double power = (2.0 - distance_meters_) * KP - linear_speed_ * KD;
      power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));
      drive_.setDutyCycles(power, power);
    }
  }
}
