// Something that changes a motor speed: give it a speed, get a new speed back.
@FunctionalInterface
public interface SpeedFilter {
    double apply(double speed);
}
