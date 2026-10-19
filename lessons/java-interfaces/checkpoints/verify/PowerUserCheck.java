public class PowerUserCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        if (!PowerUser.class.isInterface()) {
            fail("PowerUser should be declared as \"public interface PowerUser\", not a class.");
        }

        PowerUser motor = new Motor("Left drive", 40.0);
        if (!"Left drive".equals(motor.getName())) {
            fail("new Motor(\"Left drive\", 40.0).getName() was \"" + motor.getName() + "\", expected \"Left drive\".");
        }
        if (Math.abs(motor.getAmps() - 40.0) > 0.0001) {
            fail("new Motor(\"Left drive\", 40.0).getAmps() was " + motor.getAmps() + ", expected 40.0.");
        }

        PowerUser leds = new LedStrip(60);
        if (!"LED strip".equals(leds.getName())) {
            fail("LedStrip's getName() was \"" + leds.getName() + "\", expected \"LED strip\".");
        }
        if (Math.abs(leds.getAmps() - 1.2) > 0.0001) {
            fail("new LedStrip(60).getAmps() was " + leds.getAmps() + ", expected 1.2 (60 LEDs x 0.02 A).");
        }
        PowerUser short_strip = new LedStrip(10);
        if (Math.abs(short_strip.getAmps() - 0.2) > 0.0001) {
            fail("new LedStrip(10).getAmps() was " + short_strip.getAmps() + ", expected 0.2 (10 LEDs x 0.02 A).");
        }

        System.out.println("Motor and LedStrip are both PowerUsers.");
    }
}
