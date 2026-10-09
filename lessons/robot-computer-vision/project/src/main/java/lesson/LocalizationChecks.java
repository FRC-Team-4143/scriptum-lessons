package lesson;

import com.marswars.logging.MwLog;
import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.subsystems.drive.DrivetrainSubsystem;

/**
 * Provided for you, no need to edit. It checks how good your pose estimate is by comparing it with
 * where the simulated robot REALLY is, but only at a fair moment: after the robot has driven at
 * least 5 meters in total and then sat still for 1.5 seconds. (While it moves, even a good estimate
 * is a little behind. At rest it should have settled on the truth.) It publishes under
 * "Check/Localization/":
 *
 * <ul>
 *   <li>Accurate: 1 if at the latest of those moments the estimate was within 5 centimeters and 1
 *       degree of the truth, otherwise 0 (and 0 until the first such moment has happened).
 *   <li>PositionErrorMeters and HeadingErrorDegrees: the two errors measured at that moment (-1
 *       until it has happened).
 * </ul>
 */
public final class LocalizationChecks {
  private static final double LOOP_SECONDS = 0.02;
  private static final double MIN_DRIVEN_METERS = 5.0;
  private static final double MIN_REST_SECONDS = 1.5;
  // Moving slower than this (per loop) counts as sitting still: 2 cm/s and 0.02 rad/s.
  private static final double REST_METERS_PER_LOOP = 0.02 * LOOP_SECONDS;
  private static final double REST_RADIANS_PER_LOOP = 0.02 * LOOP_SECONDS;
  private static final double MAX_POSITION_ERROR_METERS = 0.05;
  private static final double MAX_HEADING_ERROR_DEGREES = 1.0;

  private Pose2d last_true_ = null;
  private double driven_meters_ = 0.0;
  private double rest_seconds_ = 0.0;
  private boolean evaluated_this_rest_ = false;
  private double position_error_ = -1.0;
  private double heading_error_ = -1.0;
  private boolean accurate_ = false;

  /** Call once per loop, after your code has logged its values. */
  public void update() {
    DrivetrainSubsystem drive = DrivetrainSubsystem.getInstance();
    Pose2d truth = drive.getTruePose();
    if (last_true_ != null) {
      double moved = truth.getTranslation().getDistance(last_true_.getTranslation());
      double turned = Math.abs(truth.getRotation().minus(last_true_.getRotation()).getRadians());
      driven_meters_ += moved;
      if (moved < REST_METERS_PER_LOOP && turned < REST_RADIANS_PER_LOOP) {
        rest_seconds_ += LOOP_SECONDS;
      } else {
        rest_seconds_ = 0.0;
        evaluated_this_rest_ = false;
      }
      if (driven_meters_ >= MIN_DRIVEN_METERS
          && rest_seconds_ >= MIN_REST_SECONDS
          && !evaluated_this_rest_) {
        Pose2d estimate = drive.getPose();
        position_error_ = truth.getTranslation().getDistance(estimate.getTranslation());
        heading_error_ = Math.abs(truth.getRotation().minus(estimate.getRotation()).getDegrees());
        accurate_ =
            position_error_ <= MAX_POSITION_ERROR_METERS
                && heading_error_ <= MAX_HEADING_ERROR_DEGREES;
        evaluated_this_rest_ = true;
      }
    }
    last_true_ = truth;
    MwLog.log("Check/Localization/Accurate", accurate_ ? 1.0 : 0.0);
    MwLog.log("Check/Localization/PositionErrorMeters", position_error_);
    MwLog.log("Check/Localization/HeadingErrorDegrees", heading_error_);
  }
}
