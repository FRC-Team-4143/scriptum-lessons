public class Main {
    public static void main(String[] args) {
        // Teams in the order our scouts first saw them. 254 was written down twice.
        int[] scouted = {4143, 254, 1678, 118, 971, 33, 4414, 254, 6328};

        TeamTree teams = new TeamTree();
        for (int team : scouted) {
            teams.insert(team);
        }
        System.out.println("Teams scouted: " + teams.size());
        System.out.println("Is 1678 here? " + teams.contains(1678));
        System.out.println("Is 7 here? " + teams.contains(7));
        System.out.println("In order: " + teams.inOrder());
        System.out.println("Tree height: " + teams.height());

        // A two-piece auto, shaped like a behavior tree.
        AutoNode auto = new AutoNode("Sequence: two-piece auto")
            .add(new AutoNode("shoot preload"))
            .add(new AutoNode("Parallel: grab")
                .add(new AutoNode("drive to note"))
                .add(new AutoNode("run intake")))
            .add(new AutoNode("Sequence: score")
                .add(new AutoNode("drive to speaker"))
                .add(new AutoNode("aim"))
                .add(new AutoNode("shoot")));
        System.out.println(auto.getName() + " has " + auto.countActions()
            + " actions and depth " + auto.depth());
    }
}
