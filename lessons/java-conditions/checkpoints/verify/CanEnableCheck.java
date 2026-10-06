public class CanEnableCheck {
    public static void main(String[] args) {
        check(Main.canEnable(true, false), true, "canEnable(true, false)");
        check(Main.canEnable(true, true), false, "canEnable(true, true)");
        check(Main.canEnable(false, false), false, "canEnable(false, false)");
        check(Main.canEnable(false, true), false, "canEnable(false, true)");
        System.out.println("canEnable looks good.");
    }

    static void check(boolean actual, boolean expected, String label) {
        if (actual != expected) {
            System.out.println(label + " returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
