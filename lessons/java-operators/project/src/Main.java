public class Main {
    // TODO: return the sum of a and b.
    public static int addInts(int a, int b) {
        return 0;
    }

    // TODO: convert inches to meters (1 inch = 0.0254 meters) and return the result.
    public static double inchesToMeters(double inches) {
        return 0.0;
    }

    // TODO: return true if value is even, false if it's odd.
    // Use the modulo operator (%).
    public static boolean isEven(int value) {
        return false;
    }

    // TODO: return the difference a - b.
    public static int subtractInts(int a, int b) {
        return 0;
    }

    // TODO: convert meters to inches (1 inch = 0.0254 meters) and return the
    // result - the inverse of inchesToMeters above.
    public static double metersToInches(double meters) {
        return 0.0;
    }

    // TODO: starting from start, apply each assignment operator in order and
    // return the final value:
    //   double x = start;   (=)
    //   x += 1;              (+=)
    //   x -= 0.14;            (-=)
    //   x *= 6;                (*=)
    //   x /= 3;                 (/=)
    public static double applyAssignments(double start) {
        return 0.0;
    }

    public static void main(String[] args) {
        System.out.println("3 + 4 = " + addInts(3, 4));
        System.out.println("10 inches = " + inchesToMeters(10) + " meters");
        System.out.println("Is 6 even? " + isEven(6));
        System.out.println("7 - 4 = " + subtractInts(7, 4));
        System.out.println("0.508 meters = " + metersToInches(0.508) + " inches");
        System.out.println("applyAssignments(0) = " + applyAssignments(0));
    }
}
