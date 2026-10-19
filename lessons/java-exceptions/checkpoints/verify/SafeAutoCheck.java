public class SafeAutoCheck {
    // Steps the lesson never mentions, so runStep has to work for any step.
    static class Crash extends AutoStep {
        private final RuntimeException problem_;

        Crash(double speed, RuntimeException problem) {
            super("crash", speed, false);
            problem_ = problem;
        }

        @Override
        public void run(Drivetrain drive) {
            drive.setSpeed(speed_);
            throw problem_;
        }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expect(AutoStep step, boolean want, String what) {
        Drivetrain drive = new Drivetrain();
        boolean got;
        try {
            got = SafeAuto.runStep(step, drive);
        } catch (RuntimeException e) {
            fail("runStep with " + what + " didn't catch the " + e.getClass().getSimpleName()
                + " it threw. Catch it and return false.");
            return;
        }
        if (got != want) {
            fail("runStep with " + what + " returned " + got + ", expected " + want + ".");
        }
        if (drive.getSpeed() != 0.0) {
            fail("After runStep with " + what + ", the drivetrain was still driving at " + drive.getSpeed()
                + ". Stop it in a finally block, whatever happens.");
        }
    }

    public static void main(String[] args) {
        expect(new AutoStep("forward", 0.6, false), true, "a step that finishes at speed 0.6");
        expect(new AutoStep("back up", -0.4, false), true, "a step that finishes at speed -0.4");
        expect(new AutoStep("turn", 0.3, true), false, "a step whose gyro is unplugged");
        expect(new Crash(0.8, new ArithmeticException("/ by zero")), false,
            "a step that divides by zero at speed 0.8");
        expect(new Crash(0.0, new NullPointerException("no camera")), false,
            "a step that crashes before it drives");

        System.out.println("runStep survives a crashing step and always stops the robot.");
    }
}
