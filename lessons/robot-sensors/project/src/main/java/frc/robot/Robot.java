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

    // STEP 1: the drivetrain reports wheel ROTATIONS. Change the four readings into meters and
    // meters per second. (One rotation moves the robot one wheel circumference.)
    double leftMeters = 0.0;
    double rightMeters = 0.0;
    double leftMetersPerSecond = 0.0;
    double rightMetersPerSecond = 0.0;

    // STEP 2: describe the whole robot, not just each side. The README has the formulas.
    double linearSpeed = 0.0; // meters per second
    double angularSpeed = 0.0; // radians per second
    distanceMeters = 0.0; // meters

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
    // Ignore the tiny values on both sticks (the README says how).

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
    // STEP 3: BANG-BANG CONTROL. Drive forward until the robot has gone 2.0 meters, then stop.
    // The motors are either on or off, nothing in between.
    drive.setDutyCycles(0.0, 0.0);
  }
}
