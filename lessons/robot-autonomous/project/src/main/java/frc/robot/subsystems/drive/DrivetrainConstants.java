package frc.robot.subsystems.drive;

import com.ctre.phoenix6.signals.InvertedValue;
import com.marswars.mechanisms.MotorConfig;
import com.marswars.mechanisms.MotorConfig.TalonMotorType;
import com.marswars.subsystem.MwConstants;
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
  private static MotorConfig motor(int canId, boolean inverted) {
    MotorConfig config = new MotorConfig();
    config.can_id = canId;
    config.motor_type = TalonMotorType.X60;
    config.getAsFXConfig().MotorOutput.Inverted =
        inverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;
    return config;
  }

  /** The things the drivetrain can be doing. */
  public enum DriveStates {
    /** Not moving. */
    IDLE,
    /** Driving from the controller sticks. */
    ARCADE,
    /** Driving whatever speeds the autonomous commands ask for. */
    COMMANDED
  }

  // =============================================================================
  // AUTONOMOUS DRIVING
  // =============================================================================

  /** Fastest the robot drives on its own, as a duty cycle from 0.0 to 1.0. */
  public final double MAX_AUTO_SPEED = 0.5;
  /** Slowest it creeps along, so the last few centimeters do not take forever. */
  public final double MIN_AUTO_SPEED = 0.08;
  /** Driving: duty cycle per meter of distance left. */
  public final double DRIVE_KP = 0.6;
  /** Driving counts as finished when this close to the goal, in meters. */
  public final double DRIVE_TOLERANCE_METERS = 0.05;

  /** Fastest the robot turns on its own, as a duty cycle. Turning is much more sensitive. */
  public final double MAX_TURN_SPEED = 0.2;
  /** Slowest it creeps while turning. */
  public final double MIN_TURN_SPEED = 0.04;
  /** Turning: duty cycle per radian of heading error. */
  public final double TURN_KP = 0.3;
  /** Turning counts as finished when this close to the goal, in degrees. */
  public final double TURN_TOLERANCE_DEGREES = 2.0;
}
