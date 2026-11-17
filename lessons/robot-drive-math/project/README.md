# Drive Math and Methods

Last lesson you wrote the same math inside one long `robotPeriodic()`. In this lesson you will
move that math into **methods**: small named pieces of code that do one job and can be reused. When
you are done, `Robot.java` reads almost like a sentence.

Companion docs page: [Drive Math and Methods](https://docs.marswars.org/docs/software/training/drive-math-methods)

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Where the code lives

You work in **two files**. Open them from the Explorer on the left; both are under
`src/main/java/frc/robot/`.

| Method | File | Status |
| --- | --- | --- |
| `radiansToMeters` | `DriveMath.java` | fill in the body |
| `linearSpeed` | `DriveMath.java` | **not there yet**: write the whole method |
| `angularSpeed` | `DriveMath.java` | **not there yet**: write the whole method |
| `arcadeToWheelSpeeds` | `DriveMath.java` | fill in the body |
| `average` | `DriveMath.java` | fill in the body |
| `setLeftDutyCycle` | `mechanisms/DifferentialDriveMech.java` | fill in the body |
| `setRightDutyCycle` | `mechanisms/DifferentialDriveMech.java` | fill in the body |
| `arcadeDrive` | `mechanisms/DifferentialDriveMech.java` | fill in the body |

- **`src/main/java/frc/robot/DriveMath.java`** is **pure math**. Numbers go in, a number comes out.
  It never touches a motor or a sensor, so it is easy to test. Five methods go here. The ones to
  fill in have a `// TODO` comment; `linearSpeed` and `angularSpeed` you add yourself, signature and
  all, where the `// YOUR TURN` comment is.
- **`src/main/java/frc/robot/mechanisms/DifferentialDriveMech.java`** is the drivetrain. Three
  methods go here, near the bottom of the file, each with a `// TODO`. The file also has readings in
  meters and meters per second (`getLeftMeters()`, `getLinearSpeed()` and so on). Those are already
  written, and they call your `DriveMath` methods.
- **`src/main/java/frc/robot/Robot.java`** is already finished: don't change it. It **calls** your
  methods, so read it to see how they are used. Until `linearSpeed` and `angularSpeed` exist the
  project shows red errors and will not build, and until the rest are written the robot sits still
  and logs zeros. **Verify** still works on each `DriveMath` method on its own.

Tip: press **Ctrl+P** (Cmd+P on a Mac) and type a file name, like `DriveMath`, to open it.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Read about methods, arrays and loops |
| 30 | `DriveMath.java`: radiansToMeters, then write linearSpeed and angularSpeed yourself |
| 30 | `DriveMath.java`: arcadeToWheelSpeeds |
| 25 | `DriveMath.java`: average |
| 30 | `DifferentialDriveMech.java`: setLeftDutyCycle, setRightDutyCycle, arcadeDrive |
| 15 | Read "How the loop works" and test-drive |
| 15 | Verify |
| 15 | Catch up on any step that took longer, or help a classmate |

Go slower on anything that is new.

## What you need to do

Work through the methods one at a time. After each, click **Verify** to test it.

### In `DriveMath.java` (`src/main/java/frc/robot/DriveMath.java`)

1. **`radiansToMeters`.** Wheel radians times the wheel radius (arc length).
2. **Write `linearSpeed` yourself.** It is not in the file yet, but the drivetrain already calls it,
   so the name must match exactly. Write it where the `// YOUR TURN` comment is, just below
   `radiansToMeters`. It takes the left and right speeds (two `double`s) and returns their average.
3. **Write `angularSpeed` yourself**, in the same place. It takes the left speed, the right speed and
   the track width (three `double`s) and returns `(right - left) / trackWidth`.
4. **`arcadeToWheelSpeeds`.** Return `{left, right}` with `left = forward + turn` and
   `right = forward - turn`. If either is above `1.0` or below `-1.0`, divide **both** by whichever
   is bigger.
5. **`average`.** Take an array of numbers and return their average, using a `for` loop.
   Then compare `Robot/SmoothedSpeed` with `Robot/LinearSpeed` in AdvantageScope.

### In `DifferentialDriveMech.java` (`src/main/java/frc/robot/mechanisms/DifferentialDriveMech.java`)

Scroll down to the three methods with a `// TODO`, just below `getAngularSpeed()`.

6. **`setLeftDutyCycle` and `setRightDutyCycle`.** Copy how `setDutyCycles` (higher up in the same
   file) stores a clamped value in `left_request_.Output`.
7. **`arcadeDrive`.** Call `DriveMath.arcadeToWheelSpeeds` and pass the result to `setDutyCycles`.

Then **Start**, enable Teleop and drive (left stick: W / S forward/back, A / D turns). Run Auto to
see the robot drive 2 meters, built from your methods.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.
