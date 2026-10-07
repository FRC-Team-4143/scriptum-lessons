public class AllianceEnumCheck {
    public static void main(String[] args) {
        Class<?> allianceClass;
        try {
            // Nested inside Main, so its binary (compiled) name is Main$ALLIANCE,
            // not a bare top-level ALLIANCE - see Main.java's TODO.
            allianceClass = Class.forName("Main$ALLIANCE");
        } catch (ClassNotFoundException e) {
            System.out.println(
                "No ALLIANCE type found. Define it inside Main: enum ALLIANCE { RED, BLUE } "
                    + "(team standard: enum names are SCREAMING_SNAKE_CASE)."
            );
            System.exit(1);
            return;
        }
        if (!allianceClass.isEnum()) {
            System.out.println("ALLIANCE exists but isn't an enum.");
            System.exit(1);
        }
        Object[] constants = allianceClass.getEnumConstants();
        boolean hasRed = false;
        boolean hasBlue = false;
        for (Object c : constants) {
            if (c.toString().equals("RED")) hasRed = true;
            if (c.toString().equals("BLUE")) hasBlue = true;
        }
        if (!hasRed || !hasBlue) {
            System.out.println("ALLIANCE enum needs both RED and BLUE values.");
            System.exit(1);
        }
        System.out.println("ALLIANCE enum looks good.");
    }
}
