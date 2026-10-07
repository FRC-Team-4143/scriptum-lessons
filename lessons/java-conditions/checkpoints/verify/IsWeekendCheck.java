public class IsWeekendCheck {
    public static void main(String[] args) {
        check(Main.isWeekend("Saturday"), true, "isWeekend(\"Saturday\")");
        check(Main.isWeekend("Sunday"), true, "isWeekend(\"Sunday\")");
        check(Main.isWeekend("Monday"), false, "isWeekend(\"Monday\")");
        check(Main.isWeekend("Friday"), false, "isWeekend(\"Friday\")");
        System.out.println("isWeekend looks good.");
    }

    static void check(boolean actual, boolean expected, String label) {
        if (actual != expected) {
            System.out.println(label + " returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
