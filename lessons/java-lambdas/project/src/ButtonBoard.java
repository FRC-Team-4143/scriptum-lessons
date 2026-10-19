import java.util.ArrayList;
import java.util.function.BooleanSupplier;

public class ButtonBoard {
    // An ArrayList is an array that grows: bindings_.add(x) puts x on the end,
    // and for (Binding binding : bindings_) visits each one, like an array.
    protected final ArrayList<Binding> bindings_ = new ArrayList<>();

    // TODO: remember this pair by adding a new Binding to bindings_.
    //   Don't run the action here! It runs later, from poll().
    public void onTrue(BooleanSupplier condition, Runnable action) {
    }

    // Called once every robot loop.
    // TODO: for each binding, ask its condition_ for its value right now
    //   (getAsBoolean()). If it is true now but was_true_ is false, the button
    //   was just pressed: run its action_ (run()). Either way, save the value
    //   in was_true_ for next time.
    public void poll() {
    }
}
