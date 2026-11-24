package com.marswars.mechanisms;

import com.ctre.phoenix6.configs.TalonFXConfiguration;

public class MotorConfig {
  public enum TalonMotorType {
    X60,
    X44,
    FALCON500,
    MINION,
    NEO_550,
  }

  public String canbus_name = "rio";
  public int can_id = 1;
  public TalonMotorType motor_type = TalonMotorType.X60;
  private final TalonFXConfiguration fx_config_ = new TalonFXConfiguration();

  public MotorConfig() {}

  public MotorConfig(MotorConfig config) {
    canbus_name = config.canbus_name;
    can_id = config.can_id;
    motor_type = config.motor_type;
  }

  public TalonFXConfiguration getAsFXConfig() {
    return fx_config_;
  }
}
