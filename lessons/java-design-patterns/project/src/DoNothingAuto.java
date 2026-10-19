import java.util.List;

// The safe choice: sit still. Better than driving into a wall.
public class DoNothingAuto implements Auto {
    @Override
    public String getName() {
        return "Do nothing";
    }

    @Override
    public List<String> getSteps() {
        return List.of();
    }
}
