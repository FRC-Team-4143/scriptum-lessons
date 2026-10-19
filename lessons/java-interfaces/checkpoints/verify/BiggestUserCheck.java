public class BiggestUserCheck {
    // A part the lesson never mentions: biggestUser has to work for any PowerUser.
    static class Part implements PowerUser {
        private final String name_;
        private final double amps_;

        Part(String name, double amps) {
            name_ = name;
            amps_ = amps;
        }

        public String getName() { return name_; }
        public double getAmps() { return amps_; }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expect(PowerUser[] parts, String want, String what) {
        String got = PowerBudget.biggestUser(parts);
        if (!want.equals(got)) {
            fail("biggestUser of " + what + " was \"" + got + "\", expected \"" + want + "\".");
        }
    }

    public static void main(String[] args) {
        expect(new PowerUser[] { new Part("Fan", 2.0), new Part("Arm", 25.0), new Part("Lights", 1.0) },
            "Arm", "a 2 A fan, a 25 A arm and 1 A lights");
        expect(new PowerUser[] { new Part("Climber", 60.0), new Part("Fan", 2.0) },
            "Climber", "a 60 A climber then a 2 A fan (the biggest is first)");
        expect(new PowerUser[] { new Part("Fan", 2.0), new Part("Shooter", 50.0) },
            "Shooter", "a 2 A fan then a 50 A shooter (the biggest is last)");
        expect(new PowerUser[] { new Part("Left", 40.0), new Part("Right", 40.0) },
            "Left", "two 40 A motors (a tie goes to the first one)");
        expect(new PowerUser[] { new Part("Idle", 0.0) },
            "Idle", "one part drawing 0 A");
        expect(new PowerUser[0], "none", "an empty array");

        System.out.println("biggestUser finds the hungriest part.");
    }
}
