public class Main {
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double scaleJoystick(double raw_input, double sensitivity) {
        return clamp(raw_input * sensitivity, -1.0, 1.0);
    }

    public static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public static int factorial(int n) {
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static String reverseString(String s) {
        String reversed = "";
        for (int i = s.length() - 1; i >= 0; i--) {
            reversed += s.charAt(i);
        }
        return reversed;
    }

    public static boolean isPalindrome(String s) {
        return s.equals(reverseString(s));
    }

    public static void main(String[] args) {
        System.out.println(clamp(5.0, 0.0, 1.0));
        System.out.println(isPalindrome("racecar"));
    }
}
