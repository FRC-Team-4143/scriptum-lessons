public class Main {
    // TODO: return "EMPTY", "LOW", "MEDIUM", or "FULL" based on percent:
    //   percent == 0        -> "EMPTY"
    //   0 < percent < 50    -> "LOW"
    //   50 <= percent < 90  -> "MEDIUM"
    //   percent >= 90       -> "FULL"
    public static String batteryStatus(double percent) {
        return null;
    }

    // TODO: give this enum its four values: TANK, ARCADE, SWERVE, and
    // FIELD_ORIENTED_SWERVE, separated by commas.
    // Team standard: the enum name is UpperCamelCase (like a class) and its
    // values are SCREAMING_SNAKE_CASE. It's nested inside Main because only
    // Main uses it.
    enum DriveMode {
    }

    // TODO: use a switch statement on mode to return the drive mode's name:
    //   TANK                  -> "Tank"
    //   ARCADE                -> "Arcade"
    //   SWERVE                -> "Swerve"
    //   FIELD_ORIENTED_SWERVE -> "Field-Oriented Swerve"
    // Return "Unknown mode" from the switch's default case.
    public static String driveModeName(DriveMode mode) {
        return null;
    }

    // TODO: return whether the robot can enable - true only when there's
    // comms AND the robot is not e-stopped. Use && and !.
    public static boolean canEnable(boolean has_comms, boolean e_stopped) {
        return false;
    }

    // TODO: return whether day is "Saturday" OR "Sunday". Use ||.
    public static boolean isWeekend(String day) {
        return false;
    }

    // TODO: return "REVERSED" if is_reversed is true, "FORWARD" otherwise.
    // Use the ternary operator (condition ? ifTrue : ifFalse) instead of an
    // if/else - one line, no braces.
    public static String motorDirection(boolean is_reversed) {
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Battery at 75%: " + batteryStatus(75));
        // TODO: once DriveMode has its values, delete the // on the next line.
        // System.out.println("Drive mode SWERVE: " + driveModeName(DriveMode.SWERVE));
        System.out.println("Can enable (comms, not e-stopped)? " + canEnable(true, false));
        System.out.println("Is Saturday a weekend? " + isWeekend("Saturday"));
        System.out.println("Motor direction (reversed): " + motorDirection(true));
    }
}
