// Replay a run: play ReplayDemo.btlog (in this folder) back in Arbor, then answer these from what
// you saw. The README's "Replay a run" says how. Save, then Checkpoints > Verify checks them.
public class Answers {
    // The nodes in the ReplayDemo tree, top to bottom.
    enum Node {
        NOT_ANSWERED,
        FOLLOW_PATH,
        SET_WANTED_STATE_AIM,
        TIMEOUT,
        RETRY_UNTIL_SUCCESSFUL,
        IS_SYSTEM_STATE,
        SET_WANTED_STATE_SHOOT,
    }

    // 1. The time next to the playback bar says where you are in the run, in seconds.
    //    When did FollowPath finish (turn green)? For example 1.75.
    public static final double FOLLOW_PATH_DONE_SECONDS = 0;

    // 2. Which node's failure stopped the Sequence, so the whole auto failed?
    public static final Node FAILED_THE_AUTO = Node.NOT_ANSWERED;

    // 3. Which node never ran at all?
    public static final Node NEVER_RAN = Node.NOT_ANSWERED;
}
