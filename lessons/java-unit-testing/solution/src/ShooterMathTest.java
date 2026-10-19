// Your tests for ShooterMath. ShooterMath has a bug: write a test that
// catches it BEFORE you fix it.
public class ShooterMathTest {
    @Test
    void atSpeedWhenExactlyOnTarget() {
        Assertions.assertTrue(ShooterMath.isAtSpeed(3000.0, 3000.0, 50.0));
    }

    @Test
    void atSpeedWhenALittleFastOrSlow() {
        Assertions.assertTrue(ShooterMath.isAtSpeed(3040.0, 3000.0, 50.0));
        Assertions.assertTrue(ShooterMath.isAtSpeed(2960.0, 3000.0, 50.0));
    }

    @Test
    void notAtSpeedWhenTooFast() {
        Assertions.assertFalse(ShooterMath.isAtSpeed(3200.0, 3000.0, 50.0));
    }

    @Test
    void notAtSpeedWhileStillSpinningUp() {
        Assertions.assertFalse(ShooterMath.isAtSpeed(1000.0, 3000.0, 50.0),
            "a flywheel at 1000 rpm is nowhere near 3000");
    }
}
