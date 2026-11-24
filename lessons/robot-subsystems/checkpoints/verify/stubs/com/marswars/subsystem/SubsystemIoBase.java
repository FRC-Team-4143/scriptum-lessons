package com.marswars.subsystem;

public interface SubsystemIoBase {
  void readInputs(double timestamp);

  void writeOutputs(double timestamp);

  void logData();
}
