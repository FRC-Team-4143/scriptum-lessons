import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TeamNameConstantCheck {
    public static void main(String[] args) {
        Field field;
        try {
            field = Main.class.getField("TEAM_NAME");
        } catch (NoSuchFieldException e) {
            System.out.println("No TEAM_NAME field found on Main.");
            System.exit(1);
            return;
        }
        if (!Modifier.isFinal(field.getModifiers())) {
            System.out.println("TEAM_NAME needs the `final` keyword - team standard for constants.");
            System.exit(1);
        }
        Object value;
        try {
            value = field.get(null);
        } catch (IllegalAccessException e) {
            System.out.println("Couldn't read TEAM_NAME - make sure it's public.");
            System.exit(1);
            return;
        }
        if (!"Team 4143".equals(value)) {
            System.out.println("TEAM_NAME is " + value + ", expected \"Team 4143\".");
            System.exit(1);
        }
        System.out.println("TEAM_NAME constant looks good.");
    }
}
