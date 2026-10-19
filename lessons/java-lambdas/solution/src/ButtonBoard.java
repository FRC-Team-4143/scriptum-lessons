import java.util.ArrayList;
import java.util.function.BooleanSupplier;

public class ButtonBoard {
    // An ArrayList is an array that grows: bindings_.add(x) puts x on the end,
    // and for (Binding binding : bindings_) visits each one, like an array.
    protected final ArrayList<Binding> bindings_ = new ArrayList<>();

    public void onTrue(BooleanSupplier condition, Runnable action) {
        bindings_.add(new Binding(condition, action));
    }

    // Called once every robot loop.
    public void poll() {
        for (Binding binding : bindings_) {
            boolean now = binding.condition_.getAsBoolean();
            if (now && !binding.was_true_) {
                binding.action_.run();
            }
            binding.was_true_ = now;
        }
    }
}
