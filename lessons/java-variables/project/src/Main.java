public class Main {
    // TODO: declare a constant named TEAM_NAME holding the String "Team 4143".
    // Team standard: constants use the `final` keyword and SCREAMING_SNAKE_CASE.
    public static final String TEAM_NAME = "";

    // TODO: define an enum named ALLIANCE with two values: RED and BLUE.
    // Team standard: both the enum name and its values are SCREAMING_SNAKE_CASE.
    // Put it inside this class, as a nested type - not as its own top-level
    // type below the class. It's used only by Main, so it belongs in Main.

    // TODO: define an enum named MATCH_PERIOD with three values: AUTONOMOUS,
    // TELEOP, and ENDGAME. Same rules as ALLIANCE: nested inside this class,
    // SCREAMING_SNAKE_CASE name and values.

    // TODO: declare a constant named MAX_SPEED holding 5.0.
    // Team standard: constants use the `final` keyword and SCREAMING_SNAKE_CASE.
    public static final double MAX_SPEED = 0.0;

    public static void main(String[] args) {
        // TODO: declare four local variables (name them whatever you like)
        // and print each on its own line with System.out.println, matching
        // these datatypes/values/printed keys - see the README's table:
        //   int, 4143         -> "Team number: " + <your variable>
        //   double, 3.14      -> "Pi: " + <your variable>
        //   boolean, true     -> "Robot is on: " + <your variable>
        //   String, "I am a string!" -> "Message: " + <your variable>

        System.out.println("Team name: " + TEAM_NAME);
        System.out.println("Max speed: " + MAX_SPEED);
    }
}
