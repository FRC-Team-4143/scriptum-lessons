public class MatchPeriodEnumCheck {
    public static void main(String[] args) {
        Class<?> matchPeriodClass;
        try {
            // Nested inside Main, so its binary (compiled) name is
            // Main$MatchPeriod, not a bare top-level MatchPeriod - see
            // Main.java's TODO.
            matchPeriodClass = Class.forName("Main$MatchPeriod");
        } catch (ClassNotFoundException e) {
            System.out.println(
                "No MatchPeriod type found. Define it inside Main: "
                    + "enum MatchPeriod { AUTONOMOUS, TELEOP, ENDGAME } "
                    + "(team standard: enum type names are UpperCamelCase, like classes; the values are SCREAMING_SNAKE_CASE)."
            );
            System.exit(1);
            return;
        }
        if (!matchPeriodClass.isEnum()) {
            System.out.println("MatchPeriod exists but isn't an enum.");
            System.exit(1);
        }
        Object[] constants = matchPeriodClass.getEnumConstants();
        boolean hasAutonomous = false;
        boolean hasTeleop = false;
        boolean hasEndgame = false;
        for (Object c : constants) {
            if (c.toString().equals("AUTONOMOUS")) hasAutonomous = true;
            if (c.toString().equals("TELEOP")) hasTeleop = true;
            if (c.toString().equals("ENDGAME")) hasEndgame = true;
        }
        if (!hasAutonomous || !hasTeleop || !hasEndgame) {
            System.out.println("MatchPeriod enum needs AUTONOMOUS, TELEOP, and ENDGAME values.");
            System.exit(1);
        }
        System.out.println("MatchPeriod enum looks good.");
    }
}
