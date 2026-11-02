package frc.robot.subsystems.intake;

import com.ctre.phoenix6.signals.InvertedValue;
import com.marswars.mechanisms.MotorConfig;
import com.marswars.mechanisms.MotorConfig.TalonMotorType;
import com.marswars.subsystem.MwConstants;

/** Every number that describes the intake, in one place. */
public class IntakeConstants extends MwConstants {

  /** The things the intake can be doing. */
  public enum IntakeStates {
    /** The driver's button controls the intake directly. */
    MANUAL
  }

  public final int ROLLER_MOTOR_ID = 22;
  public final double ROLLER_GEAR_RATIO = 4.0;
  public final double ROLLER_INERTIA = 0.0005; // kg*m^2, used by the simulation
  public final MotorConfig ROLLER_MOTOR_CONFIG = rollerConfig(ROLLER_MOTOR_ID);
  public final double INTAKE_DUTY_CYCLE = 0.6;

  private static MotorConfig rollerConfig(int can_id) {
    MotorConfig config = new MotorConfig();
    config.can_id = can_id;
    config.motor_type = TalonMotorType.FALCON500;
    config.getAsFXConfig().MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
    return config;
  }
}
