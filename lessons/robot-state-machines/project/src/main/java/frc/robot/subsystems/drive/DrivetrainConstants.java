package frc.robot.subsystems.drive;

import com.ctre.phoenix6.signals.InvertedValue;
import com.marswars.mechanisms.MotorConfig;
import com.marswars.mechanisms.MotorConfig.TalonMotorType;
import com.marswars.subsystem.MwConstants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import java.util.List;

/** Everything that describes the drivetrain subsystem, in one place. */
public class DrivetrainConstants extends MwConstants {

  // =============================================================================
  // CHASSIS: the numbers that describe the drivetrain
  // =============================================================================

  // The wheel radius and track width are the real robot's. The gear ratio and mass are still
  // placeholders until they are measured.
  public static final double WHEEL_RADIUS_METERS = Units.inchesToMeters(3.0);
  public static final double GEAR_RATIO = 8.45; // motor turns for every 1 wheel turn
  public static final double TRACK_WIDTH_METERS =
      Units.inchesToMeters(24.0); // left to right wheels
  public static final double ROBOT_MASS_KG = 50.0;

  // Each side of the drivetrain has one brushless Kraken X60. A side is a List of motors, so a
  // robot with more motors per side could simply add to its list.
  public static final List<MotorConfig> LEFT_MOTORS = List.of(motor(1, false));
  public static final List<MotorConfig> RIGHT_MOTORS = List.of(motor(2, true));

  /** Builds the settings for one drive motor: its CAN id and whether it spins backwards. */
  private static MotorConfig motor(int can_id, boolean inverted) {
    MotorConfig config = new MotorConfig();
    config.can_id = can_id;
    config.motor_type = TalonMotorType.X60;
    config.getAsFXConfig().MotorOutput.Inverted =
        inverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;
    return config;
  }

  // =============================================================================
  // AIMING: turn to face the goal
  // =============================================================================

  // Where the goal is on the field, in meters. The robot starts the simulation at (0, 0) facing
  // along +x (heading 0), so this goal begins up and to the left of it. Only the position matters;
  // the heading of this Pose2d is not used.
  public static final Pose2d GOAL = new Pose2d(2.0, 3.0, new Rotation2d());

  // The PID gains for aiming. The error is in RADIANS and the output is a turn command from
  // -1.0 to 1.0, so kP = 1.0 means "turn at full power per radian of error" (about 57 degrees).
  // These starter values do not aim well. Tuning them is your job.
  public static final double AIM_KP = 0.2;
  public static final double AIM_KI = 0.0;
  public static final double AIM_KD = 0.0;

  // Close enough to count as aimed, in degrees.
  public static final double AIM_TOLERANCE_DEGREES = 2.0;

  /** The things the drivetrain can be doing. */
  public enum DriveStates {
    /** Not moving. */
    IDLE,
    /** Driving from the controller sticks. */
    ARCADE
    // TODO (aim): add a third state, AIM: turn in place to face the goal. Don't forget the comma
    // after ARCADE.
  }
}
