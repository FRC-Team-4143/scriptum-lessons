import frc.robot.DriveMath;

public class ArcadeMathCheck {
    public static void main(String[] args) {
        expect(0.5, 0.0, 0.5, 0.5);
        expect(-0.5, 0.0, -0.5, -0.5);
        expect(0.0, 0.5, 0.5, -0.5);
        expect(0.4, 0.2, 0.6, 0.2);
        expect(1.0, 1.0, 1.0, 0.0);
        expect(-1.0, 0.5, -0.5 / 1.5, -1.5 / 1.5);
        System.out.println("arcadeToWheelSpeeds looks good.");
    }

    static void expect(double forward, double turn, double left, double right) {
        double[] actual = DriveMath.arcadeToWheelSpeeds(forward, turn);
        if (actual == null || actual.length != 2) {
            System.out.println("arcadeToWheelSpeeds must return an array with exactly 2 numbers: {left, right}.");
            System.exit(1);
        }
        if (Math.abs(actual[0] - left) > 0.0001 || Math.abs(actual[1] - right) > 0.0001) {
            System.out.println(
                "arcadeToWheelSpeeds(" + forward + ", " + turn + ") returned {" + actual[0] + ", " + actual[1]
                + "}, expected {" + left + ", " + right + "}."
            );
            System.exit(1);
        }
    }
}
