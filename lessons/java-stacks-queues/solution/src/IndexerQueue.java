// The indexer holds game pieces in a row of slots between the intake and the
// shooter. Pieces go in at the back and the shooter fires the one at the
// front, so the first piece loaded is the first one fired.
//
// Build this one by hand, with a plain array: no ArrayDeque, no ArrayList,
// nothing from java.util.
public class IndexerQueue {
    protected String[] slots_;
    protected int front_; // the slot holding the piece that fires next
    protected int count_; // how many pieces are loaded

    public IndexerQueue(int capacity) {
        slots_ = new String[capacity];
    }

    // How many pieces are loaded.
    public int size() {
        return count_;
    }

    public boolean isFull() {
        return count_ == slots_.length;
    }

    public boolean load(String piece) {
        if (isFull()) {
            return false;
        }
        int back = (front_ + count_) % slots_.length;
        slots_[back] = piece;
        count_++;
        return true;
    }

    public String fire() {
        if (count_ == 0) {
            return "empty";
        }
        String piece = slots_[front_];
        slots_[front_] = null;
        front_ = (front_ + 1) % slots_.length;
        count_--;
        return piece;
    }
}
