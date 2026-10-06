public class ReverseStringCheck {
    public static void main(String[] args) {
        check("hello", "olleh");
        check("MARS/WARS", "SRAW/SRAM");
        check("a", "a");
        check("", "");
        System.out.println("reverseString looks good.");
    }

    static void check(String s, String expected) {
        String actual = Main.reverseString(s);
        if (actual == null || !actual.equals(expected)) {
            System.out.println(
                "reverseString(\"" + s + "\") returned "
                    + (actual == null ? "null" : "\"" + actual + "\"")
                    + ", expected \"" + expected + "\"."
            );
            System.exit(1);
        }
    }
}
