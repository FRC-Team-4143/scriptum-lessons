// TODO: add "implements PowerUser" to the class declaration below.
public class LedStrip {
    public static final double AMPS_PER_LED = 0.02;

    protected int led_count_;

    public LedStrip(int led_count) {
        led_count_ = led_count;
    }

    // TODO: getName() returns "LED strip".
    // TODO: getAmps() returns AMPS_PER_LED for every LED in the strip.
}
