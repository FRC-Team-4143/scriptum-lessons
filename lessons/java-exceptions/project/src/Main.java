public class Main {
    public static void main(String[] args) {
        // What our scouts typed during the first matches. Some lines have typos.
        String[] lines = {
            "4143,1,42",
            "254,1,55",
            "",
            "1678,one,30",
            "118,2",
            "4143,2,-5",
            "971, 2 , 61",
        };

        ScoutReport report = ScoutParser.parseAll(lines);
        System.out.println("Read " + report.goodCount() + " good lines:");
        for (ScoutEntry entry : report.getEntries()) {
            System.out.println("  " + entry);
        }
        System.out.println("Skipped " + report.badCount() + " bad lines: " + report.getBadLines());

        try {
            ScoutParser.parseLine("4143;3;40", 8);
            System.out.println("Line 8 was fine.");
        } catch (BadLineException e) {
            System.out.println("Line " + e.getLineNumber() + " is bad: " + e.getMessage());
        }

        Drivetrain drive = new Drivetrain();
        AutoStep[] auto = {
            new AutoStep("Drive to the reef", 0.5, false),
            new AutoStep("Turn to the tag", 0.3, true),
        };
        for (AutoStep step : auto) {
            boolean worked = SafeAuto.runStep(step, drive);
            System.out.println(step.getName() + ": " + (worked ? "done" : "FAILED")
                + ", drive speed is now " + drive.getSpeed());
        }
    }
}
