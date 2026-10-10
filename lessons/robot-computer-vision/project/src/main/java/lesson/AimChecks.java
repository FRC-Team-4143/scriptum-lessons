package lesson;

import com.marswars.logging.MwLog;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.OI;

/**
 * Provided for you, no need to edit. While the aim button is held it watches how the simulated
 * robot's REAL heading error (Drive/TrueAimErrorDegrees) behaves and publishes what the aim
 * checkpoints need under "Check/Aim/":
 *
 * <ul>
 *   <li>Settled: 1 once the robot got within 2 degrees of the goal and stayed there for half a
 *       second, within a few seconds of the button going down.
 *   <li>OvershootDegrees: the farthest the robot swung PAST the goal, in degrees (-1 until the aim
 *       button has been used).
 * </ul>
 */
public final class AimChecks {
  private static final String PREFIX = "/AdvantageKit/RealOutputs/";
  private static final double LOOP_SECONDS = 0.02;
  private static final double CLOSE_ENOUGH_DEGREES = 2.0;
  private static final double STAY_SECONDS = 0.5;
  private static final double MUST_SETTLE_WITHIN_SECONDS = 4.0;
  // An aim that starts nearly aimed proves nothing.
  private static final double MIN_START_ERROR_DEGREES = 15.0;

  private final NetworkTableEntry error =
      NetworkTableInstance.getDefault().getEntry(PREFIX + "Drive/TrueAimErrorDegrees");

  private boolean wasHeld = false;
  private double startSign = 0.0;
  private boolean bigStart = false;
  private double heldSeconds = 0.0;
  private double closeSeconds = 0.0;
  private boolean settled = false;
  private double overshoot = -1.0;

  /** Call once per loop, after your code has logged its values. */
  public void update() {
    boolean held = OI.getAimButton().getAsBoolean();
    if (error.exists()) {
      double degrees = error.getDouble(0.0);
      if (held && !wasHeld) {
        startSign = Math.signum(degrees);
        bigStart = Math.abs(degrees) >= MIN_START_ERROR_DEGREES;
        heldSeconds = 0.0;
        closeSeconds = 0.0;
        overshoot = Math.max(overshoot, 0.0);
      }
      if (held) {
        heldSeconds += LOOP_SECONDS;
        // Past the goal means the error changed sign since the button went down.
        overshoot = Math.max(overshoot, -startSign * degrees);
        closeSeconds = Math.abs(degrees) < CLOSE_ENOUGH_DEGREES ? closeSeconds + LOOP_SECONDS : 0.0;
        if (bigStart && closeSeconds >= STAY_SECONDS && heldSeconds <= MUST_SETTLE_WITHIN_SECONDS) {
          settled = true;
        }
      }
    }
    wasHeld = held;
    MwLog.log("Check/Aim/Settled", settled ? 1.0 : 0.0);
    MwLog.log("Check/Aim/OvershootDegrees", overshoot);
  }
}
