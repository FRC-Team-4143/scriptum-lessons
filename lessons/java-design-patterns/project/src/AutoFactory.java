// Builds the auto the drive team picked on the dashboard, from its name.
// The rest of the robot code just asks the factory and never needs to know
// which classes exist. That's the Factory pattern.
public class AutoFactory {
    // The names the dashboard offers.
    public static final String[] NAMES = { "Leave", "One piece", "Two piece left", "Two piece right" };

    // TODO: return a NEW auto for the name:
    //   "leave"           -> new LeaveAuto()
    //   "one piece"       -> new OnePieceAuto()
    //   "two piece left"  -> new TwoPieceAuto("left")
    //   "two piece right" -> new TwoPieceAuto("right")
    //   Ignore upper/lower case and spaces at either end ("  LEAVE " is
    //   "leave"). Anything else, including null, gets new DoNothingAuto().
    //   Never return null.
    public static Auto create(String name) {
        return null;
    }
}
