import java.util.ArrayList;
import java.util.List;

public class InOrderCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expect(int[] teams, List<Integer> want) {
        TeamTree tree = new TeamTree();
        for (int team : teams) {
            tree.insert(team);
        }
        ArrayList<Integer> got = tree.inOrder();
        if (!want.equals(got)) {
            fail("inOrder() after inserting " + java.util.Arrays.toString(teams) + " was " + got + ", expected " + want + ".");
        }
    }

    static void run() {
        expect(new int[] {}, List.of());
        expect(new int[] {2056}, List.of(2056));
        expect(new int[] {60, 20, 90, 10, 30, 80, 95}, List.of(10, 20, 30, 60, 80, 90, 95));
        expect(new int[] {5, 4, 3, 2, 1}, List.of(1, 2, 3, 4, 5));
        expect(new int[] {1, 2, 3, 4, 5}, List.of(1, 2, 3, 4, 5));
        expect(new int[] {3000, 17, 3000, 17, 900}, List.of(17, 900, 3000));

        TeamTree tree = new TeamTree();
        tree.insert(8);
        tree.inOrder();
        tree.insert(3);
        ArrayList<Integer> second = tree.inOrder();
        if (!List.of(3, 8).equals(second)) {
            fail("Calling inOrder() twice: after inserting 8, calling inOrder(), then inserting 3, inOrder() was " + second + ", expected [3, 8]. Start each call with a new list.");
        }

        System.out.println("inOrder lists every team, lowest number first.");
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
