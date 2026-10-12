import frc.robot.DriveMath;

public class RadiansToMetersCheck {
    public static void main(String[] args) {
        expect("radiansToMeters(1.0, 0.1)", DriveMath.radiansToMeters(1.0, 0.1), 0.1);
        expect("radiansToMeters(0.0, 0.1)", DriveMath.radiansToMeters(0.0, 0.1), 0.0);
        expect("radiansToMeters(2.5, 0.0762)", DriveMath.radiansToMeters(2.5, 0.0762), 0.1905);
        expect("radiansToMeters(2 * PI, 0.1)", DriveMath.radiansToMeters(2.0 * Math.PI, 0.1), 0.6283185);
        expect("radiansToMeters(-1.0, 0.1)", DriveMath.radiansToMeters(-1.0, 0.1), -0.1);
        System.out.println("radiansToMeters looks good.");
    }

    static void expect(String call, double actual, double expected) {
        if (Math.abs(actual - expected) > 0.0001) {
            System.out.println(call + " returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
