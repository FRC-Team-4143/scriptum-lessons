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

    // TODO: make slots_ an array with room for capacity pieces.
    public IndexerQueue(int capacity) {
    }

    // How many pieces are loaded.
    public int size() {
        return count_;
    }

    // TODO: true when every slot is full.
    public boolean isFull() {
        return false;
    }

    // TODO: put a piece in the first free slot behind the others and return
    //   true. If the indexer is full, change nothing and return false.
    //   When you reach the end of the array, wrap round to slot 0.
    public boolean load(String piece) {
        return false;
    }

    // TODO: take the piece at the front out and return it, or return "empty"
    //   when nothing is loaded.
    public String fire() {
        return "empty";
    }
}
