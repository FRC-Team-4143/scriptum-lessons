import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

public class LocalVariablesCheck {
    public static void main(String[] args) throws Exception {
        Method method;
        try {
            method = Main.class.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            System.out.println("No main(String[] args) method found on Main.");
            System.exit(1);
            return;
        }

        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            method.invoke(null, (Object) new String[0]);
        } finally {
            System.setOut(originalOut);
        }
        String printed = captured.toString();

        if (!printed.contains("Team number: 4143")) {
            System.out.println(
                "Didn't find \"Team number: 4143\" in the printed output - "
                    + "declare int team_number = 4143; and print it."
            );
            System.exit(1);
            return;
        }
        if (!printed.contains("Pi: 3.14")) {
            System.out.println(
                "Didn't find \"Pi: 3.14\" in the printed output - "
                    + "declare double pi = 3.14; and print it."
            );
            System.exit(1);
            return;
        }
        if (!printed.contains("Robot is on: true")) {
            System.out.println(
                "Didn't find \"Robot is on: true\" in the printed output - "
                    + "declare boolean robot_is_on = true; and print it."
            );
            System.exit(1);
            return;
        }
        if (!printed.contains("Message: I am a string!")) {
            System.out.println(
                "Didn't find \"Message: I am a string!\" in the printed output - "
                    + "declare String message = \"I am a string!\"; and print it."
            );
            System.exit(1);
            return;
        }
        System.out.println("Local variables look good.");
    }
}
