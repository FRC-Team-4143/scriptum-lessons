import frc.robot.DriveMath;

public class AngularSpeedCheck {
    public static void main(String[] args) {
        expect("angularSpeed(1.0, 1.0, 0.6)", DriveMath.angularSpeed(1.0, 1.0, 0.6), 0.0);
        expect("angularSpeed(0.0, 0.6, 0.6)", DriveMath.angularSpeed(0.0, 0.6, 0.6), 1.0);
        expect("angularSpeed(0.6, 0.0, 0.6)", DriveMath.angularSpeed(0.6, 0.0, 0.6), -1.0);
        expect("angularSpeed(-1.0, 1.0, 0.5)", DriveMath.angularSpeed(-1.0, 1.0, 0.5), 4.0);
        System.out.println("angularSpeed looks good.");
    }

    static void expect(String call, double actual, double expected) {
        if (Math.abs(actual - expected) > 0.0001) {
            System.out.println(call + " returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
