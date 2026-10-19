public class LedStrip implements PowerUser {
    public static final double AMPS_PER_LED = 0.02;

    protected int led_count_;

    public LedStrip(int led_count) {
        led_count_ = led_count;
    }

    @Override
    public String getName() {
        return "LED strip";
    }

    @Override
    public double getAmps() {
        return led_count_ * AMPS_PER_LED;
    }
}
