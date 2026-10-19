// Builds the auto the drive team picked on the dashboard, from its name.
// The rest of the robot code just asks the factory and never needs to know
// which classes exist. That's the Factory pattern.
public class AutoFactory {
    // The names the dashboard offers.
    public static final String[] NAMES = { "Leave", "One piece", "Two piece left", "Two piece right" };

    public static Auto create(String name) {
        if (name == null) {
            return new DoNothingAuto();
        }
        switch (name.trim().toLowerCase()) {
            case "leave":
                return new LeaveAuto();
            case "one piece":
                return new OnePieceAuto();
            case "two piece left":
                return new TwoPieceAuto("left");
            case "two piece right":
                return new TwoPieceAuto("right");
            default:
                return new DoNothingAuto();
        }
    }
}
