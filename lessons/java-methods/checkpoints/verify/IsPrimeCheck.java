public class IsPrimeCheck {
    public static void main(String[] args) {
        check(-1, false);
        check(0, false);
        check(1, false);
        check(2, true);
        check(3, true);
        check(4, false);
        check(17, true);
        check(21, false);
        check(97, true);
        System.out.println("isPrime looks good.");
    }

    static void check(int n, boolean expected) {
        boolean actual = Main.isPrime(n);
        if (actual != expected) {
            System.out.println("isPrime(" + n + ") returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
