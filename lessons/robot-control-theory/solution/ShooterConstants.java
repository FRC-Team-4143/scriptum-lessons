package frc.robot.subsystems.shooter;

import com.ctre.phoenix6.signals.InvertedValue;
import com.marswars.mechanisms.MotorConfig;
import com.marswars.mechanisms.MotorConfig.TalonMotorType;
import com.marswars.subsystem.MwConstants;
import edu.wpi.first.math.util.Units;

/**
 * Every number that describes the shooter, in one place. Subsystem code uses names like
 * CONSTANTS.SHOOT_VELOCITY instead of raw numbers, so changing the robot means editing one file.
 */
public class ShooterConstants extends MwConstants {

  /** The things the shooter can be doing. */
  public enum ShooterStates {
    /** The driver's buttons control the shooter directly. */
    MANUAL
  }

  /** The ways the flywheel can be told to hold its speed. */
  public enum FlywheelControl {
    /** Full power when too slow, no power when fast enough. */
    BANG_BANG,
    /** A power level worked out from the target speed alone (a good guess, no checking). */
    FEEDFORWARD,
    /** Feedforward plus a correction for the measured error (what the motor controller does). */
    PID
  }

  // =============================================================================
  // FLYWHEEL: the fast spinning wheel that launches the game piece
  // =============================================================================

  // Change this, run again, and watch Subsystem/Shooter/FlywheelVelocity in AdvantageScope.
  public final FlywheelControl FLYWHEEL_CONTROL = FlywheelControl.PID;

  // Feedforward: volts needed just to HOLD a speed, per (rotation per second). A first guess is
  // 12 volts divided by the flywheel's top speed in rotations per second.
  public final double FLYWHEEL_KV = 0.12;
  // Feedback: extra volts for every (rotation per second) of speed ERROR.
  public final double FLYWHEEL_KP = 0.2;

  public final int FLYWHEEL_MOTOR_ID = 20;
  public final double FLYWHEEL_GEAR_RATIO = 1.0; // motor turns for every 1 flywheel turn
  public final double FLYWHEEL_RADIUS = Units.inchesToMeters(3.0);
  public final double FLYWHEEL_MASS = 2.3; // kg, approximate
  // A solid wheel is harder to spin the heavier and wider it is: 1/2 * mass * radius^2 (kg*m^2).
  public final double FLYWHEEL_INERTIA = 0.5 * FLYWHEEL_MASS * FLYWHEEL_RADIUS * FLYWHEEL_RADIUS;
  public final double SHOOT_VELOCITY = 300.0; // radians per second
  public final MotorConfig FLYWHEEL_MOTOR_CONFIG =
      flywheelConfig(FLYWHEEL_MOTOR_ID, FLYWHEEL_KV, FLYWHEEL_KP);

  // The flywheel counts as "at speed" when it is within this fraction of the target.
  public final double AT_SPEED_TOLERANCE = 0.03;
  // How hard a launched game piece pushes back on the flywheel in the simulation (newton-meters).
  public final double BALL_LOAD_TORQUE = 25.0;

  // =============================================================================
  // ROLLER: feeds ("indexes") the game piece into the flywheel
  // =============================================================================

  public final int ROLLER_MOTOR_ID = 21;
  public final double ROLLER_GEAR_RATIO = 3.0;
  public final double ROLLER_INERTIA = 0.0005; // kg*m^2, used by the simulation

  public final MotorConfig ROLLER_MOTOR_CONFIG = rollerConfig(ROLLER_MOTOR_ID);
  public final double INDEX_DUTY_CYCLE = 0.5;

  // =============================================================================
  // Helper methods that build motor settings. You do not need to change these.
  // =============================================================================

  private static MotorConfig flywheelConfig(int can_id, double k_v, double k_p) {
    MotorConfig config = baseConfig(can_id, false);
    // The motor controller's velocity gains (used by the PID style).
    config.getAsFXConfig().Slot1.kV = k_v;
    config.getAsFXConfig().Slot1.kP = k_p;
    return config;
  }

  private static MotorConfig rollerConfig(int can_id) {
    return baseConfig(can_id, false);
  }

  private static MotorConfig baseConfig(int can_id, boolean inverted) {
    MotorConfig config = new MotorConfig();
    config.can_id = can_id;
    config.motor_type = TalonMotorType.X60;
    config.getAsFXConfig().MotorOutput.Inverted =
        inverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;
    return config;
  }
}
