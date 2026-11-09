package lesson;

import com.marswars.logging.MwLog;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Provided for you, no need to edit. It judges the "Two-cycle auto" challenge using the simulator's
 * true position, and publishes under "Check/Challenge/":
 *
 * <ul>
 *   <li>TwoCycle: 1 once one Auto run did the whole list below within 25 seconds.
 *   <li>TwoCycleStep: how far the latest run got. 1 pickup, 2 scoring spot, 3 first aimed shot, 4
 *       pickup again, 5 scoring spot again, 6 second aimed shot.
 *   <li>TwoCycleSeconds: when the latest run finished its second shot (-1 until one has).
 * </ul>
 */
public final class TwoCycleCheck {
  private static final String PREFIX = "/AdvantageKit/RealOutputs/";
  private static final double LOOP_SECONDS = 0.02;
  private static final double CLOSE_ENOUGH_METERS = 0.4;
  private static final double AIMED_RAD = 0.087; // about 5 degrees
  // A real match gives 15 seconds. The practice robot settles fully at the end of every path, about
  // 2 seconds each, so the practice limit is longer.
  private static final double LIMIT_SECONDS = 25.0;

  private final NetworkTableEntry to_pickup = entry("Drive/DistanceToPickup");
  private final NetworkTableEntry to_score = entry("Drive/DistanceToScoreSpot");
  private final NetworkTableEntry shots = entry("Subsystem/Shooter/ShotCount");
  private final NetworkTableEntry aim_error = entry("Subsystem/Shooter/AimErrorAtShotRad");

  private boolean was_auto_ = false;
  private int step_ = 0;
  private long shots_seen_ = 0;
  private double run_seconds_ = 0.0;
  private double finished_seconds_ = -1.0;
  private boolean passed_ = false;

  private static NetworkTableEntry entry(String key) {
    return NetworkTableInstance.getDefault().getEntry(PREFIX + key);
  }

  /** Call once per loop, after your code has logged its values. */
  public void update() {
    boolean auto = DriverStation.isAutonomousEnabled();
    if (auto && !was_auto_) {
      step_ = 0;
      shots_seen_ = 0;
      run_seconds_ = 0.0;
      finished_seconds_ = -1.0;
    }
    was_auto_ = auto;

    if (auto) {
      run_seconds_ += LOOP_SECONDS;
      boolean at_pickup = to_pickup.getDouble(99.0) <= CLOSE_ENOUGH_METERS;
      boolean at_score = to_score.getDouble(99.0) <= CLOSE_ENOUGH_METERS;
      long shot_count = shots.getInteger(0);
      boolean new_shot = shot_count > shots_seen_;
      boolean aimed = aim_error.getDouble(99.0) <= AIMED_RAD;
      shots_seen_ = Math.max(shots_seen_, shot_count);

      if ((step_ == 0 || step_ == 3) && at_pickup) {
        step_++;
      } else if ((step_ == 1 || step_ == 4) && at_score) {
        step_++;
      } else if ((step_ == 2 || step_ == 5) && new_shot && aimed) {
        step_++;
        if (step_ == 6) {
          finished_seconds_ = run_seconds_;
          passed_ = passed_ || run_seconds_ <= LIMIT_SECONDS;
        }
      }
    }
    MwLog.log("Check/Challenge/TwoCycle", passed_ ? 1.0 : 0.0);
    MwLog.log("Check/Challenge/TwoCycleStep", step_);
    MwLog.log("Check/Challenge/TwoCycleSeconds", finished_seconds_);
  }
}
