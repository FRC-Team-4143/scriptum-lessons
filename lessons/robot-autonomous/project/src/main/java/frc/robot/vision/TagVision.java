package frc.robot.vision;

import edu.wpi.first.math.geometry.Pose2d;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects the poses the cameras work out from AprilTags and hands them to localization as clean
 * {@link VisionMeasurement}s. Provided for you, no need to edit.
 *
 * <p>It does two jobs so your code does not have to: it throws away pictures that cannot be right
 * (too few tags to trust, the pose is off the field, or the robot was spinning too fast for a sharp
 * picture), and it hands every picture over exactly once. In simulation the SimulationSubsystem
 * feeds it; on a real robot the camera coprocessor would.
 */
public final class TagVision {
  private static TagVision instance_ = null;

  public static TagVision getInstance() {
    if (instance_ == null) {
      instance_ = new TagVision();
    }
    return instance_;
  }

  private TagVision() {}

  private final List<VisionMeasurement> pending_ = new ArrayList<>();
  private int dropped_count_ = 0;
  private double ignore_before_seconds_ = 0.0;

  /** Called by whoever produces camera data (the simulator). */
  public void addMeasurement(VisionMeasurement measurement) {
    pending_.add(measurement);
  }

  /**
   * Forgets every picture taken before this time. Called when the robot is moved to a new pose (a
   * reset): pictures still on their way were taken at the OLD spot and would pull the estimate back
   * there.
   */
  public void ignorePicturesBefore(double timestamp_seconds) {
    ignore_before_seconds_ = timestamp_seconds;
    pending_.clear();
  }

  /**
   * The new, sensible measurements since the last time this was called, oldest first.
   *
   * @param yaw_rate how fast the robot is turning right now, in radians per second (positive or
   *     negative)
   */
  public List<VisionMeasurement> takeMeasurements(double yaw_rate) {
    List<VisionMeasurement> good = new ArrayList<>();
    boolean spinning_too_fast =
        Math.abs(yaw_rate) > VisionConstants.MAX_YAW_RATE_RADIANS_PER_SECOND;
    for (VisionMeasurement measurement : pending_) {
      if (!spinning_too_fast
          && measurement.getTimestamp() >= ignore_before_seconds_
          && measurement.getTagCount() >= VisionConstants.MIN_TAG_COUNT
          && isOnField(measurement.getPose())) {
        good.add(measurement);
      } else {
        dropped_count_++;
      }
    }
    pending_.clear();
    return good;
  }

  /** How many measurements have been thrown away so far. */
  public int getDroppedCount() {
    return dropped_count_;
  }

  /** True if the pose is inside the walls of the field. */
  private static boolean isOnField(Pose2d pose) {
    return pose.getX() >= 0.0
        && pose.getX() <= VisionConstants.FIELD_LAYOUT.getFieldLength()
        && pose.getY() >= 0.0
        && pose.getY() <= VisionConstants.FIELD_LAYOUT.getFieldWidth();
  }
}
