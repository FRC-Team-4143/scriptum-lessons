public class Main {
    public static void main(String[] args) {
        Intake intake = new Intake("Intake");
        intake.setRunning(true);

        // One array, four different kinds of part. It works because each of
        // them is a PowerUser.
        PowerUser[] robot = {
            new Motor("Left drive", 40.0),
            new Motor("Right drive", 40.0),
            new LedStrip(120),
            intake,
        };

        for (PowerUser user : robot) {
            System.out.println(user.getName() + ": " + user.getAmps() + " A");
        }
        System.out.println("Total: " + PowerBudget.totalAmps(robot) + " A");
        System.out.println("Biggest user: " + PowerBudget.biggestUser(robot));

        if (intake.isOverLimit(20.0)) {
            System.out.println("The intake is over its 20 A limit.");
        }
    }
}
