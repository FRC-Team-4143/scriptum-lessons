import frc.robot.DriveMath;

public class LinearSpeedCheck {
    public static void main(String[] args) {
        expect("linearSpeed(2.0, 2.0)", DriveMath.linearSpeed(2.0, 2.0), 2.0);
        expect("linearSpeed(1.0, 3.0)", DriveMath.linearSpeed(1.0, 3.0), 2.0);
        expect("linearSpeed(-1.0, 1.0)", DriveMath.linearSpeed(-1.0, 1.0), 0.0);
        expect("linearSpeed(-2.0, -4.0)", DriveMath.linearSpeed(-2.0, -4.0), -3.0);
        System.out.println("linearSpeed looks good.");
    }

    static void expect(String call, double actual, double expected) {
        if (Math.abs(actual - expected) > 0.0001) {
            System.out.println(call + " returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
