package frc.robot.subsystems.shooter;

import com.ctre.phoenix6.signals.InvertedValue;
import com.marswars.mechanisms.MotorConfig;
import com.marswars.mechanisms.MotorConfig.TalonMotorType;
import com.marswars.subsystem.MwConstants;
import edu.wpi.first.math.util.Units;

/** Every number that describes the shooter, in one place. */
public class ShooterConstants extends MwConstants {

  /** The things the shooter can be doing. */
  public enum ShooterStates {
    /** Everything stopped. */
    IDLE,
    /** Getting the flywheel up to speed. The roller waits. */
    SPIN_UP,
    /** The flywheel is at speed, so the roller feeds game pieces in. */
    SHOOT
  }

  // =============================================================================
  // FLYWHEEL
  // =============================================================================

  public final int FLYWHEEL_MOTOR_ID = 20;
  public final double FLYWHEEL_GEAR_RATIO = 1.0;
  // These match the real flywheel on the robot.
  public final double FLYWHEEL_RADIUS = Units.inchesToMeters(3.0);
  public final double FLYWHEEL_MASS = 2.3; // kg, approximate
  public final double FLYWHEEL_INERTIA = 0.5 * FLYWHEEL_MASS * FLYWHEEL_RADIUS * FLYWHEEL_RADIUS;
  public final double SHOOT_VELOCITY = 300.0; // radians per second
  public final MotorConfig FLYWHEEL_MOTOR_CONFIG = flywheelConfig(FLYWHEEL_MOTOR_ID);

  // The flywheel counts as "at speed" when it is within this fraction of the target.
  public final double AT_SPEED_TOLERANCE = 0.03;
  // Launching a game piece slows the flywheel. If it falls more than this fraction below the
  // target, it needs to spin back up.
  public final double DIP_TOLERANCE = 0.08;
  // How hard a launched game piece pushes back on the flywheel in the simulation (newton-meters).
  public final double BALL_LOAD_TORQUE = 25.0;

  // =============================================================================
  // ROLLER
  // =============================================================================

  public final int ROLLER_MOTOR_ID = 21;
  public final double ROLLER_GEAR_RATIO = 3.0;
  public final double ROLLER_INERTIA = 0.0005;
  public final MotorConfig ROLLER_MOTOR_CONFIG = rollerConfig(ROLLER_MOTOR_ID);
  public final double INDEX_DUTY_CYCLE = 0.5;

  // =============================================================================
  // Helper methods that build motor settings. You do not need to change these.
  // =============================================================================

  private static MotorConfig flywheelConfig(int canId) {
    MotorConfig config = baseConfig(canId, false);
    // Velocity control gains for the flywheel (you will learn where these numbers come from in the
    // next lesson).
    //   kV: volts needed just to HOLD a speed, per (rotation per second).
    //   kP: extra volts per (rotation per second) of speed ERROR.
    config.getAsFXConfig().Slot1.kV = 0.12;
    config.getAsFXConfig().Slot1.kP = 0.2;
    return config;
  }

  private static MotorConfig rollerConfig(int canId) {
    return baseConfig(canId, false);
  }

  private static MotorConfig baseConfig(int canId, boolean inverted) {
    MotorConfig config = new MotorConfig();
    config.can_id = canId;
    config.motor_type = TalonMotorType.X60;
    config.getAsFXConfig().MotorOutput.Inverted =
        inverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;
    return config;
  }
}
