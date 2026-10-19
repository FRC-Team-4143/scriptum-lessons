public class BstInsertCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static TeamTree build(int... teams) {
        TeamTree tree = new TeamTree();
        for (int team : teams) {
            tree.insert(team);
        }
        return tree;
    }

    static void run() {
        TeamTree empty = new TeamTree();
        if (empty.size() != 0) {
            fail("A new, empty tree has size() " + empty.size() + ", expected 0.");
        }
        if (empty.contains(4143)) {
            fail("A new, empty tree says contains(4143) is true, expected false.");
        }

        TeamTree tree = build(500, 300, 800, 100, 400, 900, 650);
        if (tree.root_ == null || tree.root_.team_ != 500) {
            fail("After inserting 500 first, root_ should hold 500.");
        }
        if (tree.root_.left_ == null || tree.root_.left_.team_ != 300) {
            fail("After inserting 500 then 300, 300 should be root_.left_ (lower numbers go left).");
        }
        if (tree.root_.right_ == null || tree.root_.right_.team_ != 800) {
            fail("After inserting 500, 300 then 800, 800 should be root_.right_ (higher numbers go right).");
        }
        TeamNode n650 = tree.root_.right_.left_;
        if (n650 == null || n650.team_ != 650) {
            fail("After inserting 500, 300, 800, 100, 400, 900, 650, team 650 should be left of 800 (higher than 500, lower than 800).");
        }
        if (tree.size() != 7) {
            fail("After inserting 7 different teams, size() was " + tree.size() + ", expected 7.");
        }
        for (int team : new int[] {500, 300, 800, 100, 400, 900, 650}) {
            if (!tree.contains(team)) {
                fail("After inserting 500, 300, 800, 100, 400, 900, 650, contains(" + team + ") was false, expected true.");
            }
        }
        for (int team : new int[] {0, 99, 350, 501, 700, 1000}) {
            if (tree.contains(team)) {
                fail("contains(" + team + ") was true, but " + team + " was never inserted.");
            }
        }

        TeamTree dupes = build(42, 42, 7, 42, 7);
        if (dupes.size() != 2) {
            fail("After inserting 42, 42, 7, 42, 7, size() was " + dupes.size() + ", expected 2 (a team already in the tree isn't added again).");
        }

        System.out.println("Teams go left or right in the tree, and contains finds them.");
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
