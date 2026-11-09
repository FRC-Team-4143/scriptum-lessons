public class DriveModeEnumCheck {
    static final String[] VALUES = { "TANK", "ARCADE", "SWERVE", "FIELD_ORIENTED_SWERVE" };

    public static void main(String[] args) {
        Class<?> driveMode;
        try {
            // Nested inside Main, so its binary (compiled) name is Main$DriveMode.
            driveMode = Class.forName("Main$DriveMode");
        } catch (ClassNotFoundException e) {
            System.out.println(
                "No DriveMode type found. Keep it inside Main: enum DriveMode { TANK, ARCADE, SWERVE, FIELD_ORIENTED_SWERVE } "
                    + "(team standard: enum type names are UpperCamelCase, like classes; the values are SCREAMING_SNAKE_CASE)."
            );
            System.exit(1);
            return;
        }
        if (!driveMode.isEnum()) {
            System.out.println("DriveMode exists but isn't an enum.");
            System.exit(1);
        }
        for (String value : VALUES) {
            boolean found = false;
            for (Object c : driveMode.getEnumConstants()) {
                if (c.toString().equals(value)) found = true;
            }
            if (!found) {
                System.out.println("DriveMode is missing " + value + ". It needs TANK, ARCADE, SWERVE, and FIELD_ORIENTED_SWERVE.");
                System.exit(1);
            }
        }
        System.out.println("DriveMode enum looks good.");
    }
}
