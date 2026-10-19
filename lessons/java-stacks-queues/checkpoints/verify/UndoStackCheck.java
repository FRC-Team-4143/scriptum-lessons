public class UndoStackCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expectValue(SettingHistory h, double want, String what) {
        double got = h.current();
        if (Math.abs(got - want) > 1e-9) {
            fail(what + ": current() was " + got + ", expected " + want + ".");
        }
    }

    static void expectCount(SettingHistory h, int want, String what) {
        int got = h.undoCount();
        if (got != want) {
            fail(what + ": undoCount() was " + got + ", expected " + want + ".");
        }
    }

    static void run() {
        SettingHistory h = new SettingHistory(2.5);
        expectValue(h, 2.5, "A new history starting at 2.5");
        expectCount(h, 0, "A new history with no changes");
        h.undo();
        expectValue(h, 2.5, "Undo with nothing to undo (it should change nothing)");
        expectCount(h, 0, "Undo with nothing to undo");

        h.set(1.0);
        h.set(7.25);
        h.set(-3.0);
        expectValue(h, -3.0, "After set(1.0), set(7.25), set(-3.0)");
        expectCount(h, 3, "After three changes");
        h.undo();
        expectValue(h, 7.25, "After three changes and one undo (the newest change is undone first)");
        h.undo();
        expectValue(h, 1.0, "After three changes and two undos");
        expectCount(h, 1, "After three changes and two undos");

        h.set(9.0);
        expectValue(h, 9.0, "Setting 9.0 after undoing");
        h.undo();
        expectValue(h, 1.0, "Undoing that 9.0");
        h.undo();
        expectValue(h, 2.5, "Undoing back to the very start");
        h.undo();
        h.undo();
        expectValue(h, 2.5, "Extra undos past the start (they should change nothing)");
        expectCount(h, 0, "Extra undos past the start");

        SettingHistory zero = new SettingHistory(0.0);
        zero.set(0.0);
        zero.set(0.0);
        expectCount(zero, 2, "Setting the same value 0.0 twice (each set is still a change)");

        System.out.println("Undo goes back one change at a time, newest first.");
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) {
            fail("Your code crashed: " + e + ". Look for a pop, an index or a null that isn't checked first.");
        }
    }
}
