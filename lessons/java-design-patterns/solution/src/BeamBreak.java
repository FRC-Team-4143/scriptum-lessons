import java.util.ArrayList;
import java.util.List;

// A beam-break sensor in the intake: a light beam across the opening. When a
// game piece blocks the beam, the robot has a piece. Other parts of the robot
// (LEDs, the intake, the dashboard) sign up as listeners, and the sensor
// tells them when it changes. That's the Observer pattern.
public class BeamBreak {
    protected List<PieceListener> listeners_ = new ArrayList<>();
    protected boolean has_piece_ = false;

    public void addListener(PieceListener listener) {
        listeners_.add(listener);
    }

    public void removeListener(PieceListener listener) {
        listeners_.remove(listener);
    }

    // Called every robot loop with what the sensor reads now.
    public void update(boolean beam_blocked) {
        if (beam_blocked == has_piece_) {
            return;
        }
        has_piece_ = beam_blocked;
        for (PieceListener listener : listeners_) {
            listener.onChange(has_piece_);
        }
    }

    public boolean hasPiece() {
        return has_piece_;
    }
}
