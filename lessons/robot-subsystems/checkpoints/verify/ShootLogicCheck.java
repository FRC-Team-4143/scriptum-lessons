import com.marswars.mechanisms.FlywheelMech;
import com.marswars.mechanisms.MechStub;
import com.marswars.mechanisms.RollerMech;
import frc.robot.OI;
import frc.robot.subsystems.shooter.ShooterConstants;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import java.lang.reflect.Field;

/**
 * Presses and releases the shoot button, runs the student's updateLogic() after each change, and
 * checks what the flywheel was told to do. How the logic is written doesn't matter, only what it
 * does. Prints one line for the student; exits 1 on a failure.
 */
public class ShootLogicCheck {
  static void fail(String message) {
    System.out.println(message);
    System.exit(1);
  }

  static String where(Throwable e) {
    for (StackTraceElement frame : e.getStackTrace()) {
      if ("ShooterSubsystem.java".equals(frame.getFileName())) {
        return " (ShooterSubsystem.java line " + frame.getLineNumber() + ")";
      }
    }
    return "";
  }

  static String num(double value) {
    return Double.isNaN(value) ? "nothing" : String.valueOf(value);
  }

  public static void main(String[] args) throws Exception {
    double shoot_duty = new ShooterConstants().SHOOT_DUTY_CYCLE;

    ShooterSubsystem shooter;
    try {
      shooter = ShooterSubsystem.getInstance();
    } catch (Throwable e) {
      fail("Making the ShooterSubsystem crashed" + where(e) + ": " + e);
      return;
    }
    if (FlywheelMech.made.isEmpty()) {
      fail("The ShooterSubsystem never makes its FlywheelMech. Keep the flywheel_ = new FlywheelMech(...) line in the constructor.");
    }

    // This checkpoint can come before the roller is finished: stand in for any mechanism still null.
    for (Field field : ShooterSubsystem.class.getDeclaredFields()) {
      if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
      field.setAccessible(true);
      if (field.get(shooter) != null) continue;
      if (field.getType() == RollerMech.class) {
        field.set(shooter, new RollerMech("check", java.util.List.of(), 1.0));
      } else if (field.getType() == FlywheelMech.class) {
        field.set(shooter, new FlywheelMech("check", java.util.List.of(), 1.0, 1.0, 1.0));
      }
    }

    // Pressed and released in a pattern a fixed answer can't guess, including holding it for a
    // few loops and starting with it released.
    boolean[] presses = {true, false, true, true, false, false, true, false, true, true, false};
    double time = 0.0;
    for (boolean pressed : presses) {
      OI.shoot = pressed;
      OI.index = false;
      for (FlywheelMech flywheel : FlywheelMech.made) flywheel.forget();
      try {
        shooter.updateLogic(time);
      } catch (Throwable e) {
        fail("updateLogic() crashed" + where(e) + ": " + e);
      }
      time += 0.02;

      MechStub flywheel = null;
      for (FlywheelMech made : FlywheelMech.made) {
        if (made.last_mode != null) flywheel = made;
      }
      String state = pressed ? "held" : "released";
      if (flywheel == null) {
        if (pressed) {
          fail("While the shoot button is held, updateLogic() doesn't tell the flywheel anything. Call flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE) when OI.getShootButton() is true.");
        }
        fail("When the shoot button is released, updateLogic() doesn't tell the flywheel anything, so it keeps spinning. Set its duty cycle to 0.0 when the button isn't held (an else works).");
      }
      if (!flywheel.last_mode.equals("duty")) {
        fail("With the shoot button " + state + ", the flywheel was given a " + flywheel.last_mode + ". This lesson runs it with a duty cycle: use flywheel_.setTargetDutyCycle(...).");
      }
      double want = pressed ? shoot_duty : 0.0;
      if (Math.abs(flywheel.last_value - want) > 1e-9) {
        if (pressed) {
          fail("While the shoot button is held, the flywheel's duty cycle should be CONSTANTS.SHOOT_DUTY_CYCLE (" + shoot_duty + "), but it was " + num(flywheel.last_value) + ".");
        }
        fail("When the shoot button is released, the flywheel should stop (duty cycle 0.0), but it was " + num(flywheel.last_value) + ". Set it to 0.0 when the button isn't held (an else works).");
      }
    }
    System.out.println("The shoot button logic is written.");
  }
}
