import java.lang.reflect.Method;

public class DriveModeNameCheck {
    // Looked up by name, so this compiles even before DriveMode has its values.
    static Class<?> driveMode;
    static Method driveModeName;

    public static void main(String[] args) throws Exception {
        try {
            driveMode = Class.forName("Main$DriveMode");
            driveModeName = Main.class.getMethod("driveModeName", driveMode);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            System.out.println("No driveModeName(DriveMode mode) found on Main. Keep the method's DriveMode parameter.");
            System.exit(1);
            return;
        }
        check("TANK", "Tank");
        check("ARCADE", "Arcade");
        check("SWERVE", "Swerve");
        check("FIELD_ORIENTED_SWERVE", "Field-Oriented Swerve");
        System.out.println("driveModeName looks good.");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static void check(String value, String expected) throws Exception {
        Object mode;
        try {
            mode = Enum.valueOf((Class) driveMode, value);
        } catch (IllegalArgumentException e) {
            System.out.println("DriveMode has no " + value + " yet. Give DriveMode its four values first.");
            System.exit(1);
            return;
        }
        Object actual = driveModeName.invoke(null, mode);
        if (!expected.equals(actual)) {
            System.out.println("driveModeName(DriveMode." + value + ") returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
