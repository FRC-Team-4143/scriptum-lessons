public class Drivetrain {
    protected double speed_ = 0.0;

    public void drive(double speed) {
        speed_ = speed;
    }

    public double getSpeed() {
        return speed_;
    }
}
