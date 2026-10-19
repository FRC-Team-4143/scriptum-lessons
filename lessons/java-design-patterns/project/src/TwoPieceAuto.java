import java.util.List;

public class TwoPieceAuto implements Auto {
    protected String side_;

    // side is "left" or "right": which side of the field to grab the second piece from.
    public TwoPieceAuto(String side) {
        side_ = side;
    }

    @Override
    public String getName() {
        return "Two piece (" + side_ + ")";
    }

    @Override
    public List<String> getSteps() {
        return List.of(
            "score the preloaded piece",
            "drive to the " + side_ + " piece",
            "intake it",
            "drive back and score it");
    }
}
