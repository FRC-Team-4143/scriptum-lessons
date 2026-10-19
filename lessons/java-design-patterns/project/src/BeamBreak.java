import java.util.ArrayList;
import java.util.List;

// A beam-break sensor in the intake: a light beam across the opening. When a
// game piece blocks the beam, the robot has a piece. Other parts of the robot
// (LEDs, the intake, the dashboard) sign up as listeners, and the sensor
// tells them when it changes. That's the Observer pattern.
public class BeamBreak {
    protected List<PieceListener> listeners_ = new ArrayList<>();
    protected boolean has_piece_ = false;

    // TODO: add the listener to listeners_.
    public void addListener(PieceListener listener) {
    }

    // TODO: take the listener out of listeners_, so it hears nothing more.
    public void removeListener(PieceListener listener) {
    }

    // Called every robot loop with what the sensor reads now.
    // TODO: if beam_blocked is different from has_piece_, set has_piece_
    //   to the new value FIRST, then call onChange(has_piece_) on every
    //   listener, in the order they were added. If nothing changed, tell
    //   nobody.
    public void update(boolean beam_blocked) {
    }

    public boolean hasPiece() {
        return has_piece_;
    }
}
