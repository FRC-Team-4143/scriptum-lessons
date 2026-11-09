package com.marswars.logging;

/** Logging does nothing during the check. */
public final class MwLog {
  private MwLog() {}

  public static void log(String key, double value) {}

  public static void log(String key, boolean value) {}

  public static void log(String key, String value) {}
}
