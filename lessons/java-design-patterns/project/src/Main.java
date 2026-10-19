public class Main {
    public static void main(String[] args) {
        // Strategy: one drivetrain, two ways to drive it.
        Gamepad pad = new Gamepad(0.6, 0.2, 0.5);
        Drivetrain drivetrain = new Drivetrain(new TankDrive());
        System.out.println(drivetrain.getModeName() + ": " + drivetrain.drive(pad));
        drivetrain.setMode(new ArcadeDrive());
        System.out.println(drivetrain.getModeName() + ": " + drivetrain.drive(pad));
        System.out.println();

        // Observer: the LEDs and the intake listen to the beam break.
        BeamBreak beam = new BeamBreak();
        beam.addListener(has_piece -> System.out.println(has_piece ? "  LEDs: green" : "  LEDs: off"));
        beam.addListener(has_piece -> {
            if (has_piece) {
                System.out.println("  Intake: stop, we have it");
            }
        });
        boolean[] readings = { false, false, true, true, true, false };
        for (int loop = 0; loop < readings.length; loop++) {
            System.out.println("Loop " + loop + ": beam blocked = " + readings[loop]);
            beam.update(readings[loop]);
        }
        System.out.println();

        // Factory: build whatever auto the dashboard picked.
        String[] picks = { "Two piece left", "  one PIECE ", "Spin in circles" };
        for (String pick : picks) {
            Auto auto = AutoFactory.create(pick);
            if (auto == null) {
                System.out.println("\"" + pick + "\" -> null (the factory should never return null)");
            } else {
                System.out.println("\"" + pick + "\" -> " + auto.getName() + ": " + auto.getSteps());
            }
        }
    }
}
