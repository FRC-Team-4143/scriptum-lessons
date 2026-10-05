package frc.robot;

import com.ctre.phoenix6.signals.InvertedValue;
import com.marswars.mechanisms.MotorConfig;
import com.marswars.mechanisms.MotorConfig.TalonMotorType;
import edu.wpi.first.math.util.Units;
import java.util.List;

/** Numbers that describe the robot. Change them here and the whole project follows. */
public final class Constants {
  private Constants() {}

  // The chassis. The wheel radius and track width are the real robot's. The gear ratio and mass are
  // still placeholders until they are measured.
  public static final double WHEEL_RADIUS_METERS = Units.inchesToMeters(3.0);
  public static final double GEAR_RATIO = 8.45; // motor turns for every 1 wheel turn
  public static final double TRACK_WIDTH_METERS = Units.inchesToMeters(24.0); // left to right wheels
  public static final double ROBOT_MASS_KG = 50.0;

  // Each drive motor is a brushless Kraken X60.
  //
  // The FIRST motor in a list is the "leader". Any motors after it are "followers": they copy the
  // leader exactly, so one side of the robot can use two motors but you only command it once.
  //
  public static final List<MotorConfig> LEFT_MOTORS = List.of(motor(1, false), motor(2, false));
  public static final List<MotorConfig> RIGHT_MOTORS = List.of(motor(3, true), motor(4, true));

  /** Builds the settings for one drive motor: its CAN id and whether it spins backwards. */
  private static MotorConfig motor(int canId, boolean inverted) {
    MotorConfig config = new MotorConfig();
    config.can_id = canId;
    config.motor_type = TalonMotorType.X60;
    config.getAsFXConfig().MotorOutput.Inverted =
        inverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;
    return config;
  }
}
