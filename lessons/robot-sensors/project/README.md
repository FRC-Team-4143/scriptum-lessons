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
  - `drive_.getLeftPositionRotations()` / `drive_.getRightPositionRotations()`: how far each side's
    wheels have turned, in wheel rotations. Forward is positive.
  - `drive_.getLeftVelocityRps()` / `drive_.getRightVelocityRps()`: how fast, in rotations per second.
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
| 35 | Steps 1-2: meters, speeds and distance |
| 25 | Step 3: graph and check your numbers |
| 35 | Step 4: the deadband (in `Constants.java` and `Robot.java`) |
| 40 | Step 5: bang-bang driving, then the "Think about it" discussion |
| 15 | Verify |
| 10 | Bonus challenges |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

1. **STEP 1: meters.** In `Robot.java`, fill in `left_meters`, `right_meters`, `left_meters_per_second` and
   `right_meters_per_second`. One wheel rotation moves the robot one wheel circumference:

   ```text
   meters            = rotations         x (2 x pi x wheel radius)
   meters per second = rotations per sec x (2 x pi x wheel radius)
   ```

   `Math.PI` is pi and `Constants.WHEEL_RADIUS_METERS` is the wheel radius.
2. **STEP 2: the whole robot.** Fill in the three values under it:

   ```text
   linear speed  = (left speed + right speed) / 2                        (m/s)
   angular speed = (right speed - left speed) / track width              (rad/s, positive = turning left)
   distance      = (left meters + right meters) / 2                      (m)
   ```

   The track width is `Constants.TRACK_WIDTH_METERS`.
3. **Check them in AdvantageScope.** Drive forward, then spin in place, and watch the numbers.
4. **STEP 4: add a deadband.** Add a `DEADBAND` constant of `0.1` to `Constants.java`. Then, in
   `teleopPeriodic()`, use an `if` on `forward` and another on `turn`: if the absolute value
   (`Math.abs(...)`) is less than `Constants.DEADBAND`, set it to `0.0`.
5. **STEP 3: drive exactly 2 meters.** In `autonomousPeriodic()`, write an `if` / `else` on `distance_meters_`:
   drive at `0.4` while it is below `2.0`, stop otherwise. Run Auto and see where the robot stops.
6. **Run Auto again.** Disable, then Enable in Auto a second time. The robot starts over and drives
   the same distance again. That is thanks to the **reset** in `autonomousInit()` (see below).
7. **Click Verify** to check your work.

### What "reset" means

`drive_.resetEncoders()` means: *forget everything driven so far and start counting from zero again.*
The encoders only ever count up (or down) from where they started, so after one run of Auto the
distance reads about 2.0. If Auto did not reset, the next run would start at 2.0, think it had already
arrived, and never move. With the reset, `Robot/Distance` goes back to **0** at the start of every run
(it should never go negative), and the simulated robot is put back at its starting spot.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- Drive **backward** 2 meters.
- Drive at `0.2` instead of `0.4`. How far past 2.0 m does it stop now?
- Log the speed of each side separately (`Robot/LeftSpeed`, `Robot/RightSpeed`).
