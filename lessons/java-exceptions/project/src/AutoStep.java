// One step of an autonomous routine. If the gyro is unplugged, run() starts the
// drivetrain and then throws, just like real code that can't read a sensor.
// This file is already finished.
public class AutoStep {
    protected String name_;
    protected double speed_;
    protected boolean gyro_unplugged_;

    public AutoStep(String name, double speed, boolean gyro_unplugged) {
        name_ = name;
        speed_ = speed;
        gyro_unplugged_ = gyro_unplugged;
    }

    public String getName() {
        return name_;
    }

    public void run(Drivetrain drive) {
        drive.setSpeed(speed_);
        if (gyro_unplugged_) {
            throw new IllegalStateException(name_ + ": the gyro is unplugged");
        }
    }
}
