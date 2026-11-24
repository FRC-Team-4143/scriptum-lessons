package com.marswars.mechanisms;

import com.marswars.subsystem.SubsystemIoBase;

/** Remembers the last thing the subsystem asked a mechanism to do, so the check can read it. */
public abstract class MechStub implements SubsystemIoBase {
  /** "duty", "velocity", "position" or "current"; null until something is asked. */
  public String last_mode = null;
  public double last_value = Double.NaN;

  protected void asked(String mode, double value) {
    last_mode = mode;
    last_value = value;
  }

  public void forget() {
    last_mode = null;
    last_value = Double.NaN;
  }

  @Override
  public void readInputs(double timestamp) {}

  @Override
  public void writeOutputs(double timestamp) {}

  @Override
  public void logData() {}

  public double getCurrentVelocity() {
    return 0.0;
  }

  public double getLeaderSupplyCurrent() {
    return 0.0;
  }

  public double getLeaderStatorCurrent() {
    return 0.0;
  }

  public void setTargetDutyCycle(double duty_cycle) {
    asked("duty", duty_cycle);
  }

  public void setTargetVelocity(double velocity_rad_per_sec) {
    asked("velocity", velocity_rad_per_sec);
  }

  public void setTargetVelocityMotionProfile(double velocity_rad_per_sec) {
    asked("velocity", velocity_rad_per_sec);
  }

  public void setTargetCurrent(double current_amps) {
    asked("current", current_amps);
  }

  public void applyLoadTorque(double torque_nm) {}
}
