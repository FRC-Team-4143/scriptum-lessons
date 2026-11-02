import frc.robot.subsystems.shooter.FlywheelPid;

/** Tests FlywheelPid on exact numbers, then flies it against a model of the real flywheel. */
public class OwnPidCheck {
    static final double DT = 0.02;
    // The real flywheel: one Falcon 500, 1.04:1, a 2.3 kg wheel of 3 inch radius.
    static final double GEAR = 1.04;
    static final double INERTIA = 0.5 * 2.3 * 0.0762 * 0.0762;
    static final double KT = 4.69 / 257.0;
    static final double R = 12.0 / 257.0;
    static final double KE = 12.0 / 668.0;

    public static void main(String[] args) {
        math();
        flight();
        System.out.println("Your PID works: exact math, no windup, no kick, and it holds speed on a model of the flywheel.");
    }

    static void math() {
        close("kV only: new FlywheelPid(0.02, 0, 0, 0).calculate(300, 300, 0.02)", new FlywheelPid(0.02, 0, 0, 0).calculate(300, 300, DT), 6.0);
        close("kP only: new FlywheelPid(0, 0.1, 0, 0).calculate(100, 80, 0.02)", new FlywheelPid(0, 0.1, 0, 0).calculate(100, 80, DT), 2.0);
        close("kP with the wheel too fast: new FlywheelPid(0, 0.1, 0, 0).calculate(100, 120, 0.02)", new FlywheelPid(0, 0.1, 0, 0).calculate(100, 120, DT), -2.0);
        close("too much for the battery: new FlywheelPid(0, 1, 0, 0).calculate(300, 0, 0.02)", new FlywheelPid(0, 1, 0, 0).calculate(300, 0, DT), 12.0);
        close("too much backward: new FlywheelPid(0, 1, 0, 0).calculate(0, 300, 0.02)", new FlywheelPid(0, 1, 0, 0).calculate(0, 300, DT), -12.0);

        // kI: error times time adds up. Adding before or after computing the output are both fine.
        FlywheelPid integral = new FlywheelPid(0, 0, 2.0, 0);
        double first = integral.calculate(100, 90, DT);
        double second = integral.calculate(100, 90, DT);
        if (Math.abs(first - 0.4) > 0.001 && Math.abs(first) > 0.001) {
            fail("kI: new FlywheelPid(0, 0, 2, 0).calculate(100, 90, 0.02) returned " + first + ". kI x error x seconds is 0.4.");
        }
        close("kI should keep adding up: the second identical call changes the output by 0.4, the change was", second - first, 0.4);

        FlywheelPid again = new FlywheelPid(0, 0, 2.0, 0);
        again.calculate(100, 90, DT);
        again.calculate(100, 90, DT);
        again.reset();
        double after = again.calculate(100, 100, DT);
        close("reset() should forget the added-up error: after it, an error of 0 returned", after, 0.0);

        // kD: on the measurement, so changing the target doesn't kick the motor.
        FlywheelPid derivative = new FlywheelPid(0, 0, 0, 0.01);
        close("kD on the very first call (no change seen yet): calculate(100, 50, 0.02)", derivative.calculate(100, 50, DT), 0.0);
        close("kD: the wheel sped up by 10 in 0.02 s: calculate(100, 60, 0.02)", derivative.calculate(100, 60, DT), -5.0);
        close("kD must not kick when only the TARGET changes: calculate(300, 60, 0.02)", derivative.calculate(300, 60, DT), 0.0);
        derivative.reset();
        close("reset() should forget the last speed: the next calculate(100, 200, 0.02)", derivative.calculate(100, 200, DT), 0.0);

        double zero = new FlywheelPid(0.01, 0.1, 0.1, 0.1).calculate(100, 50, 0.0);
        if (Double.isNaN(zero) || Double.isInfinite(zero)) {
            fail("calculate(100, 50, 0.0) returned " + zero + ". A time step of 0 must not divide by zero.");
        }
    }

    static void flight() {
        // A long spin-up at full power: without anti-windup the added-up error overshoots badly.
        Run windup = fly(new FlywheelPid(0.0187, 0.03, 0.4, 0), 300, 4.0, 0, 0);
        if (windup.peak > 318) {
            fail("Spinning up to 300 with kI 0.4, the wheel peaked at " + round(windup.peak) + ". That is windup: only add up the error when it is small (within 30 rad/s of the target), and not while the output is stuck at 12 volts.");
        }
        // kV wrong on purpose: only the I term can remove the leftover error.
        Run wrong_kv = fly(new FlywheelPid(0.015, 0.03, 0.4, 0), 300, 8.0, 0, 0);
        if (Math.abs(wrong_kv.last - 300) > 1.0) {
            fail("With a kV that is too small, the wheel settled at " + round(wrong_kv.last) + " instead of 300. The I term should remove that error.");
        }
        // A launch at 4 s: back within 3% in time, and settled afterward.
        Run launch = fly(new FlywheelPid(0.0187, 0.03, 0.4, 0), 300, 8.0, 4.0, 25.0);
        if (launch.recovery < 0 || launch.recovery > 1.2) {
            fail("After a launch the wheel took " + round(launch.recovery) + " s to get back within 3% of its speed (-1 means it never did).");
        }
        if (Math.abs(launch.last - 300) > 1.0) {
            fail("After a launch the wheel settled at " + round(launch.last) + " instead of 300.");
        }
    }

    static class Run {
        double peak = 0;
        double last = 0;
        double recovery = -1;
    }

    static Run fly(FlywheelPid pid, double target, double seconds, double launch_at, double load_torque) {
        Run run = new Run();
        double speed = 0.0;
        boolean launched = false;
        boolean dipped = false;
        double launch_time = 0.0;
        for (int i = 0; i * DT < seconds; i++) {
            double now = i * DT;
            double volts = pid.calculate(target, speed, DT);
            if (Double.isNaN(volts)) {
                fail("calculate(...) returned NaN while flying the flywheel model.");
            }
            volts = Math.max(-12.0, Math.min(12.0, volts));
            double motor_speed = speed * GEAR;
            double torque = GEAR * KT * (volts - KE * motor_speed) / R;
            if (load_torque > 0 && !launched && now >= launch_at) {
                launched = true;
                launch_time = now;
                // The launch is one loop of hard push back, as in the robot's simulator.
                torque -= load_torque;
            }
            speed += torque / INERTIA * DT;
            run.peak = launched ? run.peak : Math.max(run.peak, speed);
            run.last = speed;
            if (launched && speed < target * 0.97) {
                dipped = true;
            }
            if (launched && dipped && run.recovery < 0 && speed >= target * 0.97) {
                run.recovery = now - launch_time;
            }
        }
        return run;
    }

    static void close(String what, double actual, double expected) {
        if (Math.abs(actual - expected) > 0.001) {
            fail(what + " returned " + actual + ", expected " + expected + ".");
        }
    }

    static String round(double value) {
        return String.valueOf(Math.round(value * 10.0) / 10.0);
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }
}
