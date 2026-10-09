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
  public static final double TRACK_WIDTH_METERS =
      Units.inchesToMeters(24.0); // left to right wheels
  public static final double ROBOT_MASS_KG = 50.0;

  // Each side of the drivetrain has one brushless Kraken X60. A side is a List of motors, so a
  // robot
  // with more motors per side could simply add to its list.
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
}
