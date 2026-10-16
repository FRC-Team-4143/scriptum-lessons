public class Battery {
    // The lowest battery voltage in the log, in volts. Readings below 0 mean the sensor
    // missed that tick, so skip them. If there are no real readings at all (or the array
    // is empty), return 0.0.
    public static double lowestVolts(int[] millivolts) {
        int lowest = -1;
        for (int i = 0; i < millivolts.length; i++) {
            if (millivolts[i] < 0) {
                continue;
            }
            if (lowest < 0 || millivolts[i] < lowest) {
                lowest = millivolts[i];
            }
        }
        if (lowest < 0) {
            return 0.0;
        }
        return lowest / 1000.0;
    }
}
