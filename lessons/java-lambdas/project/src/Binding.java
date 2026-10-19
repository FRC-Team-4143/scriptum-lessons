import java.util.function.BooleanSupplier;

// One button binding: when to act, what to do, and whether the condition was
// true the last time ButtonBoard checked it. This one is finished.
public class Binding {
    public final BooleanSupplier condition_;
    public final Runnable action_;
    public boolean was_true_ = false;

    public Binding(BooleanSupplier condition, Runnable action) {
        condition_ = condition;
        action_ = action;
    }
}
