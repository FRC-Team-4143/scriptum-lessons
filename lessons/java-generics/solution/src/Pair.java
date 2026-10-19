// Two values that belong together, like a team number and its nickname, or a
// distance and the flywheel speed for it.
public class Pair<A, B> {
    protected A first_;
    protected B second_;

    public Pair(A first, B second) {
        first_ = first;
        second_ = second;
    }

    public A getFirst() {
        return first_;
    }

    public B getSecond() {
        return second_;
    }
}
