package lesson;

import com.marswars.logging.MwLog;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;

/**
 * Provided for you, no need to edit. It judges the "Clear a jam" challenge by watching the robot's
 * state, the shooter's state and the mechanisms, and publishes under "Check/Challenge/":
 *
 * <ul>
 *   <li>Unjam: 1 once one tap of Y, with the flywheel at speed, ran the whole sequence by itself
 *       and no rule was broken since Start.
 *   <li>UnjamStep: how far the latest try got. 0 waiting for Y, 1 the robot is in UNJAM, 2 the
 *       shooter is in SPIN_DOWN, 3 the shooter is in UNJAM, 4 the shooter is back in IDLE, 5 the
 *       robot is back in DRIVE.
 *   <li>UnjamMistakes: how many times the roller ran backward with the flywheel still fast.
 *   <li>UnjamSeconds: how long the roller ran backward in the latest try (-1 until it has).
 * </ul>
 */
public final class UnjamCheck {
  private static final String PREFIX = "/AdvantageKit/RealOutputs/";
  private static final double LOOP_SECONDS = 0.02;
  private static final double FAST_RAD_PER_SEC = 200.0;
  private static final double SAFE_RAD_PER_SEC = 130.0;
  private static final double BACKWARD = -0.1;
  private static final double MIN_UNJAM_SECONDS = 0.4;
  private static final double MAX_UNJAM_SECONDS = 0.7;
  private static final double GIVE_UP_SECONDS = 20.0;
  // A mistake has to last this long, so one loop of logging delay doesn't count.
  private static final double MISTAKE_SECONDS = 0.06;

  private final NetworkTableEntry robot_state = entry("Robot/State");
  private final NetworkTableEntry shooter_state = entry("Subsystem/Shooter/State");
  private final NetworkTableEntry flywheel = entry("Subsystem/Shooter/FlywheelVelocity");
  private final NetworkTableEntry roller = entry("Subsystem/Shooter/Roller/control/duty_cycle_target");

  private boolean y_was_held_ = false;
  private boolean armed_ = false;
  private int step_ = 0;
  private double try_seconds_ = 0.0;
  private double backward_seconds_ = 0.0;
  private double unsafe_seconds_ = 0.0;
  private double unjam_seconds_ = -1.0;
  private int mistakes_ = 0;
  private boolean finished_ = false;

  private static NetworkTableEntry entry(String key) {
    return NetworkTableInstance.getDefault().getEntry(PREFIX + key);
  }

  /** Call once per loop, after your code has logged its values. */
  public void update() {
    boolean y_held = DriverStation.getStickButton(0, XboxController.Button.kY.value);
    String robot = robot_state.getString("");
    String shooter = shooter_state.getString("");
    double velocity = flywheel.getDouble(0.0);
    boolean backward = roller.getDouble(0.0) < BACKWARD;

    // Never run the roller backward into a fast flywheel, at any time.
    boolean unsafe = backward && velocity > SAFE_RAD_PER_SEC;
    if (unsafe && unsafe_seconds_ < MISTAKE_SECONDS && unsafe_seconds_ + LOOP_SECONDS >= MISTAKE_SECONDS) {
      mistakes_++;
    }
    unsafe_seconds_ = unsafe ? unsafe_seconds_ + LOOP_SECONDS : 0.0;

    if (y_held && !y_was_held_ && velocity >= FAST_RAD_PER_SEC) {
      try_seconds_ = 0.0;
      backward_seconds_ = 0.0;
      unjam_seconds_ = -1.0;
      step_ = 0;
      armed_ = true;
    }
    y_was_held_ = y_held;

    if (armed_) {
      try_seconds_ += LOOP_SECONDS;
      if (step_ == 0 && "UNJAM".equals(robot)) {
        step_ = 1;
      }
      if (step_ == 1 && "SPIN_DOWN".equals(shooter)) {
        step_ = 2;
      }
      if (step_ == 2 && "UNJAM".equals(shooter)) {
        step_ = 3;
      }
      if (step_ == 3) {
        if ("UNJAM".equals(shooter)) {
          backward_seconds_ += backward ? LOOP_SECONDS : 0.0;
        } else if ("IDLE".equals(shooter)) {
          unjam_seconds_ = backward_seconds_;
          step_ = 4;
        } else {
          armed_ = false;
        }
      }
      if (step_ == 4 && "DRIVE".equals(robot) && !y_held) {
        step_ = 5;
        armed_ = false;
        finished_ =
            finished_
                || (unjam_seconds_ >= MIN_UNJAM_SECONDS && unjam_seconds_ <= MAX_UNJAM_SECONDS);
      }
      if (try_seconds_ > GIVE_UP_SECONDS) {
        armed_ = false;
      }
    }

    MwLog.log("Check/Challenge/Unjam", finished_ && mistakes_ == 0 ? 1.0 : 0.0);
    MwLog.log("Check/Challenge/UnjamStep", step_);
    MwLog.log("Check/Challenge/UnjamMistakes", mistakes_);
    MwLog.log("Check/Challenge/UnjamSeconds", unjam_seconds_);
  }
}
