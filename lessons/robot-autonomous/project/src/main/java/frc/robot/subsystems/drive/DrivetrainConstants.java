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
    COMMANDED,
    /**
     * Following a Choreo path: the robot drives the speeds the path says, corrected by where it is.
     */
    CHOREO_PATH
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

  // =============================================================================
  // CHOREO PATH FOLLOWING (pretuned, provided: you read it, you do not tune it)
  // =============================================================================

  /**
   * Fastest the wheels can spin with nothing to push against: a Kraken X60 at 6000 RPM through the
   * gearbox, in meters per second. Full duty cycle (1.0) would be this fast.
   */
  public static final double FREE_SPEED_METERS_PER_SECOND =
      Units.rotationsPerMinuteToRadiansPerSecond(6000.0) / GEAR_RATIO * WHEEL_RADIUS_METERS;

  /**
   * Feedforward: duty cycle for every meter per second of wheel speed we want. The simplest honest
   * model of a motor is "twice the voltage, twice the speed", so this is 1 divided by the top
   * speed.
   */
  public final double CHOREO_KV = 1.0 / FREE_SPEED_METERS_PER_SECOND;

  /**
   * Feedforward: the first bit of duty cycle only breaks the wheels loose (static friction), so it
   * is added on top in the direction we want to move. 0.25 V out of a 12 V battery.
   */
  public final double CHOREO_KS = 0.25 / 12.0;

  /**
   * Feedforward for turning: spinning drags the wheels sideways across the carpet, so on top of the
   * power it takes to roll, it takes extra to turn. This is duty cycle for every meter per second
   * of difference between a side's speed and the middle of the robot's, added to the faster side
   * and taken from the slower one. (On a real robot you would measure this with a test drive; here
   * it comes from 4.5 V per meter per second out of 12 V.)
   */
  public final double CHOREO_KV_TURN = 4.5 / 12.0;

  /**
   * If the robot falls this far (meters) behind where the path says it should be, the path's clock
   * stops and waits for it to catch up instead of running away without it.
   */
  public final double CHOREO_LOOK_AHEAD_METERS = 0.5;

  /**
   * The last bit of a path: once the path's clock runs out, the robot turns to face the end point
   * and drives at it (see DifferentialPathFollower.settle). It drives at most this fast, in meters
   * per second, ...
   */
  public final double CHOREO_SETTLE_MAX_SPEED = 0.6;

  /** ... this much faster for every meter it is away, ... */
  public final double CHOREO_SETTLE_KP_DISTANCE = 1.5;

  /** ... turns this many radians per second for every radian it is pointing away, ... */
  public final double CHOREO_SETTLE_KP_HEADING = 3.0;

  /** ... and stops completely when it is this close to the end point, in meters. */
  public final double CHOREO_SETTLE_DEADBAND_METERS = 0.04;

  /** The path counts as finished when the robot is this close to the end point, in meters. */
  public final double CHOREO_TRANSLATION_TOLERANCE_METERS = 0.15;

  /** ... and is slower than this, in meters per second. */
  public final double CHOREO_STOPPED_SPEED_METERS_PER_SECOND = 0.15;
}
