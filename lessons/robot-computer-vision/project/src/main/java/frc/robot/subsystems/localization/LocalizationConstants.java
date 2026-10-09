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
  // VISION: how much to trust the cameras (your job)
  // =============================================================================

  // How far off could a camera's position be, in meters? Same idea for the heading, in radians.
  // These starter numbers say "cameras are very unreliable", so they barely help. Choose better
  // ones once you have looked at how far off the camera poses really are.
  public static final double VISION_XY_STD_METERS = 5.0;
  public static final double VISION_HEADING_STD_RADIANS = 5.0;

  /** The things the localization subsystem can be doing. */
  public enum LocalizationStates {
    /** Combining the wheels and the cameras. */
    ACTIVE
  }
}
