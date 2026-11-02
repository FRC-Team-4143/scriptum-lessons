package lesson;

import com.marswars.logging.MwLog;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructSubscriber;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Provided for you, no need to edit. It judges the "Waypoint tour" challenge by watching where the
 * simulated robot REALLY is (Drive/TruePose) during each Auto run, and publishes under
 * "Check/Challenge/":
 *
 * <ul>
 *   <li>WaypointTour: 1 once a run reached the first waypoint, then the second, then stopped there,
 *       all in time.
 *   <li>WaypointsReached: how many waypoints the latest run has reached, in order.
 * </ul>
 */
public final class WaypointTourCheck {
  private static final double LOOP_SECONDS = 0.02;
  private static final Translation2d[] WAYPOINTS = {
    new Translation2d(2.0, 1.0), new Translation2d(3.5, -1.0)
  };
  private static final double CLOSE_ENOUGH_METERS = 0.3;
  private static final double MAX_SECONDS = 10.0;
  private static final double REST_SECONDS = 0.5;
  private static final double REST_METERS_PER_SECOND = 0.02;

  private final StructSubscriber<Pose2d> truth =
      NetworkTableInstance.getDefault()
          .getStructTopic("/AdvantageKit/RealOutputs/Drive/TruePose", Pose2d.struct)
          .subscribe(new Pose2d());

  private boolean was_auto_ = false;
  private Pose2d last_ = new Pose2d();
  private double run_seconds_ = 0.0;
  private double rest_seconds_ = 0.0;
  private int reached_ = 0;
  private boolean passed_ = false;

  /** Call once per loop. */
  public void update() {
    boolean auto = DriverStation.isAutonomousEnabled();
    Pose2d pose = truth.get();
    if (auto && !was_auto_) {
      run_seconds_ = 0.0;
      rest_seconds_ = 0.0;
      reached_ = 0;
    }
    if (auto) {
      run_seconds_ += LOOP_SECONDS;
      Translation2d here = pose.getTranslation();
      if (reached_ < WAYPOINTS.length
          && here.getDistance(WAYPOINTS[reached_]) <= CLOSE_ENOUGH_METERS) {
        reached_++;
      }
      double speed = here.getDistance(last_.getTranslation()) / LOOP_SECONDS;
      rest_seconds_ = speed < REST_METERS_PER_SECOND ? rest_seconds_ + LOOP_SECONDS : 0.0;
      if (reached_ == WAYPOINTS.length
          && rest_seconds_ >= REST_SECONDS
          && here.getDistance(WAYPOINTS[WAYPOINTS.length - 1]) <= CLOSE_ENOUGH_METERS
          && run_seconds_ <= MAX_SECONDS) {
        passed_ = true;
      }
    }
    last_ = pose;
    was_auto_ = auto;
    MwLog.log("Check/Challenge/WaypointTour", passed_ ? 1.0 : 0.0);
    MwLog.log("Check/Challenge/WaypointsReached", reached_);
  }
}
