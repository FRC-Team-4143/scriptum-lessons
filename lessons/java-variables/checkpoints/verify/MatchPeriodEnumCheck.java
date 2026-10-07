public class MatchPeriodEnumCheck {
    public static void main(String[] args) {
        Class<?> matchPeriodClass;
        try {
            // Nested inside Main, so its binary (compiled) name is
            // Main$MATCH_PERIOD, not a bare top-level MATCH_PERIOD - see
            // Main.java's TODO.
            matchPeriodClass = Class.forName("Main$MATCH_PERIOD");
        } catch (ClassNotFoundException e) {
            System.out.println(
                "No MATCH_PERIOD type found. Define it inside Main: "
                    + "enum MATCH_PERIOD { AUTONOMOUS, TELEOP, ENDGAME } "
                    + "(team standard: enum names are SCREAMING_SNAKE_CASE)."
            );
            System.exit(1);
            return;
        }
        if (!matchPeriodClass.isEnum()) {
            System.out.println("MATCH_PERIOD exists but isn't an enum.");
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
            System.out.println("MATCH_PERIOD enum needs AUTONOMOUS, TELEOP, and ENDGAME values.");
            System.exit(1);
        }
        System.out.println("MATCH_PERIOD enum looks good.");
    }
}
