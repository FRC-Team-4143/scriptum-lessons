// TODO: add "extends Mechanism" to the class declaration below.
public class Intake {
    public static final double RUNNING_AMPS = 30.0;

    protected boolean running_ = false;

    // TODO: constructor Intake(String name) - its first line is super(name).

    public void setRunning(boolean running) {
        running_ = running;
    }

    // TODO: getAmps() returns RUNNING_AMPS while running_ is true, and
    //   0.0 otherwise. Don't write getName() - Mechanism already has it.
}
