// One node of an autonomous routine shaped like a tree, the same shape as
// the behavior trees our robot runs. A node with children (like a Sequence
// that runs them in order) is a group. A node with no children (like
// "shoot") is an action: the part that actually makes the robot move.
import java.util.ArrayList;

public class AutoNode {
    protected String name_;
    protected ArrayList<AutoNode> children_ = new ArrayList<>();

    public AutoNode(String name) {
        name_ = name;
    }

    // Adds a child and hands back this node, so calls can be chained:
    //   new AutoNode("Sequence").add(a).add(b)
    public AutoNode add(AutoNode child) {
        children_.add(child);
        return this;
    }

    public String getName() {
        return name_;
    }

    // TODO: how many actions (nodes with no children) are in this tree,
    //   counting this node if it is one.
    public int countActions() {
        return 0;
    }

    // TODO: how many nodes are on the longest path from this node down to
    //   an action. An action on its own has depth 1.
    public int depth() {
        return 0;
    }
}
