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

    public void configureBindings() {
        buttons_.onTrue(controller_::a, intake_::run);
        buttons_.onTrue(controller_::b, intake_::stop);
        buttons_.onTrue(controller_::x, () -> intake_.setPower(EJECT_POWER));
    }

    public DoubleSupplier driveSpeed() {
        return () -> stick_filter_.apply(-controller_.getLeftY());
    }

    // One loop of the robot. This one is finished.
    public void periodic() {
        buttons_.poll();
        drivetrain_.drive(driveSpeed().getAsDouble());
    }
}
