# Drive Math and Methods

Last lesson you wrote the same math inside one long `robotPeriodic()`. In this lesson you will
move that math into **methods**: small named pieces of code that do one job and can be reused. When
you are done, `Robot.java` reads almost like a sentence.

Companion docs page: [Drive Math and Methods](https://docs.marswars.org/docs/software/training/drive-math-methods)

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Where the code lives

- `DriveMath.java` is **pure math**. Numbers go in, a number comes out. It never touches a motor
  or a sensor, so it is easy to test. You write five methods here, and **two of them (`linearSpeed`
  and `angularSpeed`) you create yourself, signature and all**.
- `mechanisms/DifferentialDriveMech.java` is the drivetrain. You write three more methods in it:
  `setLeftDutyCycle`, `setRightDutyCycle` and `arcadeDrive`. It also now has readings in meters and
  meters per second (`getLeftMeters()`, `getLinearSpeed()` and so on). Those are already written, and
  they call your `DriveMath` methods.
- `Robot.java` is already finished. It **calls** your methods, so read it to see how they are used.
  Until `linearSpeed` and `angularSpeed` exist the project shows red errors and will not build, and
  until the rest are written the robot sits still and logs zeros. **Verify** still works on each
  `DriveMath` method on its own.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Read about methods, arrays and loops |
| 30 | DriveMath: rotationsToMeters, then write linearSpeed and angularSpeed yourself |
| 30 | DriveMath.arcadeToWheelSpeeds |
| 25 | DriveMath.average |
| 30 | The mech: setLeft, setRight, arcadeDrive |
| 15 | Read "How the loop works" and test-drive |
| 15 | Verify |
| 15 | Bonus challenges |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

Work through the methods one at a time. After each, click **Verify** to test it.

1. **`DriveMath.rotationsToMeters`.** Wheel rotations times `2 * Math.PI * radius`.
2. **Write `DriveMath.linearSpeed` yourself.** It is not in the file yet, but the robot code already
   calls it, so the name must match exactly. It takes the left and right speeds (two `double`s) and
   returns their average.
3. **Write `DriveMath.angularSpeed` yourself.** It takes the left speed, the right speed and the
   track width (three `double`s) and returns `(right - left) / trackWidth`.
4. **`DriveMath.arcadeToWheelSpeeds`.** Return `{left, right}` with `left = forward + turn` and
   `right = forward - turn`. If either is above `1.0` or below `-1.0`, divide **both** by whichever
   is bigger.
5. **`DriveMath.average`.** Take an array of numbers and return their average, using a `for` loop.
   Then compare `Robot/SmoothedSpeed` with `Robot/LinearSpeed` in AdvantageScope.
6. **`setLeftDutyCycle` and `setRightDutyCycle`** in the drivetrain. Copy how `setDutyCycles` stores
   a clamped value in `leftRequest.Output`.
7. **`arcadeDrive`** in the drivetrain. Call `DriveMath.arcadeToWheelSpeeds` and pass the result to
   `setDutyCycles`.

Then **Start**, enable Teleop and drive (left stick: W / S forward/back, A / D turns). Run Auto to
see the robot drive 2 meters, built from your methods.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- Add `metersToRotations(meters, radius)` to `DriveMath` (the opposite of step 1).
- Add a **slow mode** to `teleopPeriodic()`: multiply forward by `0.5` while a bumper is held.
- Make the arcade turn gentler near the middle of the stick by **squaring** the stick value but
  keeping its sign. Hint: `Math.copySign(x * x, x)`.
