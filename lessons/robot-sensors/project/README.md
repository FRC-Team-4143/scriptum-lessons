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
  - `drive_.getLeftPositionRad()` / `drive_.getRightPositionRad()`: how far each side's
    wheels have turned, as a wheel angle in radians. Forward is positive.
  - `drive_.getLeftVelocityRadPerSec()` / `drive_.getRightVelocityRadPerSec()`: how fast, in radians per second.
  - `drive_.resetEncoders()`: reset: set both positions back to zero, so the distance starts over.

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
| 35 | Steps 1-2: meters, speeds and distance, then log them |
| 25 | Step 3: graph and check your numbers, then fix the gear ratio |
| 35 | Step 4: the deadband (in `Constants.java` and `Robot.java`) |
| 40 | Step 5: bang-bang driving, then the "Think about it" discussion |
| 15 | Verify |
| 10 | Catch up on any step that took longer, or help a classmate |

Go slower on anything that is new.

## What you need to do

1. **STEP 1: meters.** In `Robot.java`, fill in `left_meters`, `right_meters`, `left_meters_per_second` and
   `right_meters_per_second`. A wheel that turns through an angle in radians rolls the robot that angle times the wheel
   radius (arc length):

   ```text
   meters            = radians         x wheel radius
   meters per second = radians per sec x wheel radius
   ```

   `Constants.WHEEL_RADIUS_METERS` is the wheel radius.
2. **STEP 2: the whole robot.** Fill in the three values under it:

   ```text
   linear speed  = (left speed + right speed) / 2                        (m/s)
   angular speed = (right speed - left speed) / track width              (rad/s, positive = turning left)
   distance      = (left meters + right meters) / 2                      (m)
   ```

   The track width is `Constants.TRACK_WIDTH_METERS`.
3. **LOG IT: send your numbers to AdvantageScope.** A number you never log is a number you can't
   see. The robot runs this code 50 times a second, far too fast to watch, and a match only happens
   once, so the log is how you find out what the code really measured. At the end of
   `robotPeriodic()`, under the `LOG IT` comment, write one line for each number:

   ```java
   MwLog.log("Robot/Example", example_value);
   ```

   The first part, in quotes, is the **key**: the name the number gets in AdvantageScope. The slash
   puts it in a `Robot` folder. The second part is the value to record. Use exactly these keys,
   capitals and all, because Verify looks for them:

   | Key | Value | Unit |
   | --- | --- | --- |
   | `Robot/LinearSpeed` | `linear_speed` | m/s |
   | `Robot/AngularSpeed` | `angular_speed` | rad/s |
   | `Robot/Distance` | `distance_meters_` | m |
4. **Check them in AdvantageScope.** Drive forward flat out and watch `Robot/LinearSpeed`. It reads
   about 50 m/s, far too fast for a real robot! Your math is fine. The encoder sits inside the
   **motor**, and the drivetrain divides by `GEAR_RATIO` to get to the wheel. `GEAR_RATIO` in
   `Constants.java` is still `1.0`, which says the motor and wheel turn together. On our robot the
   gearbox is **8.46:1**: the motor turns 8.46 times for every 1 turn of the wheel. Set `GEAR_RATIO` to
   the real number, press **Start** again and check: full speed should now read about 5.9 m/s. Then
   spin in place and watch `Robot/AngularSpeed`.
5. **STEP 4: add a deadband.** Add a `DEADBAND` constant of `0.1` to `Constants.java`. Then, in
   `teleopPeriodic()`, use an `if` on `forward` and another on `turn`: if the absolute value
   (`Math.abs(...)`) is less than `Constants.DEADBAND`, set it to `0.0`.
6. **STEP 3: drive exactly 2 meters.** In `autonomousPeriodic()`, write an `if` / `else` on `distance_meters_`:
   drive at `0.4` while it is below `2.0`, stop otherwise. Run Auto and see where the robot stops.
7. **Run Auto again.** Disable, then Enable in Auto a second time. The robot starts over and drives
   the same distance again. That is thanks to the **reset** in `autonomousInit()` (see below).
8. **Click Verify** to check your work.

### What "reset" means

`drive_.resetEncoders()` means: *forget everything driven so far and start counting from zero again.*
The encoders only ever count up (or down) from where they started, so after one run of Auto the
distance reads about 2.0. If Auto did not reset, the next run would start at 2.0, think it had already
arrived, and never move. With the reset, `Robot/Distance` goes back to **0** at the start of every run
(it should never go negative), and the simulated robot is put back at its starting spot.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.
