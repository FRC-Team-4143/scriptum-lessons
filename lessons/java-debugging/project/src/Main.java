public class Main {
    public static void main(String[] args) {
        System.out.println("Average speed: " + SpeedMath.averageSpeed(MatchLog.SPEEDS) + " m/s");
        System.out.println("Shot accuracy: " + Accuracy.percent(MatchLog.SHOTS_MADE, MatchLog.SHOTS_ATTEMPTED) + "%");
        System.out.println("Auto distance: " + Odometry.totalDistance(MatchLog.AUTO_LEGS) + " m");
        System.out.println("Lowest battery: " + Battery.lowestVolts(MatchLog.batteryMillivolts()) + " V");
        System.out.println("Seconds intaking: " + StateLog.countState(MatchLog.states(), "INTAKE"));
        System.out.println("Points scored: " + Scoring.totalPoints(MatchLog.SCORED_LEVELS));
        System.out.println("Good range readings: " + Cleaner.countValid(MatchLog.RANGE_READINGS));
    }
}
