public class TotalAmpsCheck {
    // Parts the lesson never mentions: totalAmps has to work for any PowerUser.
    static class Fan implements PowerUser {
        public String getName() { return "Fan"; }
        public double getAmps() { return 2.5; }
    }

    static class Pump implements PowerUser {
        public String getName() { return "Pump"; }
        public double getAmps() { return 12.0; }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        PowerUser[] parts = { new Fan(), new Pump(), new Fan() };
        double total = PowerBudget.totalAmps(parts);
        if (Math.abs(total - 17.0) > 0.0001) {
            fail("totalAmps of a 2.5 A fan, a 12 A pump and another 2.5 A fan was " + total + ", expected 17.0.");
        }

        PowerUser[] one = { new Pump() };
        total = PowerBudget.totalAmps(one);
        if (Math.abs(total - 12.0) > 0.0001) {
            fail("totalAmps of a single 12 A pump was " + total + ", expected 12.0.");
        }

        total = PowerBudget.totalAmps(new PowerUser[0]);
        if (Math.abs(total) > 0.0001) {
            fail("totalAmps of an empty array was " + total + ", expected 0.0.");
        }

        System.out.println("totalAmps adds up every kind of PowerUser.");
    }
}
