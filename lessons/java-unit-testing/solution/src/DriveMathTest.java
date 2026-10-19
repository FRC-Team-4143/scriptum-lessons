// Your tests for DriveMath. Each @Test method checks one small thing.
public class DriveMathTest {
    // A worked example. Read it, run it, then copy its shape.
    @Test
    void deadbandKeepsFullSpeed() {
        // Full speed is way past the deadband, so it comes out unchanged.
        Assertions.assertEquals(1.0, DriveMath.deadband(1.0, 0.1), 0.0001);
    }

    @Test
    void deadbandZeroesSmallValues() {
        Assertions.assertEquals(0.0, DriveMath.deadband(0.05, 0.1), 0.0001);
        Assertions.assertEquals(0.0, DriveMath.deadband(-0.05, 0.1), 0.0001);
    }

    @Test
    void deadbandKeepsNegativeSpeeds() {
        Assertions.assertEquals(-0.6, DriveMath.deadband(-0.6, 0.1), 0.0001);
    }

    @Test
    void deadbandKeepsValueExactlyAtThreshold() {
        Assertions.assertEquals(0.1, DriveMath.deadband(0.1, 0.1), 0.0001);
        Assertions.assertEquals(-0.1, DriveMath.deadband(-0.1, 0.1), 0.0001);
    }

    @Test
    void clampRaisesValuesBelowMin() {
        Assertions.assertEquals(-1.0, DriveMath.clamp(-3.0, -1.0, 1.0), 0.0001);
    }

    @Test
    void clampLowersValuesAboveMax() {
        Assertions.assertEquals(1.0, DriveMath.clamp(2.5, -1.0, 1.0), 0.0001);
    }

    @Test
    void clampLeavesValuesInRangeAlone() {
        Assertions.assertEquals(0.4, DriveMath.clamp(0.4, -1.0, 1.0), 0.0001);
        Assertions.assertEquals(-1.0, DriveMath.clamp(-1.0, -1.0, 1.0), 0.0001);
    }

    @Test
    void inchesToMetersWholeInches() {
        Assertions.assertEquals(0.0, DriveMath.inchesToMeters(0.0), 0.0001);
        Assertions.assertEquals(0.254, DriveMath.inchesToMeters(10.0), 0.0001);
    }

    @Test
    void inchesToMetersKeepsTheFraction() {
        Assertions.assertEquals(0.0635, DriveMath.inchesToMeters(2.5), 0.0001);
    }

    @Test
    void inchesToMetersBackwards() {
        Assertions.assertEquals(-0.508, DriveMath.inchesToMeters(-20.0), 0.0001);
    }
}
