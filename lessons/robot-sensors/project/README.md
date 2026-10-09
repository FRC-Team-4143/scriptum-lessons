# Sensors and Feedback

Your robot can drive. Now teach it to **feel**. Each drive motor has a built-in sensor (an
**encoder**) that counts how far the wheel has turned. You will use it to work out how fast the
robot is going, how far it has gone, and then drive an exact distance by yourself.

Companion docs page: [Sensors and Feedback](https://docs.marswars.org/docs/software/training/sensors-feedback)

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Where the code lives

- `Robot.java` is **your code**. `robotPeriodic()` runs all the time, even when disabled, so it is
  the right place to measure things. `autonomousPeriodic()` runs on its own during autonomous.
- The drivetrain now has sensor methods you can call:
  - `drive.getLeftPositionRotations()` / `drive.getRightPositionRotations()`: how far each side's
    wheels have turned, in wheel rotations. Forward is positive.
  - `drive.getLeftVelocityRps()` / `drive.getRightVelocityRps()`: how fast, in rotations per second.
  - `drive.resetEncoders()`: set both positions back to zero.

## Running it

**Start**, then **Enable** in Teleop mode and drive around with the keyboard (**W / S** to go
forward and back, **A / D** to turn). In AdvantageScope, graph `Robot/LinearSpeed`,
`Robot/AngularSpeed` and `Robot/Distance` as they come to life. For autonomous, choose **Auto** mode and
click **Enable**.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Read about conditions and constants |
| 35 | Steps 1-2: meters, speeds and distance |
| 25 | Step 3: graph and check your numbers |
| 35 | Step 4: the deadband |
| 40 | Step 5: bang-bang driving, then the "Think about it" discussion |
| 15 | Verify |
| 10 | Bonus challenges |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

1. **Right-side conversions.** In `Robot.java`, convert `rightMeters` and `rightMetersPerSecond` the
   way the left side is already done.
2. **Fill the three `TODO` lines in STEP 2:**
   - `linearSpeed`: the average of the left and right speeds (m/s).
   - `angularSpeed`: `(right - left) / Constants.TRACK_WIDTH_METERS` (rad/s, positive = turning left).
   - `distanceMeters`: the average of the left and right distances.
3. **Check them in AdvantageScope.** Drive forward, then spin in place, and watch the numbers.
4. **Add a deadband.** In `teleopPeriodic()`, declare the `DEADBAND` constant and use an `if` on
   `forward` and another on `turn`: if the absolute value is less than `DEADBAND`, set it to `0.0`.
5. **Drive exactly 2 meters.** In `autonomousPeriodic()`, write an `if` / `else` on `distanceMeters`:
   drive at `0.4` while it is below `2.0`, stop otherwise. Run Auto and see where the robot stops.
6. **Click Verify** to check your work.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- Drive **backward** 2 meters.
- Drive at `0.2` instead of `0.4`. How far past 2.0 m does it stop now?
- Log the speed of each side separately (`Robot/LeftSpeed`, `Robot/RightSpeed`).
