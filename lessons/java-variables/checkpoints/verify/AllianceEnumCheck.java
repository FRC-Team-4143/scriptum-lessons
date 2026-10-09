public class AllianceEnumCheck {
    public static void main(String[] args) {
        Class<?> allianceClass;
        try {
            // Nested inside Main, so its binary (compiled) name is Main$Alliance,
            // not a bare top-level Alliance - see Main.java's TODO.
            allianceClass = Class.forName("Main$Alliance");
        } catch (ClassNotFoundException e) {
            System.out.println(
                "No Alliance type found. Define it inside Main: enum Alliance { RED, BLUE } "
                    + "(team standard: enum type names are UpperCamelCase, like classes; the values are SCREAMING_SNAKE_CASE)."
            );
            System.exit(1);
            return;
        }
        if (!allianceClass.isEnum()) {
            System.out.println("Alliance exists but isn't an enum.");
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
            System.out.println("Alliance enum needs both RED and BLUE values.");
            System.exit(1);
        }
        System.out.println("Alliance enum looks good.");
    }
}
