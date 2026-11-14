public class Main {
    public static String batteryStatus(double percent) {
        if (percent == 0) return "EMPTY";
        if (percent < 50) return "LOW";
        if (percent < 90) return "MEDIUM";
        return "FULL";
    }
    enum DriveMode { TANK, ARCADE, SWERVE, FIELD_ORIENTED_SWERVE }
    public static String driveModeName(DriveMode mode) {
        switch (mode) {
            case TANK: return "Tank";
            case ARCADE: return "Arcade";
            case SWERVE: return "Swerve";
            case FIELD_ORIENTED_SWERVE: return "Field-Oriented Swerve";
            default: return "Unknown mode";
        }
    }
    public static void main(String[] args) {}
}
