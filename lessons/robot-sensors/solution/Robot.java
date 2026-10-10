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

  // Filled in every loop by robotPeriodic() so autonomousPeriodic() can use it too.
  private double distance_meters_ = 0.0;

  @Override
  public void robotPeriodic() {
    loop_.doControlLoop();

    // The drivetrain reports wheel ROTATIONS. One rotation moves the robot one wheel circumference.
    double meters_per_rotation = 2.0 * Math.PI * Constants.WHEEL_RADIUS_METERS;

    // STEP 1: change the readings from rotations into meters.
    double left_meters = drive_.getLeftPositionRotations() * meters_per_rotation;
    double right_meters = drive_.getRightPositionRotations() * meters_per_rotation;
    double left_meters_per_second = drive_.getLeftVelocityRps() * meters_per_rotation;
    double right_meters_per_second = drive_.getRightVelocityRps() * meters_per_rotation;

    // STEP 2: describe the whole robot, not just each side.
    double linear_speed = (left_meters_per_second + right_meters_per_second) / 2.0;
    double angular_speed =
        (right_meters_per_second - left_meters_per_second) / Constants.TRACK_WIDTH_METERS;
    distance_meters_ = (left_meters + right_meters) / 2.0;

    // Send the numbers to AdvantageScope.
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
    // STEP 3: BANG-BANG CONTROL. Drive forward at 0.4 until the robot has gone 2.0 meters, then
    // stop.
    // "Bang-bang" means the motors are either fully on or fully off, nothing in between.
    if (distance_meters_ < 2.0) {
      drive_.setDutyCycles(0.4, 0.4);
    } else {
      drive_.setDutyCycles(0.0, 0.0);
    }
  }
}
