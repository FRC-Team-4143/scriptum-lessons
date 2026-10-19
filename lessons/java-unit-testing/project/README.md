# Unit Testing

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Unit Testing](https://frc-team-4143.github.io/docs/software/java/advanced/unit-testing).

How do you know your code works? Up to now you ran `Main` and read the
output yourself. That works once. But robot code changes every week, and
nobody re-reads every number after every change. A **unit test** is a tiny
method that calls your code with an input you picked and checks the answer
for you. Write it once, and it re-checks the code every time you run it.

In this lesson the robot math in `DriveMath.java` is already correct. Your
job is to write the tests that **prove** it. Then `ShooterMath.java` has a
real bug, and you catch it with a test before you fix it.

## How you're graded: planted bugs

A test that can never fail is useless. So the checker plants a bug in the
code (a slightly wrong copy of it), runs your tests, and does this for
several different bugs. A checkpoint passes only when:

- every one of your tests **passes** on the correct code, and
- for each planted bug, at least one of your tests **fails**.

If a bug slips through, the checkpoint tells you what kind of bug it was,
so you know which kind of test to add. Pros call this **mutation testing**.

## What you need to do

Team standard: fields use `trailing_snake_case_`, methods use `camelCase`.
Name each test after what it proves, like `deadbandZeroesSmallValues`.

1. **Read the worked example** in `src/DriveMathTest.java`, then run `Main`.
   Each test prints `PASS` or `FAIL`.
2. **Test `deadband(value, threshold)`.** Anything closer to 0 than the
   threshold, on either side, becomes `0.0`. Anything else comes out
   unchanged. Think about small values, negative values, and a value
   exactly *on* the threshold.
3. **Test `clamp(value, min, max)`.** Below `min` gives `min`, above `max`
   gives `max`, and anything in between comes out unchanged.
4. **Test `inchesToMeters(inches)`.** 1 inch is 0.0254 meters. Try a value
   that isn't a whole number, and a negative distance.
5. **Find the bug in `ShooterMath.isAtSpeed`.** It should be `true` when the
   flywheel is at most `tolerance_rpm` away from `target_rpm`, too fast *or*
   too slow. Write tests in `src/ShooterMathTest.java` first. Run them and
   watch one **FAIL**. Then fix `ShooterMath.java` until they all pass.

Work out every expected value from the rules above (or a calculator), not
by copying what the code prints. If the code were wrong, copying its answer
would write the bug into your test.

Don't edit `Test.java`, `Assertions.java`, `TestRunner.java` or
`DriveMath.java`: they're provided, and the checker uses its own copies.

### New Java in this lesson

- `@Test`: an **annotation**, a tag on the line above a method. The test
  runner finds every method tagged `@Test` and runs it.
- `Assertions.assertEquals(expected, actual, tolerance)` for decimals. Two
  `double`s are almost never *exactly* equal (`0.1 + 0.2` is
  `0.30000000000000004`), so you say how close counts.
- `Assertions.assertEquals(expected, actual)` for whole numbers and
  Strings, and `Assertions.assertTrue(...)` / `assertFalse(...)` for
  booleans. Each one can take a message as the last argument, which is
  shown when it fails.
- An **`AssertionError`**: what an assert throws when it is wrong. It is
  what turns a test into a `FAIL`.

### The real thing: JUnit

These files are a small copy of **JUnit 5**, the testing library real Java
projects (and WPILib robot projects) use. In JUnit you write:

```java
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DriveMathTest {
    @Test
    void deadbandKeepsFullSpeed() {
        assertEquals(1.0, DriveMath.deadband(1.0, 0.1), 0.0001);
    }
}
```

The `import static` line is why JUnit tests can say `assertEquals` without
`Assertions.` in front. This project has no packages, so it can't use that
trick. In a robot project, tests go in `src/test/java` and you run them all
with `./gradlew test`.

## Why this matters on the robot

You can't plug the robot in every time you change a line of math. A bug in
`deadband` makes the robot creep across the field; a bug in a shooter check
like `isAtSpeed` fires the game piece before the flywheel is up to speed,
and it misses. Tests catch those on your laptop, in a second, before the
code ever reaches the robot, and they keep catching them every time someone
changes the code later.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
