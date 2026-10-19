public class BracketsCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expect(String text, boolean want, String why) {
        boolean got = AutoParser.isBalanced(text);
        if (got != want) {
            fail("isBalanced(\"" + text + "\") was " + got + ", expected " + want + why + ".");
        }
    }

    static void run() {
        expect("", true, " (an empty auto has nothing left open)");
        expect("shoot", true, " (no brackets at all)");
        expect("par{race(drive[1], wait[0.5]), lift}", true, "");
        expect("{[()()]}", true, "");
        expect("((", false, " (two brackets are never closed)");
        expect("drive)", false, " (a close with nothing open)");
        expect(")(", false, " (closed before it was opened)");
        expect("(]", false, " (a ( has to be closed by a ), not a ])");
        expect("seq{drive(1}", false, " (the ( is still open when the } comes)");
        expect("([)]", false, " (the newest bracket opened has to close first)");
        expect("[[intake]]]", false, " (one close too many)");
        expect("a(b[c{d}e]f)g", true, "");

        System.out.println("isBalanced catches every bracket typo.");
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) {
            fail("Your code crashed: " + e + ". Look for a pop, an index or a null that isn't checked first.");
        }
    }
}
