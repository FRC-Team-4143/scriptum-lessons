public class ApplyAssignmentsCheck {
    public static void main(String[] args) {
        check(Main.applyAssignments(0), 1.72, "applyAssignments(0)");
        check(Main.applyAssignments(10), 21.72, "applyAssignments(10)");
        check(Main.applyAssignments(-5), -8.28, "applyAssignments(-5)");
        System.out.println("applyAssignments looks good.");
    }

    static void check(double actual, double expected, String label) {
        if (Math.abs(actual - expected) > 0.001) {
            System.out.println(label + " returned " + actual + ", expected about " + expected + ".");
            System.exit(1);
        }
    }
}
