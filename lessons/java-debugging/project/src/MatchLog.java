// The data from one practice match. It's provided and correct: you don't need to change
// anything in this file.
public class MatchLog {
    // The robot's speed in meters per second, sampled once a second.
    public static final double[] SPEEDS = {0.0, 1.0, 2.5, 3.0, 3.5, 2.5, 1.5, 0.5};

    // Shots at the goal.
    public static final int SHOTS_MADE = 7;
    public static final int SHOTS_ATTEMPTED = 8;

    // How far the robot drove in each leg of autonomous, in meters.
    public static final double[] AUTO_LEGS = {2.5, 1.5, 3.0};

    // Which level each scoring cycle reached: 1 (low), 2 (mid) or 3 (high).
    public static final int[] SCORED_LEVELS = {1, 3, 2, 3, 1};

    // Distance sensor readings in meters. -1.0 means the sensor glitched.
    public static final double[] RANGE_READINGS = {1.2, 1.1, -1.0, 0.9, -1.0, 0.8};

    // The battery voltage every tick of the match, in millivolts (12600 means 12.6 V).
    // A reading of -1 means the sensor missed that tick.
    public static int[] batteryMillivolts() {
        int[] millivolts = new int[500];
        int volts = 12600;
        for (int i = 0; i < millivolts.length; i++) {
            volts -= (i * 37) % 5;
            if (i % 50 == 25) {
                volts -= 400;
            }
            if (i % 50 == 30) {
                volts += 350;
            }
            millivolts[i] = volts;
            if ((i * 7919) % 500 == 113) {
                millivolts[i] = -1;
            }
        }
        return millivolts;
    }

    // What the robot was doing each second. The log stores the names in lowercase, and the
    // dashboard wants capitals.
    private static final String[] RAW_STATES = {"drive", "intake", "intake", "drive", "shoot", "intake", "drive", "shoot"};

    public static String[] states() {
        String[] states = new String[RAW_STATES.length];
        for (int i = 0; i < RAW_STATES.length; i++) {
            states[i] = RAW_STATES[i].toUpperCase();
        }
        return states;
    }
}
