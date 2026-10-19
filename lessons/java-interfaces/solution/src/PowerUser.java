// Anything on the robot that draws current from the battery.
public interface PowerUser {
    String getName();

    double getAmps();
}
