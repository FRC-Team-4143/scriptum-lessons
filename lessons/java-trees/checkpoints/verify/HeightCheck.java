public class HeightCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expect(int[] teams, int want, String why) {
        TeamTree tree = new TeamTree();
        for (int team : teams) {
            tree.insert(team);
        }
        int got = tree.height();
        if (got != want) {
            fail("height() after inserting " + java.util.Arrays.toString(teams) + " was " + got + ", expected " + want + why + ".");
        }
    }

    static void run() {
        expect(new int[] {}, 0, " (an empty tree)");
        expect(new int[] {604}, 1, " (one team is one level)");
        expect(new int[] {50, 25, 75}, 2, "");
        expect(new int[] {50, 25, 75, 10, 30, 60, 90}, 3, " (a full tree of 7 is 3 levels)");
        expect(new int[] {10, 20, 30, 40, 50, 60}, 6, " (inserting in order makes one long chain)");
        expect(new int[] {50, 40, 60, 30, 20, 10}, 5, " (the longest side counts, here the left)");
        expect(new int[] {50, 10, 60, 70, 80, 90, 55}, 5, " (the longest side counts, here the right)");
        expect(new int[] {50, 50, 50}, 1, " (repeats aren't added)");

        System.out.println("height counts the levels down the longest path.");
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (StackOverflowError e) {
            fail("Your code called itself forever (a StackOverflowError). Every recursive method needs a base case that stops, like a null node.");
        } catch (RuntimeException e) {
            fail("Your code crashed: " + e + ". Is there a null node you use before checking it?");
        }
    }
}
