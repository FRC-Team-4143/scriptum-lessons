public class AutoFactoryCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static String show(String name) {
        return name == null ? "null" : "\"" + name + "\"";
    }

    static void expect(String name, Class<?> type, String want_name) {
        Auto got = AutoFactory.create(name);
        if (got == null) {
            fail("create(" + show(name) + ") returned null. The factory should always return an Auto (Do nothing when in doubt).");
        }
        if (got.getClass() != type || !want_name.equals(got.getName())) {
            fail("create(" + show(name) + ") built \"" + got.getName() + "\", expected \"" + want_name + "\".");
        }
    }

    public static void main(String[] args) {
        expect("leave", LeaveAuto.class, "Leave");
        expect("one piece", OnePieceAuto.class, "One piece");
        expect("two piece right", TwoPieceAuto.class, "Two piece (right)");
        expect("two piece left", TwoPieceAuto.class, "Two piece (left)");
        expect("LEAVE", LeaveAuto.class, "Leave");
        expect("Two Piece Right", TwoPieceAuto.class, "Two piece (right)");
        expect("   one piece  ", OnePieceAuto.class, "One piece");
        expect("two piece middle", DoNothingAuto.class, "Do nothing");
        expect("leave now", DoNothingAuto.class, "Do nothing");
        expect("", DoNothingAuto.class, "Do nothing");
        expect("   ", DoNothingAuto.class, "Do nothing");
        expect(null, DoNothingAuto.class, "Do nothing");
        for (String name : AutoFactory.NAMES) {
            Auto auto = AutoFactory.create(name);
            if (auto == null || auto instanceof DoNothingAuto) {
                fail("create(\"" + name + "\") didn't build a real auto, but \"" + name + "\" is one of the dashboard's names.");
            }
        }

        if (AutoFactory.create("leave") == AutoFactory.create("leave")) {
            fail("create(\"leave\") handed back the same object twice. Build a new auto with new every time.");
        }

        System.out.println("The auto factory builds the right auto from its name.");
    }
}
