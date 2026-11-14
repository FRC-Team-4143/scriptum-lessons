package frc.robot.mechanisms;

/** Stands in for the real drivetrain: it remembers what it was told. */
public class DifferentialDriveMech {
  public static double left = Double.NaN;
  public static double right = Double.NaN;

  public DifferentialDriveMech(Object left_configs, Object right_configs) {}

  public void setDutyCycles(double left_output, double right_output) {
    left = left_output;
    right = right_output;
  }
}
