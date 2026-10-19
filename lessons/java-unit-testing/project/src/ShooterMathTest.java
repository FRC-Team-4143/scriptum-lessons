// Your tests for ShooterMath. ShooterMath has a bug: write a test that
// catches it BEFORE you fix it.
public class ShooterMathTest {
    @Test
    void atSpeedWhenExactlyOnTarget() {
        Assertions.assertTrue(ShooterMath.isAtSpeed(3000.0, 3000.0, 50.0));
    }

    // TODO (find the bug): add more tests for isAtSpeed. A little too fast?
    //   A little too slow? Way too slow? Run them, find the one that FAILS,
    //   then fix ShooterMath.
}
