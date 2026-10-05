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
    double rightMeters = 0.0; // TODO: same as above, but for the right side
    double leftMetersPerSecond = drive.getLeftVelocityRps() * metersPerRotation;
    double rightMetersPerSecond = 0.0; // TODO: same as above, but for the right side

    // STEP 2: describe the whole robot, not just each side.
    // TODO: forward speed (m/s) = the average of the two side speeds
    double linearSpeed = 0.0;
    // TODO: turning speed (rad/s) = (right - left) / Constants.TRACK_WIDTH_METERS
    double angularSpeed = 0.0;
    // TODO: distance driven = the average of the two sides' meters
    distanceMeters = 0.0;

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
    // Arcade drive, from last lesson.
    double forward = -controller.getLeftY();
    double turn = controller.getRightX();

    // STEP 4: DEADBAND. Real sticks never rest at exactly 0.0, so the robot would creep.
    // TODO: declare a constant  private static final double DEADBAND = 0.1;  near the top of this
    // class. Then use an if statement on each of forward and turn: if the absolute value
    // (Math.abs(...)) is less than DEADBAND, set it to 0.0.

    drive.setDutyCycles(forward + turn, forward - turn);
  }

  @Override
  public void autonomousInit() {
    // Start counting from zero.
    drive.resetEncoders();
  }

  @Override
  public void autonomousPeriodic() {
    // STEP 3: BANG-BANG CONTROL. Drive forward at 0.4 until the robot has gone 2.0 meters, then
    // stop.
    // "Bang-bang" means the motors are either fully on or fully off, nothing in between.
    //
    // TODO: use an if / else with distanceMeters
    drive.setDutyCycles(0.0, 0.0);
  }
}
