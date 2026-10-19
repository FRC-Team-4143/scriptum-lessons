// One dashboard setting (say, the driver's max speed) with an Undo button.
// Every time the drive coach changes it, the old value is saved, so Undo can
// go back one change at a time: the newest change is undone first.
import java.util.ArrayDeque;

public class SettingHistory {
    protected double current_;
    protected ArrayDeque<Double> history_ = new ArrayDeque<>();

    public SettingHistory(double start) {
        current_ = start;
    }

    // The value the setting has right now.
    public double current() {
        return current_;
    }

    public void set(double value) {
        history_.push(current_);
        current_ = value;
    }

    public void undo() {
        if (!history_.isEmpty()) {
            current_ = history_.pop();
        }
    }

    public int undoCount() {
        return history_.size();
    }
}
