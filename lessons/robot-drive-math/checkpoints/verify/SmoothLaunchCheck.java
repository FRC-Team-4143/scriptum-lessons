import frc.robot.DriveMath;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SmoothLaunchCheck {
    static Method method;

    public static void main(String[] args) throws Exception {
        try {
            method = DriveMath.class.getMethod("smoothStick", double[].class, double.class, double.class);
        } catch (NoSuchMethodException e) {
            System.out.println("Add public static double[] smoothStick(double[] targets, double speedUpStep, double slowDownStep) to DriveMath.");
            System.exit(1);
        }

        expect("speeding up is limited", new double[] {1, 1, 1, 1, 1}, 0.25, 0.5, new double[] {0.25, 0.5, 0.75, 1, 1});
        expect("backward speeds up the same way", new double[] {-1, -1, -1}, 0.3, 0.5, new double[] {-0.3, -0.6, -0.9});
        expect("slowing down uses its own limit", new double[] {1, 0, 0, 0}, 1.0, 0.25, new double[] {1, 0.75, 0.5, 0.25});
        expect("slowing down from backward", new double[] {-0.8, 0, 0}, 1.0, 0.5, new double[] {-0.8, -0.3, 0});
        expect("it lands on a target closer than one step", new double[] {0.1, 0.1, 0.05}, 0.25, 0.25, new double[] {0.1, 0.1, 0.05});
        expect("it lands exactly on a target one step away", new double[] {0.5, 1.0}, 0.5, 0.5, new double[] {0.5, 1.0});
        expect("it stops on zero before reversing", new double[] {0.5, -1, -1, -1, -1}, 0.5, 0.3, new double[] {0.5, 0.2, 0, -0.5, -1});
        expect("it stops on zero from backward too", new double[] {-0.4, 1, 1, 1}, 0.4, 1.0, new double[] {-0.4, 0, 0.4, 0.8});
        expect("no targets", new double[] {}, 0.5, 0.5, new double[] {});

        double[] targets = {1, 1};
        call(targets, 0.5, 0.5);
        if (targets[0] != 1 || targets[1] != 1) {
            System.out.println("smoothStick changed the targets array it was given. Fill in a new array and return that.");
            System.exit(1);
        }
        System.out.println("smoothStick looks good: it never jerks the robot, and it never reverses in one step.");
    }

    static double[] call(double[] targets, double speedUp, double slowDown) throws Exception {
        return (double[]) method.invoke(null, targets, speedUp, slowDown);
    }

    static void expect(String what, double[] targets, double speedUp, double slowDown, double[] expected) throws Exception {
        double[] actual = call(targets.clone(), speedUp, slowDown);
        boolean same = actual != null && actual.length == expected.length;
        for (int i = 0; same && i < expected.length; i++) {
            same = Math.abs(actual[i] - expected[i]) < 0.0001;
        }
        if (!same) {
            System.out.println(
                    what + ": smoothStick(" + Arrays.toString(targets) + ", " + speedUp + ", " + slowDown + ") returned "
                            + Arrays.toString(actual) + ", expected " + Arrays.toString(expected) + ".");
            System.exit(1);
        }
    }
}
