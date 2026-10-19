// Turns the text our scouts type ("4143,12,38" = team, match, points) into
// ScoutEntry objects.
public class ScoutParser {
    // TODO: turn one line like "4143,12,38" into a ScoutEntry.
    //   - Split it on commas with line.split(",") and trim() each piece.
    //   - Anything wrong (not exactly 3 pieces, a piece that isn't a whole
    //     number, or numbers ScoutEntry refuses) throws a BadLineException
    //     that carries line_number.
    //   - Integer.parseInt throws a NumberFormatException for text like "abc",
    //     and ScoutEntry throws an IllegalArgumentException. Catch them and
    //     throw a BadLineException instead.
    public static ScoutEntry parseLine(String line, int line_number) throws BadLineException {
        return null;
    }

    // TODO: read every line into one ScoutReport, without crashing.
    //   - Lines are numbered from 1: lines[0] is line 1.
    //   - Skip a blank line (only spaces, or nothing). It isn't bad, just empty.
    //   - A good line's entry goes in with report.addEntry(entry).
    //   - A bad line's number goes in with report.addBadLine(number), and
    //     then you keep going with the next line.
    public static ScoutReport parseAll(String[] lines) {
        return new ScoutReport();
    }
}
