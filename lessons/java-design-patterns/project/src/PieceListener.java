// Anything that wants to hear when the intake gains or loses a game piece.
// It has one method, so a lambda can be a PieceListener:
//   beam.addListener(has_piece -> System.out.println(has_piece));
@FunctionalInterface
public interface PieceListener {
    void onChange(boolean has_piece);
}
