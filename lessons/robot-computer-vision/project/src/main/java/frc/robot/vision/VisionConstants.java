package frc.robot.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;

/**
 * Everything that describes the cameras and the AprilTags they look at. Provided, no need to edit.
 */
public final class VisionConstants {
  private VisionConstants() {}

  /**
   * Where every AprilTag is on the 2026 field, from FIRST. The origin is the blue alliance corner,
   * so the same blue-side coordinates the rest of the lessons use work as they are.
   */
  public static final AprilTagFieldLayout FIELD_LAYOUT =
      AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltWelded);

  // Where each simulated camera is bolted to the robot: x forward, y left, z up (meters), and the
  // rotation of the camera (roll, pitch, yaw in radians; a positive pitch tilts the camera DOWN).
  public static final String FRONT_CAMERA_NAME = "front";
  public static final Transform3d FRONT_CAMERA_TRANSFORM =
      new Transform3d(new Translation3d(0.3, 0.0, 0.3), new Rotation3d(0.0, 0.0, 0.0));

  public static final String BACK_CAMERA_NAME = "back";
  public static final Transform3d BACK_CAMERA_TRANSFORM =
      new Transform3d(new Translation3d(-0.3, 0.0, 0.3), new Rotation3d(0.0, 0.0, Math.PI));

  // A camera picture taken while the robot spins fast is blurred, so the pose it gives is thrown
  // away above this turning speed (radians per second).
  public static final double MAX_YAW_RATE_RADIANS_PER_SECOND = Math.toRadians(180.0);

  // The simulated cameras work out each pose from one tag only, the best one, and the farther that
  // tag is the worse the pose gets: within about 3 meters it is a few centimeters off, but now and
  // then, mostly beyond 3.5 meters, a pose is wildly wrong (up to 6 meters). A pose farther than
  // this
  // from the current estimate is thrown away (meters). The wheels drift much less than this.
  public static final double MAX_JUMP_METERS = 1.0;

  // The tags a camera used in its latest picture stay in the VisibleTags log for this long
  // (seconds). A bit more than the gap between two pictures (about 0.033 s at 30 frames per
  // second).
  public static final double VISIBLE_TAGS_SECONDS = 0.2;
}
