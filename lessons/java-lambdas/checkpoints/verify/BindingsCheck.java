public class BindingsCheck {
    // A pretend button and counters the lesson never mentions:
    // ButtonBoard has to work for any condition and any action.
    static boolean button_ = false;
    static boolean other_ = false;
    static int presses_ = 0;
    static int other_presses_ = 0;

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void step(ButtonBoard board, boolean button, int want, String what) {
        button_ = button;
        board.poll();
        if (presses_ != want) {
            fail(what + ": the action had run " + presses_ + " times, expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        ButtonBoard board = new ButtonBoard();
        board.onTrue(() -> button_, () -> presses_++);
        if (presses_ != 0) {
            fail("onTrue ran the action right away. It should only remember it; poll() runs it later.");
        }

        step(board, false, 0, "Polling while the button is up");
        step(board, true, 1, "Pressing the button and polling");
        step(board, true, 1, "Holding the button down for another poll (it should run once per press, not every loop)");
        step(board, true, 1, "Still holding the button");
        step(board, false, 1, "Letting go of the button");
        step(board, true, 2, "Pressing the button a second time");

        // Two bindings on one board don't mix up their buttons or their memory.
        board.onTrue(() -> other_, () -> other_presses_++);
        other_ = true;
        step(board, true, 2, "Pressing a second button while the first is still held");
        if (other_presses_ != 1) {
            fail("A second onTrue binding ran " + other_presses_ + " times when its button was pressed, expected 1. Does poll() visit every binding?");
        }
        other_ = false;
        step(board, false, 2, "Letting go of both buttons");
        step(board, true, 3, "Pressing the first button again");
        if (other_presses_ != 1) {
            fail("Pressing the first button also ran the second binding's action. Each binding has its own condition_ and was_true_.");
        }

        // A button already held when the board first polls counts as a press.
        ButtonBoard fresh = new ButtonBoard();
        int before = presses_;
        fresh.onTrue(() -> true, () -> presses_++);
        fresh.poll();
        if (presses_ != before + 1) {
            fail("A condition that is already true on the first poll should run its action once.");
        }

        System.out.println("ButtonBoard runs each action once per press.");
    }
}
