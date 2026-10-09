package lesson;

import com.marswars.logging.MwLog;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provided for you, no need to edit. It remembers the biggest and smallest value your code has
 * logged for a few keys and republishes them under "Check/". That lets the lesson's Verify button
 * see what your robot did even after you let go of the sticks.
 */
public final class LessonChecks {
  private static final String PREFIX = "/AdvantageKit/RealOutputs/";

  // {smallest, biggest}, set by the first value seen (so a value that starts big and shrinks
  // toward a target, like a distance, gets a real minimum instead of 0).
  private final Map<String, double[]> ranges = new LinkedHashMap<>();
  private final Map<String, NetworkTableEntry> entries = new LinkedHashMap<>();

  // How far apart two logged values got, e.g. the left and right outputs while turning.
  private record Spread(NetworkTableEntry a, NetworkTableEntry b, double[] max) {}

  private final Map<String, Spread> spreads = new LinkedHashMap<>();

  public LessonChecks(String... keys) {
    for (String key : keys) {
      ranges.put(key, new double[] {Double.NaN, Double.NaN});
      entries.put(key, NetworkTableInstance.getDefault().getEntry(PREFIX + key));
    }
  }

  /** Also track the biggest gap between two logged values, published as Check/<name>/Max. */
  public LessonChecks spread(String name, String keyA, String keyB) {
    spreads.put(
        name,
        new Spread(
            NetworkTableInstance.getDefault().getEntry(PREFIX + keyA),
            NetworkTableInstance.getDefault().getEntry(PREFIX + keyB),
            new double[] {0.0}));
    return this;
  }

  /** Call once per loop, after your code has logged its values. */
  public void update() {
    for (var item : ranges.entrySet()) {
      NetworkTableEntry entry = entries.get(item.getKey());
      double[] range = item.getValue();
      if (entry.exists()) {
        double value = entry.getDouble(0.0);
        if (Double.isNaN(range[0])) {
          range[0] = value;
          range[1] = value;
        }
        range[0] = Math.min(range[0], value);
        range[1] = Math.max(range[1], value);
      }
      // Until the value has been logged once, publish 0 like before.
      MwLog.log("Check/" + item.getKey() + "/Min", Double.isNaN(range[0]) ? 0.0 : range[0]);
      MwLog.log("Check/" + item.getKey() + "/Max", Double.isNaN(range[1]) ? 0.0 : range[1]);
    }
    for (var item : spreads.entrySet()) {
      Spread spread = item.getValue();
      if (spread.a().exists() && spread.b().exists()) {
        double gap = Math.abs(spread.a().getDouble(0.0) - spread.b().getDouble(0.0));
        spread.max()[0] = Math.max(spread.max()[0], gap);
      }
      MwLog.log("Check/" + item.getKey() + "/Max", spread.max()[0]);
    }
  }
}
