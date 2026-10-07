public class Main {
    public static String batteryStatus(double percent) {
        if (percent == 0) return "EMPTY";
        if (percent < 50) return "LOW";
        if (percent < 90) return "MEDIUM";
        return "FULL";
    }
    public static String driveModeName(int mode) {
        switch (mode) {
            case 1: return "Tank";
            case 2: return "Arcade";
            case 3: return "Swerve";
            case 4: return "Field-Oriented Swerve";
            default: return "Unknown mode";
        }
    }
    public static void main(String[] args) {}
}
