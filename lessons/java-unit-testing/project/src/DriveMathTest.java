// Your tests for DriveMath. Each @Test method checks one small thing.
public class DriveMathTest {
    // A worked example. Read it, run it, then copy its shape.
    @Test
    void deadbandKeepsFullSpeed() {
        // Full speed is way past the deadband, so it comes out unchanged.
        Assertions.assertEquals(1.0, DriveMath.deadband(1.0, 0.1), 0.0001);
    }

    // TODO (deadband): add tests for deadband. Think about a small value, a
    //   negative value, and a value exactly at the threshold.

    // TODO (clamp): add tests for clamp. Below min, above max, and in between.

    // TODO (inchesToMeters): add tests for inchesToMeters. Try a value that
    //   isn't a whole number, and a negative one.
}
