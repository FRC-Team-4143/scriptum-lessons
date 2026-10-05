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

  // =============================================================================
  // FLYWHEEL: the fast spinning wheel that launches the game piece
  // =============================================================================

  public final int FLYWHEEL_MOTOR_ID = 20;
  public final double FLYWHEEL_GEAR_RATIO = 1.0; // motor turns for every 1 flywheel turn
  public final double FLYWHEEL_RADIUS = Units.inchesToMeters(3.0);
  public final double FLYWHEEL_MASS = 2.3; // kg, approximate
  // A solid wheel is harder to spin the heavier and wider it is: 1/2 * mass * radius^2 (kg*m^2).
  public final double FLYWHEEL_INERTIA = 0.5 * FLYWHEEL_MASS * FLYWHEEL_RADIUS * FLYWHEEL_RADIUS;
  public final double SHOOT_VELOCITY = 300.0; // radians per second
  public final MotorConfig FLYWHEEL_MOTOR_CONFIG = flywheelConfig(FLYWHEEL_MOTOR_ID);

  // =============================================================================
  // ROLLER: feeds ("indexes") the game piece into the flywheel
  // =============================================================================

  public final int ROLLER_MOTOR_ID = 21;
  public final double ROLLER_GEAR_RATIO = 3.0;
  public final double ROLLER_INERTIA = 0.0005; // kg*m^2, used by the simulation

  // TODO: create ROLLER_MOTOR_CONFIG. Copy the style of FLYWHEEL_MOTOR_CONFIG above, but use
  // rollerConfig(ROLLER_MOTOR_ID) from the bottom of this file.

  // TODO: create INDEX_DUTY_CYCLE: how hard the roller pushes while indexing. Use 0.5.

  // =============================================================================
  // Helper methods that build motor settings. You do not need to change these.
  // =============================================================================

  private static MotorConfig flywheelConfig(int canId) {
    MotorConfig config = baseConfig(canId, false);
    // Velocity control gains. kV: volts per (rotation per second) needed just to HOLD a speed.
    // kP: extra volts for every (rotation per second) of speed error.
    config.getAsFXConfig().Slot1.kV = 0.12;
    config.getAsFXConfig().Slot1.kP = 0.1;
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
