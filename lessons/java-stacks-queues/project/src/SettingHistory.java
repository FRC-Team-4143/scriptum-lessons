// One dashboard setting (say, the driver's max speed) with an Undo button.
// Every time the drive coach changes it, the old value is saved, so Undo can
// go back one change at a time: the newest change is undone first.
// TODO: import java.util.ArrayDeque; at the very top of this file.
public class SettingHistory {
    protected double current_;
    // TODO: a protected ArrayDeque<Double> field named history_ that starts as
    //   a new, empty deque: new ArrayDeque<>()

    public SettingHistory(double start) {
        current_ = start;
    }

    // The value the setting has right now.
    public double current() {
        return current_;
    }

    // TODO: save the value it has now on top of the history, then change it.
    public void set(double value) {
        current_ = value;
    }

    // TODO: go back to the value before the newest change. If there is no
    //   change left to undo, do nothing.
    public void undo() {
    }

    // TODO: how many changes Undo can still go back through.
    public int undoCount() {
        return 0;
    }
}
