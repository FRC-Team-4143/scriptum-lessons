// Thrown when a line of scouting text can't be read.
// TODO: make this a checked exception: change "extends RuntimeException"
//   to "extends Exception".
public class BadLineException extends RuntimeException {
    // TODO: a protected int line_number_ field.

    // TODO: pass the message on to Exception with super(message), and keep
    //   line_number in line_number_.
    public BadLineException(String message, int line_number) {
    }

    // TODO: return line_number_.
    public int getLineNumber() {
        return 0;
    }
}
