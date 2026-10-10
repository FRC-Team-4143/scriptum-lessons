package frc.robot.subsystems.localization;

import com.marswars.subsystem.MwConstants;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

/** Everything that describes how the robot works out where it is, in one place. */
public class LocalizationConstants extends MwConstants {

  // =============================================================================
  // ODOMETRY: how much to trust the wheels
  // =============================================================================

  // Standard deviations are "how far off could this be?": the smaller the number, the more the pose
  // estimator trusts that source. Order: x (meters), y (meters), heading (radians). Provided.
  public static final Matrix<N3, N1> ODOMETRY_STD_DEVS = VecBuilder.fill(0.1, 0.1, 0.1);

  // =============================================================================
  // VISION: how much to trust the cameras (tuned in the Computer Vision lesson)
  // =============================================================================

  // How far off could a camera's position be, in meters, when its tag is 1 meter away? Same idea
  // for the heading, in radians. They are multiplied by the tag's distance squared (a tag 3 meters
  // away is 9 times less sure). The cameras here are good from close up (a few centimeters) but
  // never perfect, and the wheels are good for a short while. So the cameras only nudge the
  // estimate each time, and there are about 60 solutions a second, so it adds up.
  public static final double VISION_XY_STD_METERS = 0.2;
  public static final double VISION_HEADING_STD_RADIANS = 0.2;

  /** The things the localization subsystem can be doing. */
  public enum LocalizationStates {
    /** Combining the wheels and the cameras. */
    ACTIVE
  }
}
