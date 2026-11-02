import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Robot;
import frc.robot.mechanisms.DifferentialDriveMech;

/** Runs the student's teleopPeriodic() with exact stick positions and reads what it sent. */
public class DriverSticksCheck {
  static Robot robot;

  public static void main(String[] args) {
    robot = new Robot();
    expect("the sticks centered", 0.0, 0.0, 0.0, 0.0);
    // Squared, keeping the sign.
    expect("forward 0.5", 0.5, 0.0, 0.25, 0.25);
    expect("backward 0.5", -0.5, 0.0, -0.25, -0.25);
    expect("turn right 0.5", 0.0, 0.5, 0.25, -0.25);
    expect("turn left 0.5", 0.0, -0.5, -0.25, 0.25);
    expect("forward 0.3 and turn right 0.2", 0.3, 0.2, 0.13, 0.05);
    expect("full forward", 1.0, 0.0, 1.0, 1.0);
    // Past full power: both sides shrink together, so the robot still turns.
    expect("full forward and full right", 1.0, 1.0, 1.0, 0.0);
    expect("full forward and half right", 1.0, 0.5, 1.0, 0.6);
    expect("full backward and full right", -1.0, 1.0, 0.0, -1.0);
    expect("full backward and half left", -1.0, -0.5, -1.0, -0.6);
    expect("forward 0.8 and right 0.8", 0.8, 0.8, 1.0, 0.0);
    System.out.println("Driver-friendly sticks work: gentle in the middle, and it can always turn.");
  }

  static void expect(String stick, double forward, double turn, double left, double right) {
    XboxController.left_y = -forward;
    XboxController.left_x = turn;
    DifferentialDriveMech.left = Double.NaN;
    DifferentialDriveMech.right = Double.NaN;
    robot.teleopPeriodic();
    double got_left = DifferentialDriveMech.left;
    double got_right = DifferentialDriveMech.right;
    if (Double.isNaN(got_left) || Double.isNaN(got_right)) {
      fail("With " + stick + ", teleopPeriodic() never called drive_.setDutyCycles(...).");
    }
    if (Math.abs(got_left - left) > 0.001 || Math.abs(got_right - right) > 0.001) {
      String hint =
          Math.abs(forward) + Math.abs(turn) > 1.0
              ? " Past full power, divide both sides by the same number."
              : " Remember to square each stick and keep its sign.";
      fail(
          "With "
              + stick
              + " you sent left "
              + got_left
              + ", right "
              + got_right
              + ". Expected left "
              + left
              + ", right "
              + right
              + "."
              + hint);
    }
  }

  static void fail(String message) {
    System.out.println(message);
    System.exit(1);
  }
}
