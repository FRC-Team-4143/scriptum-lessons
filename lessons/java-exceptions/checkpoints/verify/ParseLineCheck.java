public class ParseLineCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    // Checkpoint 2 may not be finished yet, so only check the line number once
    // BadLineException can carry one.
    static boolean lineNumbersWork() {
        return new BadLineException("x", 5).getLineNumber() == 5;
    }

    static void expectGood(String line, int team, int match, int points) {
        String call = "parseLine(\"" + line + "\", 1)";
        ScoutEntry entry;
        try {
            entry = ScoutParser.parseLine(line, 1);
        } catch (BadLineException e) {
            fail(call + " threw a BadLineException (" + e.getMessage() + "), but that line is fine.");
            return;
        } catch (RuntimeException e) {
            fail(call + " threw " + e.getClass().getSimpleName() + ", but that line is fine.");
            return;
        }
        if (entry == null) {
            fail(call + " returned null, expected a ScoutEntry for team " + team + ".");
        }
        if (entry.getTeam() != team || entry.getMatch() != match || entry.getPoints() != points) {
            fail(call + " gave team " + entry.getTeam() + ", match " + entry.getMatch() + ", points "
                + entry.getPoints() + ". Expected team " + team + ", match " + match + ", points " + points + ".");
        }
    }

    static void expectBad(String line, int line_number, String why) {
        String call = "parseLine(\"" + line + "\", " + line_number + ")";
        try {
            ScoutEntry entry = ScoutParser.parseLine(line, line_number);
            fail(call + " returned " + entry + ", but " + why + ", so it should throw a BadLineException.");
        } catch (BadLineException e) {
            if (lineNumbersWork() && e.getLineNumber() != line_number) {
                fail(call + " threw a BadLineException for line " + e.getLineNumber() + ", expected line " + line_number + ".");
            }
        } catch (RuntimeException e) {
            fail(call + " didn't catch the " + e.getClass().getSimpleName() + " it ran into. Catch it and throw a BadLineException instead.");
        }
    }

    public static void main(String[] args) {
        expectGood("4143,12,38", 4143, 12, 38);
        expectGood("254, 3 ,0", 254, 3, 0);
        expectGood(" 118,77, 102 ", 118, 77, 102);

        expectBad("", 1, "it's empty");
        expectBad("4143,12", 2, "it has only 2 pieces");
        expectBad("4143,12,38,9", 3, "it has 4 pieces");
        expectBad("4143;12;38", 4, "it uses semicolons, not commas");
        expectBad("abc,12,38", 5, "\"abc\" isn't a team number");
        expectBad("4143,12,3.5", 6, "3.5 isn't a whole number");
        expectBad("4143,twelve,38", 7, "\"twelve\" isn't a whole number");

        // ScoutEntry's own checks come from checkpoint 1. Once it refuses bad
        // numbers, parseLine has to turn that into a BadLineException too.
        boolean entryChecks;
        try {
            new ScoutEntry(4143, 0, 10);
            entryChecks = false;
        } catch (IllegalArgumentException e) {
            entryChecks = true;
        }
        if (entryChecks) {
            expectBad("4143,0,10", 8, "there is no match 0");
            expectBad("4143,9,-4", 9, "a team can't score -4 points");
        }

        System.out.println("parseLine reads good lines and throws BadLineException for bad ones.");
    }
}
