// Our autos can be typed as one line of text, like
//   seq(drive[2.0], par{intake, shoot})
// Before the robot tries to run one, it checks that every bracket that opens
// is closed again, by the same kind of bracket, in the right order.
import java.util.ArrayDeque;

public class AutoParser {
    public static boolean isBalanced(String text) {
        ArrayDeque<Character> open = new ArrayDeque<>();
        for (char c : text.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                open.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (open.isEmpty()) {
                    return false;
                }
                char top = open.pop();
                if ((c == ')' && top != '(') || (c == ']' && top != '[') || (c == '}' && top != '{')) {
                    return false;
                }
            }
        }
        return open.isEmpty();
    }
}
