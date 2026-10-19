import java.util.List;

public class LeaveAuto implements Auto {
    @Override
    public String getName() {
        return "Leave";
    }

    @Override
    public List<String> getSteps() {
        return List.of("drive forward 2 m");
    }
}
