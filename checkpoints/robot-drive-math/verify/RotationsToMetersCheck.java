import frc.robot.DriveMath;

public class RotationsToMetersCheck {
    public static void main(String[] args) {
        expect("rotationsToMeters(1.0, 0.1)", DriveMath.rotationsToMeters(1.0, 0.1), 0.6283185);
        expect("rotationsToMeters(0.0, 0.1)", DriveMath.rotationsToMeters(0.0, 0.1), 0.0);
        expect("rotationsToMeters(2.5, 0.0762)", DriveMath.rotationsToMeters(2.5, 0.0762), 1.1969);
        expect("rotationsToMeters(-1.0, 0.1)", DriveMath.rotationsToMeters(-1.0, 0.1), -0.6283185);
        System.out.println("rotationsToMeters looks good.");
    }

    static void expect(String call, double actual, double expected) {
        if (Math.abs(actual - expected) > 0.0001) {
            System.out.println(call + " returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
