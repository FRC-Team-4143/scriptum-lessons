// Every team at the event, kept in a binary search tree so we can find a
// team fast and list them all in order.
//
// You will want helper methods that take a TeamNode and call themselves on
// node.left_ and node.right_. Add them below; every public method here can
// just call its helper on root_.
import java.util.ArrayList;

public class TeamTree {
    protected TeamNode root_; // null while the tree is empty

    // TODO: add the team where it belongs: lower numbers go left, higher
    //   numbers go right. A team that is already in the tree is not added
    //   again.
    public void insert(int team) {
    }

    // TODO: true if the team is in the tree. Only look down the side it
    //   could be on.
    public boolean contains(int team) {
        return false;
    }

    // TODO: how many teams are in the tree.
    public int size() {
        return 0;
    }

    // TODO: every team in the tree, lowest number first. Visit the left
    //   side, then the node itself, then the right side.
    public ArrayList<Integer> inOrder() {
        return new ArrayList<>();
    }

    // TODO: how many nodes are on the longest path from the root down to
    //   the bottom. An empty tree has height 0; a tree with one team has
    //   height 1.
    public int height() {
        return 0;
    }
}
