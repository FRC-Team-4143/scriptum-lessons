public class SubtractIntsCheck {
    public static void main(String[] args) {
        check(Main.subtractInts(7, 4), 3, "subtractInts(7, 4)");
        check(Main.subtractInts(2, 5), -3, "subtractInts(2, 5)");
        check(Main.subtractInts(0, 0), 0, "subtractInts(0, 0)");
        System.out.println("subtractInts looks good.");
    }

    static void check(int actual, int expected, String label) {
        if (actual != expected) {
            System.out.println(label + " returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
