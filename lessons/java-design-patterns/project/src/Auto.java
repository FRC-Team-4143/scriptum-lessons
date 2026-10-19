import java.util.List;

// An autonomous routine: what the robot does on its own at the start of a match.
public interface Auto {
    String getName();

    List<String> getSteps();
}
