public class Battery {
    // The lowest battery voltage in the log, in volts. Readings below 0 mean the sensor
    // missed that tick, so skip them. If there are no real readings at all (or the array
    // is empty), return 0.0.
    // BUG (Skip the missing readings): it reports a negative voltage.
    public static double lowestVolts(int[] millivolts) {
        int lowest = millivolts[0];
        for (int i = 1; i < millivolts.length; i++) {
            if (millivolts[i] < lowest) {
                lowest = millivolts[i];
            }
        }
        return lowest / 1000.0;
    }
}
