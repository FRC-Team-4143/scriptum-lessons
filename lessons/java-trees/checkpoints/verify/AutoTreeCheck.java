public class AutoTreeCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static AutoNode leaf(String name) {
        return new AutoNode(name);
    }

    static void expect(AutoNode tree, int actions, int depth, String what) {
        int got_actions = tree.countActions();
        if (got_actions != actions) {
            fail("countActions() on " + what + " was " + got_actions + ", expected " + actions + ".");
        }
        int got_depth = tree.depth();
        if (got_depth != depth) {
            fail("depth() on " + what + " was " + got_depth + ", expected " + depth + ".");
        }
    }

    static void run() {
        expect(leaf("score amp"), 1, 1, "a single action with no children");
        expect(new AutoNode("Sequence").add(leaf("a")).add(leaf("b")).add(leaf("c")).add(leaf("d")),
            4, 2, "a Sequence of 4 actions");
        expect(new AutoNode("Sequence").add(new AutoNode("Selector").add(new AutoNode("Parallel").add(leaf("climb")))),
            1, 4, "a chain Sequence > Selector > Parallel > climb");
        AutoNode lopsided = new AutoNode("Sequence")
            .add(leaf("drive"))
            .add(new AutoNode("Parallel")
                .add(leaf("intake"))
                .add(new AutoNode("Sequence").add(leaf("aim")).add(leaf("fire"))))
            .add(leaf("park"));
        expect(lopsided, 5, 4,
            "Sequence[drive, Parallel[intake, Sequence[aim, fire]], park] (count actions at every level, and the deepest branch sets the depth)");
        AutoNode wide = new AutoNode("Parallel")
            .add(new AutoNode("Sequence").add(leaf("x")))
            .add(new AutoNode("Sequence").add(leaf("y")).add(leaf("z")))
            .add(leaf("w"));
        expect(wide, 4, 3, "Parallel[Sequence[x], Sequence[y, z], w] (a group isn't an action, only nodes with no children are)");

        System.out.println("Your auto tree counts its actions and measures its depth.");
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
