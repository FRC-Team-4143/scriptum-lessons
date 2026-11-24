package com.marswars.mechanisms;

import java.util.List;

public class RollerMech extends MechStub {
  public RollerMech(String logging_prefix, List<MotorConfig> motor_configs, double gear_ratio) {}

  public RollerMech(
      String logging_prefix, String mech_name, List<MotorConfig> motor_configs, double gear_ratio) {}

  public RollerMech(
      String logging_prefix,
      List<MotorConfig> motor_configs,
      double gear_ratio,
      double roller_inertia) {}

  public RollerMech(
      String logging_prefix,
      String mech_name,
      List<MotorConfig> motor_configs,
      double gear_ratio,
      double roller_inertia) {}

  public void setCurrentPosition(double position_rad) {}

  public double getCurrentPosition() {
    return 0.0;
  }

  public void setTargetPosition(double position_rad) {
    asked("position", position_rad);
  }

  public void setTargetPositionMotionProfile(double position_rad) {
    asked("position", position_rad);
  }
}
