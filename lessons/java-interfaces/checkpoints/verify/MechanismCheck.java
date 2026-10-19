import java.lang.reflect.Modifier;

public class MechanismCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        if (!Modifier.isAbstract(Mechanism.class.getModifiers())) {
            fail("Mechanism should be \"public abstract class Mechanism\".");
        }
        if (!PowerUser.class.isAssignableFrom(Mechanism.class)) {
            fail("Mechanism should implement PowerUser.");
        }
        try {
            Intake.class.getDeclaredMethod("getName");
            fail("Intake has its own getName(). Delete it: Intake gets getName() from Mechanism.");
        } catch (NoSuchMethodException expected) {
            // Good: the name code lives in Mechanism only.
        }

        Intake intake = new Intake("Front intake");
        Mechanism mechanism = intake;
        PowerUser user = intake;
        if (!"Front intake".equals(user.getName())) {
            fail("new Intake(\"Front intake\").getName() was \"" + user.getName() + "\", expected \"Front intake\".");
        }
        if (Math.abs(user.getAmps()) > 0.0001) {
            fail("A new Intake isn't running yet, so getAmps() should be 0.0, but it was " + user.getAmps() + ".");
        }
        intake.setRunning(true);
        if (Math.abs(user.getAmps() - 30.0) > 0.0001) {
            fail("A running Intake's getAmps() was " + user.getAmps() + ", expected RUNNING_AMPS (30.0).");
        }
        if (!mechanism.isOverLimit(20.0)) {
            fail("A running intake draws 30 A, so isOverLimit(20.0) should be true.");
        }
        if (mechanism.isOverLimit(40.0)) {
            fail("A running intake draws 30 A, so isOverLimit(40.0) should be false.");
        }
        intake.setRunning(false);
        if (Math.abs(user.getAmps()) > 0.0001) {
            fail("After setRunning(false), getAmps() should be 0.0 again, but it was " + user.getAmps() + ".");
        }

        System.out.println("Mechanism shares its code, and Intake fills in the rest.");
    }
}
