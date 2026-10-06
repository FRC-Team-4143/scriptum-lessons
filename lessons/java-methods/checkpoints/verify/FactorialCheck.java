public class FactorialCheck {
    public static void main(String[] args) {
        check(0, 1);
        check(1, 1);
        check(5, 120);
        check(10, 3628800);
        System.out.println("factorial looks good.");
    }

    static void check(int n, int expected) {
        int actual = Main.factorial(n);
        if (actual != expected) {
            System.out.println("factorial(" + n + ") returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
