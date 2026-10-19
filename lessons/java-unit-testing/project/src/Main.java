public class Main {
    public static void main(String[] args) {
        // Runs every @Test method in each test class.
        int failed = TestRunner.run(DriveMathTest.class)
            + TestRunner.run(ShooterMathTest.class);

        if (failed == 0) {
            System.out.println("All tests pass.");
        } else {
            System.out.println(failed + " test(s) failed. Read the FAIL lines above.");
        }
    }
}
