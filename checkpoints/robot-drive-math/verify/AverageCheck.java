import frc.robot.DriveMath;

public class AverageCheck {
    public static void main(String[] args) {
        expect(new double[] {2.0, 4.0}, 3.0);
        expect(new double[] {1.0, 1.0, 1.0, 1.0, 1.0}, 1.0);
        expect(new double[] {-1.0, 1.0}, 0.0);
        expect(new double[] {5.0}, 5.0);
        expect(new double[] {0.5, 1.5, 2.5, 3.5, 4.5}, 2.5);
        System.out.println("average looks good.");
    }

    static void expect(double[] values, double expected) {
        double actual = DriveMath.average(values);
        if (Math.abs(actual - expected) > 0.0001) {
            System.out.println("average(" + java.util.Arrays.toString(values) + ") returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
