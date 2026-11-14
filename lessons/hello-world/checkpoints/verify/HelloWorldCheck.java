import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

public class HelloWorldCheck {
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
        String printed = captured.toString().trim();
        if (printed.isEmpty()) {
            System.out.println(
                "Running Main didn't print anything yet. Delete the // at the start of the System.out.println line."
            );
            System.exit(1);
        }
        String line = null;
        for (String l : printed.split("\\R")) {
            if (l.contains("Hello, World!")) {
                line = l.trim();
                break;
            }
        }
        if (line == null) {
            System.out.println(
                "Running Main didn't print \"Hello, World!\" - check your System.out.println call."
            );
            System.exit(1);
        }
        if (line.contains("YOUR NAME")) {
            System.out.println("Almost! Put your name where it says YOUR NAME.");
            System.exit(1);
        }
        int at = line.indexOf("My name is");
        if (at < 0 || line.substring(at + "My name is".length()).trim().isEmpty()) {
            System.out.println("Almost! Finish the line: Hello, World! My name is ...and then your name.");
            System.exit(1);
        }
        System.out.println("Looks good: " + line);
    }
}
