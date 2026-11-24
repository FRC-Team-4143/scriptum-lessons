package com.marswars.subsystem;

public class MwConstants {
  protected MwConstants() {}

  protected String getSystemName() {
    return getClass().getSimpleName().replace("Constants", "");
  }
}
