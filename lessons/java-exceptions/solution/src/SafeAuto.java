public class SafeAuto {
    public static boolean runStep(AutoStep step, Drivetrain drive) {
        try {
            step.run(drive);
            return true;
        } catch (RuntimeException e) {
            return false;
        } finally {
            drive.stop();
        }
    }
}
