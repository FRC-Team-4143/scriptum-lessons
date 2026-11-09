package frc.robot.subsystems.shooter;

/**
 * A feedforward plus PID controller for the flywheel, written by hand. It answers one question each
 * loop: how many volts should the motor get?
 */
public class FlywheelPid {
  private static final double MAX_VOLTS = 12.0;
  // Only add up the error when this close to the target, in radians per second.
  private static final double I_ZONE = 30.0;

  private final double k_v_;
  private final double k_p_;
  private final double k_i_;
  private final double k_d_;

  private double error_sum_ = 0.0;
  private double last_measured_ = 0.0;
  private boolean has_last_ = false;

  public FlywheelPid(double k_v, double k_p, double k_i, double k_d) {
    k_v_ = k_v;
    k_p_ = k_p;
    k_i_ = k_i;
    k_d_ = k_d;
  }

  /** Forgets the added-up error and the last speed. */
  public void reset() {
    error_sum_ = 0.0;
    has_last_ = false;
  }

  /** Returns the volts for the motor, from -12 to 12. */
  public double calculate(double target, double measured, double dt_seconds) {
    double error = target - measured;

    // On the measurement, not the error, so changing the target doesn't kick the motor.
    double slope = 0.0;
    if (has_last_ && dt_seconds > 0.0) {
      slope = (measured - last_measured_) / dt_seconds;
    }
    last_measured_ = measured;
    has_last_ = true;

    double without_i = k_v_ * target + k_p_ * error - k_d_ * slope;

    // Anti-windup. Far from the target the error is big for a long time, so adding it up only
    // stores an overshoot for later. The same goes for an output stuck at the battery's limit.
    double next_sum = error_sum_ + error * dt_seconds;
    double next_volts = without_i + k_i_ * next_sum;
    boolean close = Math.abs(error) <= I_ZONE;
    boolean stuck = Math.abs(next_volts) > MAX_VOLTS;
    if (close && !stuck) {
      error_sum_ = next_sum;
    }

    double volts = without_i + k_i_ * error_sum_;
    return Math.max(-MAX_VOLTS, Math.min(MAX_VOLTS, volts));
  }
}
