public class Main {
    // TODO: return the largest value in values.
    public static int max(int[] values) {
        return 0;
    }

    // TODO: return the average of all values in values, as a double.
    public static double average(int[] values) {
        return 0.0;
    }

    // TODO: return a NEW array where every value from `values` is doubled.
    // Don't modify the array that was passed in - build and return a new one.
    public static int[] doubleAll(int[] values) {
        return null;
    }

    // TODO: return true if target appears anywhere in values.
    public static boolean contains(int[] values, int target) {
        return false;
    }

    // TODO: return a NEW array with every value from `values`, sorted in
    // ascending order. Don't modify the array that was passed in - same
    // rule as doubleAll above.
    public static int[] sort(int[] values) {
        return null;
    }

    // TODO: return the first n numbers of the Fibonacci sequence, as a new
    // array: each number is the sum of the two before it, starting
    // 0, 1, 1, 2, 3, 5, 8, 13, ... Build the array with a loop - don't
    // hardcode the values. Unlike the methods above, you're building a new
    // array from scratch here, not transforming one you were given.
    public static int[] fibonacci(int n) {
        return new int[0];
    }

    public static void main(String[] args) {
        int[] sample = {4, 1, 4, 3, 9};
        System.out.println("Max: " + max(sample));
        System.out.println("Average: " + average(sample));
        System.out.println("Doubled: " + java.util.Arrays.toString(doubleAll(sample)));
        System.out.println("Contains 9? " + contains(sample, 9));
        System.out.println("Sorted: " + java.util.Arrays.toString(sort(sample)));
        System.out.println("First 8 Fibonacci: " + java.util.Arrays.toString(fibonacci(8)));
    }
}
