// Turns the text our scouts type ("4143,12,38" = team, match, points) into
// ScoutEntry objects.
public class ScoutParser {
    public static ScoutEntry parseLine(String line, int line_number) throws BadLineException {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            throw new BadLineException("expected team,match,points but got \"" + line + "\"", line_number);
        }
        try {
            int team = Integer.parseInt(parts[0].trim());
            int match = Integer.parseInt(parts[1].trim());
            int points = Integer.parseInt(parts[2].trim());
            return new ScoutEntry(team, match, points);
        } catch (NumberFormatException e) {
            throw new BadLineException("not a whole number in \"" + line + "\"", line_number);
        } catch (IllegalArgumentException e) {
            throw new BadLineException(e.getMessage(), line_number);
        }
    }

    public static ScoutReport parseAll(String[] lines) {
        ScoutReport report = new ScoutReport();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) {
                continue;
            }
            try {
                report.addEntry(parseLine(lines[i], i + 1));
            } catch (BadLineException e) {
                report.addBadLine(i + 1);
            }
        }
        return report;
    }
}
