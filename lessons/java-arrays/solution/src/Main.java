public class Main {
    public static int max(int[] values) {
        int largest = values[0];
        for (int i = 1; i < values.length; i++) if (values[i] > largest) largest = values[i];
        return largest;
    }
    public static double average(int[] values) {
        int total = 0;
        for (int i = 0; i < values.length; i++) total += values[i];
        return (double) total / values.length;
    }
    public static int[] doubleAll(int[] values) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) result[i] = values[i] * 2;
        return result;
    }
    public static boolean contains(int[] values, int target) {
        for (int i = 0; i < values.length; i++) if (values[i] == target) return true;
        return false;
    }
    public static void main(String[] args) {}
}
