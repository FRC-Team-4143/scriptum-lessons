import java.util.ArrayList;
import java.util.List;

public class ListenersCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expectHeard(List<String> heard, List<String> want, String what) {
        if (!want.equals(heard)) {
            fail("After " + what + ", the listeners heard " + heard + ", expected " + want + ".");
        }
        heard.clear();
    }

    public static void main(String[] args) {
        BeamBreak beam = new BeamBreak();
        List<String> heard = new ArrayList<>();

        // No listeners yet: update must still work, and must not crash.
        beam.update(true);
        if (!beam.hasPiece()) {
            fail("After update(true) with no listeners, hasPiece() was false, expected true.");
        }
        beam.update(false);

        PieceListener leds = has_piece -> heard.add("leds:" + has_piece);
        PieceListener intake = has_piece -> heard.add("intake:" + has_piece + ":" + beam.hasPiece());
        beam.addListener(leds);
        beam.addListener(intake);

        beam.update(false);
        expectHeard(heard, List.of(), "update(false) when there was no piece already (nothing changed)");

        beam.update(true);
        expectHeard(heard, List.of("leds:true", "intake:true:true"),
            "update(true) with two listeners (each told once, in the order they were added, and hasPiece() already true when they hear)");

        beam.update(true);
        beam.update(true);
        expectHeard(heard, List.of(), "two more update(true) loops (the piece was already there)");

        beam.update(false);
        expectHeard(heard, List.of("leds:false", "intake:false:false"), "update(false) when the piece leaves");

        PieceListener late = has_piece -> heard.add("late:" + has_piece);
        beam.addListener(late);
        expectHeard(heard, List.of(), "adding a third listener (adding one doesn't tell it anything yet)");
        beam.update(true);
        expectHeard(heard, List.of("leds:true", "intake:true:true", "late:true"), "update(true) with three listeners");

        beam.removeListener(leds);
        beam.update(false);
        expectHeard(heard, List.of("intake:false:false", "late:false"), "removeListener(the first one), then update(false)");
        if (beam.hasPiece()) {
            fail("After update(false), hasPiece() was true, expected false.");
        }

        BeamBreak other = new BeamBreak();
        other.update(true);
        expectHeard(heard, List.of(), "update(true) on a second, separate BeamBreak (its listeners are its own)");

        System.out.println("The beam break tells every listener, once per change.");
    }
}
