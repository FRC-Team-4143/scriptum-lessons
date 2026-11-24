package com.marswars.subsystem;

import java.util.List;

public abstract class MwSubsystem<
    StateType extends Enum<StateType>, ConstantsType extends MwConstants> {
  protected StateType system_state_;
  protected final ConstantsType CONSTANTS;
  private StateType wanted_state_;
  protected String subsystem_name_;

  public MwSubsystem(StateType default_state, ConstantsType constants) {
    String name = getClass().getSimpleName();
    if (name.endsWith("Subsystem")) {
      name = name.substring(0, name.length() - "Subsystem".length());
    }
    CONSTANTS = constants;
    subsystem_name_ = name;
    system_state_ = default_state;
    wanted_state_ = default_state;
  }

  protected void handleStateTransition(StateType wanted) {
    system_state_ = wanted_state_;
  }

  public abstract List<SubsystemIoBase> getIos();

  public abstract void updateLogic(double timestamp);

  public abstract void reset();

  public void setWantedState(StateType wanted) {
    wanted_state_ = wanted;
  }

  public StateType getSystemState() {
    return system_state_;
  }

  public void update(double timestamp) {
    if (wanted_state_ != system_state_) {
      handleStateTransition(wanted_state_);
    }
    updateLogic(timestamp);
  }

  public String getSubsystemKey() {
    return "Subsystem/" + subsystem_name_ + "/";
  }

  public String getNtKey() {
    return "Robot/Subsystem/" + subsystem_name_ + "/";
  }

  public String getName() {
    return subsystem_name_;
  }
}
