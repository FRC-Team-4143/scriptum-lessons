import java.util.Arrays;

public class SortCheck {
    public static void main(String[] args) {
        check(new int[] {4, 1, 4, 3, 9}, new int[] {1, 3, 4, 4, 9});
        check(new int[] {5, -1, 3, 3, 0}, new int[] {-1, 0, 3, 3, 5});

        int[] input = {4, 1, 4, 3, 9};
        Main.sort(input);
        if (!Arrays.equals(input, new int[] {4, 1, 4, 3, 9})) {
            System.out.println("sort modified the array it was given - build and return a new one instead.");
            System.exit(1);
        }

        System.out.println("sort looks good.");
    }

    static void check(int[] input, int[] expected) {
        int[] actual = Main.sort(input);
        if (actual == null || !Arrays.equals(actual, expected)) {
            System.out.println(
                "sort(" + Arrays.toString(input) + ") returned "
                    + (actual == null ? "null" : Arrays.toString(actual))
                    + ", expected " + Arrays.toString(expected) + "."
            );
            System.exit(1);
        }
    }
}
