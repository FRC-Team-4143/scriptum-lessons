import java.util.ArrayList;

// Remembers only the last few things added, like the last 5 gyro readings.
// When it's full, adding one more throws away the oldest.
// TODO: make this generic: public class RingBuffer<T>
public class RingBuffer {
    protected int capacity_;
    // TODO: a field protected ArrayList<T> items_ = new ArrayList<>();

    public RingBuffer(int capacity) {
        capacity_ = capacity;
    }

    // TODO: add(T item). If the buffer already holds capacity_ items, remove
    //   the oldest (the one at index 0) first. Then add item at the end.

    // TODO: size() returns how many items it holds right now.

    // TODO: get(int index) returns an item, type T. Index 0 is the oldest one
    //   still kept.

    // TODO: latest() returns the newest item, type T, or null when it's empty.
}
