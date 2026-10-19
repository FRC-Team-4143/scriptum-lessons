public class Main {
    public static void main(String[] args) {
        // Part 1: a filter is behavior kept in a variable.
        System.out.println("Gentle stick (deadband, then half speed):");
        SpeedFilter gentle = Filters.chain(Filters.deadband(0.1), Filters.half());
        show(gentle, 0.05);
        show(gentle, 0.4);
        show(gentle, -1.0);

        // Part 2: a pretend match. Each column is one loop of the robot.
        Controller controller = new Controller();
        Intake intake = new Intake();
        Drivetrain drivetrain = new Drivetrain();
        RobotBindings robot = new RobotBindings(controller, intake, drivetrain);
        robot.configureBindings();

        boolean[] a = { false, true, true, false, false, false, true };
        boolean[] b = { false, false, false, true, false, false, false };
        boolean[] x = { false, false, false, false, true, false, false };
        double[] stick = { 0.0, -0.6, -0.6, 0.05, 0.0, 0.8, -1.0 };

        System.out.println();
        System.out.println("Loop  Buttons  Stick   Drive   Intake");
        for (int i = 0; i < stick.length; i++) {
            controller.setA(a[i]);
            controller.setB(b[i]);
            controller.setX(x[i]);
            controller.setLeftY(stick[i]);
            robot.periodic();

            String buttons = (a[i] ? "A" : "-") + (b[i] ? "B" : "-") + (x[i] ? "X" : "-");
            System.out.println(String.format("%4d  %-7s  %5.2f   %5.2f   %5.2f",
                i + 1, buttons, stick[i], drivetrain.getSpeed(), intake.getPower()));
        }
    }

    static void show(SpeedFilter filter, double stick) {
        if (filter == null) {
            System.out.println("  (a Filters method returned null: not written yet)");
            return;
        }
        System.out.println("  " + stick + " -> " + filter.apply(stick));
    }
}
