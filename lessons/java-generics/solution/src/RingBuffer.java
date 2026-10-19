import java.util.ArrayList;

// Remembers only the last few things added, like the last 5 gyro readings.
// When it's full, adding one more throws away the oldest.
public class RingBuffer<T> {
    protected int capacity_;
    protected ArrayList<T> items_ = new ArrayList<>();

    public RingBuffer(int capacity) {
        capacity_ = capacity;
    }

    public void add(T item) {
        if (items_.size() == capacity_) {
            items_.remove(0);
        }
        items_.add(item);
    }

    public int size() {
        return items_.size();
    }

    public T get(int index) {
        return items_.get(index);
    }

    public T latest() {
        if (items_.isEmpty()) {
            return null;
        }
        return items_.get(items_.size() - 1);
    }
}
