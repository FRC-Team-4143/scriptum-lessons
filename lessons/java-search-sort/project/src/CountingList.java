import java.util.AbstractList;
import java.util.List;

// A list of numbers that counts how many times code looks at one of its
// items. Main uses it to show how many steps each method takes.
// You don't need to change this file.
//
// One "look" is reading an item with get(i), a for-each loop, contains(),
// and so on. Reading the same item twice in a row counts once.
public class CountingList extends AbstractList<Integer> {
    private final List<Integer> items_;
    private long looks_ = 0;
    private int last_index_ = -1;

    public CountingList(List<Integer> items) {
        items_ = items;
    }

    @Override
    public Integer get(int index) {
        if (index != last_index_) {
            looks_++;
            last_index_ = index;
        }
        return items_.get(index);
    }

    @Override
    public int size() {
        return items_.size();
    }

    public long getLooks() {
        return looks_;
    }

    public void resetLooks() {
        looks_ = 0;
        last_index_ = -1;
    }
}
