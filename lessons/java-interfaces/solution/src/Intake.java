public class Intake extends Mechanism {
    public static final double RUNNING_AMPS = 30.0;

    protected boolean running_ = false;

    public Intake(String name) {
        super(name);
    }

    public void setRunning(boolean running) {
        running_ = running;
    }

    @Override
    public double getAmps() {
        if (running_) {
            return RUNNING_AMPS;
        }
        return 0.0;
    }
}
