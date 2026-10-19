// Our autos can be typed as one line of text, like
//   seq(drive[2.0], par{intake, shoot})
// Before the robot tries to run one, it checks that every bracket that opens
// is closed again, by the same kind of bracket, in the right order.
// TODO: import java.util.ArrayDeque; at the very top of this file.
public class AutoParser {
    // TODO: return true when every ( [ { in text is closed by its own ) ] },
    //   in the right order. Every other character (letters, numbers, commas,
    //   spaces) doesn't matter. An auto with no brackets at all is balanced.
    public static boolean isBalanced(String text) {
        return false;
    }
}
