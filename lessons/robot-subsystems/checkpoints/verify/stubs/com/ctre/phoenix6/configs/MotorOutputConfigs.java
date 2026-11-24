package com.ctre.phoenix6.configs;

import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class MotorOutputConfigs {
  public InvertedValue Inverted = InvertedValue.CounterClockwise_Positive;
  public NeutralModeValue NeutralMode = NeutralModeValue.Coast;
}
