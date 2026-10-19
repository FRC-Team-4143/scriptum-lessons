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

    public int countActions() {
        if (children_.isEmpty()) {
            return 1;
        }
        int actions = 0;
        for (AutoNode child : children_) {
            actions += child.countActions();
        }
        return actions;
    }

    public int depth() {
        int deepest = 0;
        for (AutoNode child : children_) {
            deepest = Math.max(deepest, child.depth());
        }
        return 1 + deepest;
    }
}
