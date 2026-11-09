package lesson;

import com.marswars.logging.MwLog;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructSubscriber;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Provided for you, no need to edit. It judges the "Stop on the mark" challenge by watching where
 * the simulated robot REALLY is (Drive/TruePose) during each Auto run, and publishes under
 * "Check/Challenge/":
 *
 * <ul>
 *   <li>StopOnMark: 1 once a run stopped within 3 centimeters of 2 meters, in time.
 *   <li>StopErrorMeters: how far from 2 meters the latest run stopped (-1 until a run has stopped).
 *   <li>StopSeconds: how long the latest run took to get there (-1 until a run has stopped).
 * </ul>
 */
public final class StopOnMarkCheck {
  private static final double LOOP_SECONDS = 0.02;
  private static final double TARGET_METERS = 2.0;
  private static final double TOLERANCE_METERS = 0.03;
  private static final double MAX_SECONDS = 2.5;
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
  private boolean judged_ = false;
  private boolean passed_ = false;
  private double error_ = -1.0;
  private double seconds_ = -1.0;

  /** Call once per loop. */
  public void update() {
    boolean auto = DriverStation.isAutonomousEnabled();
    Pose2d pose = truth.get();
    if (auto && !was_auto_) {
      run_seconds_ = 0.0;
      rest_seconds_ = 0.0;
      judged_ = false;
    }
    if (auto) {
      run_seconds_ += LOOP_SECONDS;
      double speed = pose.getTranslation().getDistance(last_.getTranslation()) / LOOP_SECONDS;
      double driven = pose.getTranslation().getNorm();
      rest_seconds_ = driven > 1.0 && speed < REST_METERS_PER_SECOND ? rest_seconds_ + LOOP_SECONDS : 0.0;
      if (rest_seconds_ >= REST_SECONDS && !judged_) {
        judged_ = true;
        error_ = Math.abs(driven - TARGET_METERS);
        seconds_ = run_seconds_ - REST_SECONDS;
        passed_ = passed_ || (error_ <= TOLERANCE_METERS && seconds_ <= MAX_SECONDS);
      }
    }
    last_ = pose;
    was_auto_ = auto;
    MwLog.log("Check/Challenge/StopOnMark", passed_ ? 1.0 : 0.0);
    MwLog.log("Check/Challenge/StopErrorMeters", error_);
    MwLog.log("Check/Challenge/StopSeconds", seconds_);
  }
}
