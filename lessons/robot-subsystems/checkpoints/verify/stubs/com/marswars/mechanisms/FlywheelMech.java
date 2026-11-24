package com.marswars.mechanisms;

import java.util.ArrayList;
import java.util.List;

public class FlywheelMech extends MechStub {
  /** Every flywheel made during the check, in order. */
  public static final List<FlywheelMech> made = new ArrayList<>();

  public FlywheelMech(
      String logging_prefix,
      List<MotorConfig> motor_configs,
      double gear_ratio,
      double wheel_inertia,
      double wheel_radius) {
    this(logging_prefix, null, motor_configs, gear_ratio, wheel_inertia, wheel_radius);
  }

  public FlywheelMech(
      String logging_prefix,
      String mech_name,
      List<MotorConfig> motor_configs,
      double gear_ratio,
      double wheel_inertia,
      double wheel_radius) {
    made.add(this);
  }

  public void setTargetVelocityWithFF(double velocity_rad_per_sec, double arbitrary_feedforward) {
    asked("velocity", velocity_rad_per_sec);
  }

  public void setTargetCurrentWithFF(double current_amps, double feedforward) {
    asked("current", current_amps);
  }
}
