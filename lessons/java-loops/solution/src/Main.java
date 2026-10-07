public class Main {
    public static int sumTo(int n) {
        int total = 0;
        for (int i = 1; i <= n; i++) total += i;
        return total;
    }
    public static int countDivisibleByThree(int n) {
        int count = 0;
        for (int i = 1; i <= n; i++) if (i % 3 == 0) count++;
        return count;
    }
    public static void main(String[] args) {}
}
