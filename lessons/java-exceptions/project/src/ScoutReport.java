import java.util.ArrayList;

// What came out of a pile of scouting text: the entries that were good, and
// the line numbers of the ones that weren't. This file is already finished.
public class ScoutReport {
    protected ArrayList<ScoutEntry> entries_ = new ArrayList<>();
    protected ArrayList<Integer> bad_lines_ = new ArrayList<>();

    public void addEntry(ScoutEntry entry) {
        entries_.add(entry);
    }

    public void addBadLine(int line_number) {
        bad_lines_.add(line_number);
    }

    public ArrayList<ScoutEntry> getEntries() {
        return entries_;
    }

    public ArrayList<Integer> getBadLines() {
        return bad_lines_;
    }

    public int goodCount() {
        return entries_.size();
    }

    public int badCount() {
        return bad_lines_.size();
    }
}
