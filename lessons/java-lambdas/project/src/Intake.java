public class Intake {
    public static final double RUN_POWER = 1.0;

    protected double power_ = 0.0;

    public void run() {
        power_ = RUN_POWER;
    }

    public void stop() {
        power_ = 0.0;
    }

    public void setPower(double power) {
        power_ = power;
    }

    public double getPower() {
        return power_;
    }
}
