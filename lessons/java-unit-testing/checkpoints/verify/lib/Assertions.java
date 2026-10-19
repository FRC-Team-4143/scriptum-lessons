// The check's own copy, so an edited project copy can't change what passes.
// A small copy of JUnit's Assertions class. Each method checks one thing and
// throws an AssertionError when it is wrong, which makes the test FAIL.
//
// In real JUnit you write `import static org.junit.jupiter.api.Assertions.*;`
// and then just assertEquals(...). Here you write Assertions.assertEquals(...).
public class Assertions {
    // Decimals: passes when actual is within tolerance of expected.
    public static void assertEquals(double expected, double actual, double tolerance) {
        assertEquals(expected, actual, tolerance, null);
    }

    public static void assertEquals(double expected, double actual, double tolerance, String message) {
        if (Double.isNaN(actual) || Math.abs(expected - actual) > tolerance) {
            fail(message, "expected " + expected + " but was " + actual + " (tolerance " + tolerance + ")");
        }
    }

    // Whole numbers.
    public static void assertEquals(int expected, int actual) {
        assertEquals(expected, actual, null);
    }

    public static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            fail(message, "expected " + expected + " but was " + actual);
        }
    }

    // Anything else: Strings, booleans, objects. null is allowed.
    public static void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, (String) null);
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual == null : expected.equals(actual)) {
            return;
        }
        String why = "expected " + expected + " but was " + actual;
        if (expected instanceof Number && actual instanceof Number) {
            why += " (comparing decimals? pass a tolerance: assertEquals(expected, actual, 0.0001))";
        }
        fail(message, why);
    }

    public static void assertTrue(boolean condition) {
        assertTrue(condition, null);
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            fail(message, "expected true but was false");
        }
    }

    public static void assertFalse(boolean condition) {
        assertFalse(condition, null);
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            fail(message, "expected false but was true");
        }
    }

    private static void fail(String message, String why) {
        throw new AssertionError(message == null ? why : message + ": " + why);
    }
}
