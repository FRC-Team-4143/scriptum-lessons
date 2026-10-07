import java.util.Arrays;

public class FibonacciCheck {
    public static void main(String[] args) {
        check(1, new int[] {0});
        check(2, new int[] {0, 1});
        check(8, new int[] {0, 1, 1, 2, 3, 5, 8, 13});
        System.out.println("fibonacci looks good.");
    }

    static void check(int n, int[] expected) {
        int[] actual = Main.fibonacci(n);
        if (actual == null || !Arrays.equals(actual, expected)) {
            System.out.println(
                "fibonacci(" + n + ") returned "
                    + (actual == null ? "null" : Arrays.toString(actual))
                    + ", expected " + Arrays.toString(expected) + "."
            );
            System.exit(1);
        }
    }
}
