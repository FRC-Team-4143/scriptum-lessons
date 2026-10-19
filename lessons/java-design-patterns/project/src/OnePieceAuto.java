import java.util.List;

public class OnePieceAuto implements Auto {
    @Override
    public String getName() {
        return "One piece";
    }

    @Override
    public List<String> getSteps() {
        return List.of("score the preloaded piece", "drive forward 2 m");
    }
}
