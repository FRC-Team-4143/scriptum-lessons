// Thrown when a line of scouting text can't be read.
public class BadLineException extends Exception {
    protected int line_number_;

    public BadLineException(String message, int line_number) {
        super(message);
        line_number_ = line_number;
    }

    public int getLineNumber() {
        return line_number_;
    }
}
