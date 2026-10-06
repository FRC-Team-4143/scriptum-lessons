public class FibonacciRecursiveCheck {
    public static void main(String[] args) {
        check(0, 0);
        check(1, 1);
        check(2, 1);
        check(6, 8);
        check(10, 55);
        System.out.println("fibonacci looks good.");
    }

    static void check(int n, int expected) {
        int actual = Main.fibonacci(n);
        if (actual != expected) {
            System.out.println("fibonacci(" + n + ") returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
