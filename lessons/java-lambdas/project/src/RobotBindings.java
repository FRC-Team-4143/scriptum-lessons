import java.util.function.DoubleSupplier;

// Which button does what, and how the stick drives the robot.
public class RobotBindings {
    public static final double STICK_DEADBAND = 0.1;
    public static final double EJECT_POWER = -0.5;

    protected final Controller controller_;
    protected final Intake intake_;
    protected final Drivetrain drivetrain_;
    protected final ButtonBoard buttons_ = new ButtonBoard();
    protected final SpeedFilter stick_filter_ = Filters.deadband(STICK_DEADBAND);

    public RobotBindings(Controller controller, Intake intake, Drivetrain drivetrain) {
        controller_ = controller;
        intake_ = intake;
        drivetrain_ = drivetrain;
    }

    // TODO: three buttons_.onTrue(...) lines:
    //   A runs the intake        - method references: controller_::a, intake_::run
    //   B stops the intake       - controller_::b, intake_::stop
    //   X ejects at EJECT_POWER  - setPower needs a number, so use a lambda:
    //                              () -> intake_.setPower(EJECT_POWER)
    public void configureBindings() {
    }

    // TODO: replace this lambda (it always says 0.0) with one that reads the
    //   stick EVERY time it is called: flip the sign of controller_.getLeftY()
    //   (forward is negative) and pass it through stick_filter_.apply(...).
    public DoubleSupplier driveSpeed() {
        return () -> 0.0;
    }

    // One loop of the robot. This one is finished.
    public void periodic() {
        buttons_.poll();
        drivetrain_.drive(driveSpeed().getAsDouble());
    }
}
