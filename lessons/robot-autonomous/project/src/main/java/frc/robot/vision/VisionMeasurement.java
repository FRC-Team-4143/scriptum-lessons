package frc.robot.vision;

import edu.wpi.first.math.geometry.Pose2d;

/**
 * One camera picture, boiled down to what the pose estimator needs. Provided, no need to edit.
 *
 * <p>The pose is where the camera says the ROBOT was on the field, the timestamp is when the
 * picture was taken (in seconds, on the same clock as the rest of the robot code), and the tag
 * count is how many AprilTags the camera could see in that picture.
 */
public final class VisionMeasurement {
  private final Pose2d pose_;
  private final double timestamp_seconds_;
  private final int tag_count_;
  private final String camera_;

  public VisionMeasurement(Pose2d pose, double timestamp_seconds, int tag_count, String camera) {
    pose_ = pose;
    timestamp_seconds_ = timestamp_seconds;
    tag_count_ = tag_count;
    camera_ = camera;
  }

  /** Where the camera says the robot was, on the field. */
  public Pose2d getPose() {
    return pose_;
  }

  /** When the picture was taken, in seconds. Pass this to addVisionMeasurement. */
  public double getTimestamp() {
    return timestamp_seconds_;
  }

  /** How many AprilTags the camera saw in the picture. */
  public int getTagCount() {
    return tag_count_;
  }

  /** The name of the camera that took the picture. */
  public String getCamera() {
    return camera_;
  }
}
