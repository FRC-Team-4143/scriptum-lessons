public class SumDigitsCheck {
    public static void main(String[] args) {
        check(7, 7);
        check(0, 0);
        check(123, 6);
        check(9999, 36);
        check(1234, 10);
        System.out.println("sumDigits looks good.");
    }

    static void check(int n, int expected) {
        int actual = Main.sumDigits(n);
        if (actual != expected) {
            System.out.println("sumDigits(" + n + ") returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
