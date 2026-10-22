// Checks sample-logs/Answers.java against what sample-logs/ReplayDemo.btlog recorded: FollowPath
// finished at 2.420 s; IsSystemState AIM then failed every 20 ms until the Timeout gave up at
// 3.940 s and failed the Sequence, so SetWantedState SHOOT never ran.
public class ReplayCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        double seconds = Answers.FOLLOW_PATH_DONE_SECONDS;
        if (seconds == 0) {
            fail("Answer the three questions in sample-logs/Answers.java. Replay sample-logs/ReplayDemo.btlog in Arbor first (the README's Replay a run says how).");
        }
        if (seconds > 100) {
            fail("FOLLOW_PATH_DONE_SECONDS is " + seconds + ". That looks like milliseconds; write it in seconds, for example 1.75.");
        }
        if (Math.abs(seconds - 2.42) > 0.1) {
            fail("FOLLOW_PATH_DONE_SECONDS is " + seconds + ". In the replay, step with the right arrow until FollowPath turns green, and read the time next to the playback bar.");
        }

        switch (Answers.FAILED_THE_AUTO) {
            case TIMEOUT:
                break;
            case NOT_ANSWERED:
                fail("FOLLOW_PATH_DONE_SECONDS is right. Now FAILED_THE_AUTO: which node's failure stopped the Sequence?");
            case IS_SYSTEM_STATE:
                fail("FAILED_THE_AUTO: IsSystemState did fail, over and over, but RetryUntilSuccessful just asked it again each time. Which node finally gave up and passed the failure up to the Sequence?");
            case SET_WANTED_STATE_SHOOT:
                fail("FAILED_THE_AUTO: SHOOT never ran, so it couldn't fail. Which node went red just before the Sequence did?");
            default:
                fail("FAILED_THE_AUTO: that one finished fine. Step to the end of the replay: which node went red just before the Sequence did?");
        }

        switch (Answers.NEVER_RAN) {
            case SET_WANTED_STATE_SHOOT:
                break;
            case NOT_ANSWERED:
                fail("Two right. Now NEVER_RAN: which node never ran at all?");
            default:
                fail("NEVER_RAN: that node did run at some point in the replay. Which one never turned orange, green or red?");
        }

        System.out.println("Right: the path took 2.42 s, then the robot never reached AIM, so the Timeout ran out after 1.5 s and SHOOT never ran. On a real robot, that's where you'd look next: why didn't it aim?");
    }
}
