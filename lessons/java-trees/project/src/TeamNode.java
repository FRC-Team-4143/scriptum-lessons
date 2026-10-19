// One box in the team tree: a team number, plus links to two smaller trees.
// Every team in left_ has a lower number than team_, and every team in
// right_ has a higher one. A link that is null means "nothing there".
public class TeamNode {
    protected int team_;
    protected TeamNode left_;
    protected TeamNode right_;

    public TeamNode(int team) {
        team_ = team;
    }
}
