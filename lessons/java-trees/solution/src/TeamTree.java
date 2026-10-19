// Every team at the event, kept in a binary search tree so we can find a
// team fast and list them all in order.
//
// You will want helper methods that take a TeamNode and call themselves on
// node.left_ and node.right_. Add them below; every public method here can
// just call its helper on root_.
import java.util.ArrayList;

public class TeamTree {
    protected TeamNode root_; // null while the tree is empty

    public void insert(int team) {
        root_ = insertBelow(root_, team);
    }

    private TeamNode insertBelow(TeamNode node, int team) {
        if (node == null) {
            return new TeamNode(team);
        }
        if (team < node.team_) {
            node.left_ = insertBelow(node.left_, team);
        } else if (team > node.team_) {
            node.right_ = insertBelow(node.right_, team);
        }
        return node;
    }

    public boolean contains(int team) {
        return containsBelow(root_, team);
    }

    private boolean containsBelow(TeamNode node, int team) {
        if (node == null) {
            return false;
        }
        if (team == node.team_) {
            return true;
        }
        if (team < node.team_) {
            return containsBelow(node.left_, team);
        }
        return containsBelow(node.right_, team);
    }

    public int size() {
        return sizeBelow(root_);
    }

    private int sizeBelow(TeamNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + sizeBelow(node.left_) + sizeBelow(node.right_);
    }

    public ArrayList<Integer> inOrder() {
        ArrayList<Integer> teams = new ArrayList<>();
        addInOrder(root_, teams);
        return teams;
    }

    private void addInOrder(TeamNode node, ArrayList<Integer> teams) {
        if (node == null) {
            return;
        }
        addInOrder(node.left_, teams);
        teams.add(node.team_);
        addInOrder(node.right_, teams);
    }

    public int height() {
        return heightBelow(root_);
    }

    private int heightBelow(TeamNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(heightBelow(node.left_), heightBelow(node.right_));
    }
}
