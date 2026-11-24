import edu.wpi.first.wpilibj.XboxController;
import frc.robot.OI;

/** Presses each bumper on a stand-in controller and checks what the student's OI reports. */
public class OiButtonsCheck {
  static void fail(String message) {
    System.out.println(message);
    System.exit(1);
  }

  static void press(boolean left, boolean right) {
    XboxController.left_bumper = left;
    XboxController.right_bumper = right;
  }

  public static void main(String[] args) {
    boolean[][] cases = {{false, false}, {false, true}, {true, false}, {true, true}, {false, false}};
    for (boolean[] c : cases) {
      press(c[0], c[1]);
      boolean shoot;
      boolean index;
      try {
        shoot = OI.getShootButton();
        index = OI.getIndexButton();
      } catch (Throwable e) {
        fail("OI crashed when a button was read: " + e);
        return;
      }
      if (shoot != c[1]) {
        fail(c[1]
            ? "getShootButton() should be true while the right bumper is held: return driver_controller_.getRightBumperButton()."
            : "getShootButton() should be false when the right bumper isn't held. Return driver_controller_.getRightBumperButton().");
      }
      if (index != c[0]) {
        fail(c[0]
            ? "getIndexButton() should be true while the left bumper is held: return driver_controller_.getLeftBumperButton()."
            : "getIndexButton() should be false when the left bumper isn't held. Return driver_controller_.getLeftBumperButton().");
      }
    }
    System.out.println("Both shooter buttons are wired up.");
  }
}
