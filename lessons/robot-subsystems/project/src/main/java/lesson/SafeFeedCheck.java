package lesson;

import com.marswars.logging.MwLog;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;

/**
 * Provided for you, no need to edit. It judges the "Intake and safe feed" challenge by watching the
 * buttons and what the mechanisms logged, and publishes under "Check/Challenge/":
 *
 * <ul>
 *   <li>SafeFeed: 1 once all four rules have each been seen working for a moment, with no mistakes
 *       since Start.
 *   <li>SafeFeedSeen: how many of the four rules have been seen working.
 *   <li>SafeFeedMistakes: how many times a rule was broken since Start.
 * </ul>
 */
public final class SafeFeedCheck {
  private static final String PREFIX = "/AdvantageKit/RealOutputs/";
  private static final double LOOP_SECONDS = 0.02;
  private static final double HOLD_SECONDS = 0.3;
  // A mistake has to last this long, so one loop of delay between two subsystems doesn't count.
  private static final double MISTAKE_SECONDS = 0.06;
  private static final double RUNNING = 0.05;
  private static final double RUNNING_FAST = 0.3;
  private static final double TOO_SLOW_RAD_PER_SEC = 200.0;
  private static final double FAST_RAD_PER_SEC = 260.0;

  private final NetworkTableEntry roller = entry("Subsystem/Shooter/RollerDuty");
  private final NetworkTableEntry flywheel = entry("Subsystem/Shooter/FlywheelVelocity");
  private final NetworkTableEntry intake = entry("Subsystem/Intake/RollerDuty");

  private final double[] seen_seconds_ = new double[4];
  private final boolean[] seen_ = new boolean[4];
  private double slow_feed_seconds_ = 0.0;
  private double both_seconds_ = 0.0;
  private double stray_intake_seconds_ = 0.0;
  private int mistakes_ = 0;

  private static NetworkTableEntry entry(String key) {
    return NetworkTableInstance.getDefault().getEntry(PREFIX + key);
  }

  private void watch(int rule, boolean working) {
    seen_seconds_[rule] = working ? seen_seconds_[rule] + LOOP_SECONDS : 0.0;
    if (seen_seconds_[rule] >= HOLD_SECONDS) {
      seen_[rule] = true;
    }
  }

  /** Returns how long the mistake has lasted, and counts it the moment it has lasted long enough. */
  private double mistake(boolean happening, double seconds) {
    double next = happening ? seconds + LOOP_SECONDS : 0.0;
    if (seconds < MISTAKE_SECONDS && next >= MISTAKE_SECONDS) {
      mistakes_++;
    }
    return next;
  }

  /** Call once per loop, after your code has logged its values. */
  public void update() {
    boolean enabled = DriverStation.isEnabled();
    boolean index_held = DriverStation.getStickButton(0, XboxController.Button.kLeftBumper.value);
    boolean intake_held = DriverStation.getStickButton(0, XboxController.Button.kB.value);
    double roller_duty = roller.getDouble(0.0);
    double velocity = flywheel.getDouble(0.0);
    double intake_duty = intake.getDouble(0.0);
    boolean feeding = roller_duty > RUNNING;
    boolean intaking = intake_duty > RUNNING;

    if (enabled) {
      // The four rules.
      watch(0, index_held && velocity < TOO_SLOW_RAD_PER_SEC && !feeding);
      watch(1, index_held && velocity >= FAST_RAD_PER_SEC && roller_duty >= RUNNING_FAST);
      watch(2, intake_held && !feeding && intake_duty >= RUNNING_FAST);
      watch(3, intake_held && feeding && !intaking);
      // The mistakes.
      slow_feed_seconds_ = mistake(feeding && velocity < TOO_SLOW_RAD_PER_SEC, slow_feed_seconds_);
      both_seconds_ = mistake(feeding && intaking, both_seconds_);
      stray_intake_seconds_ = mistake(intaking && !intake_held, stray_intake_seconds_);
    }

    int seen = 0;
    for (boolean rule : seen_) {
      seen += rule ? 1 : 0;
    }
    MwLog.log("Check/Challenge/SafeFeed", seen == seen_.length && mistakes_ == 0 ? 1.0 : 0.0);
    MwLog.log("Check/Challenge/SafeFeedSeen", seen);
    MwLog.log("Check/Challenge/SafeFeedMistakes", mistakes_);
  }
}
