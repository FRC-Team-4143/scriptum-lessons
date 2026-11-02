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
| 35 | Steps 1-2: meters, speeds and distance |
| 25 | Step 3: graph and check your numbers, then fix the gear ratio |
| 35 | Step 4: the deadband (in `Constants.java` and `Robot.java`) |
| 40 | Step 5: bang-bang driving, then the "Think about it" discussion |
| 15 | Verify |
| 10 | Bonus challenges |

Go slower on anything that is new. Finishing every bonus is not expected.

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
3. **Check them in AdvantageScope.** Drive forward flat out and watch `Robot/LinearSpeed`. It reads
   about 50 m/s, far too fast for a real robot! Your math is fine. The encoder sits inside the
   **motor**, and the drivetrain divides by `GEAR_RATIO` to get to the wheel. `GEAR_RATIO` in
   `Constants.java` is still `1.0`, which says the motor and wheel turn together. On our robot the
   gearbox is **8.46:1**: the motor turns 8.46 times for every 1 turn of the wheel. Set `GEAR_RATIO` to
   the real number, press **Start** again and check: full speed should now read about 5.9 m/s. Then
   spin in place and watch `Robot/AngularSpeed`.
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

## Advanced challenge (optional)

This one is **optional**. It's for students who finish early and want something harder. It isn't needed to finish the lesson, and Dozer doesn't walk through it: you work it out, the way you would on a real robot. It shows as **Optional** in the Checkpoints list.

### Stop on the mark

Your bang-bang robot coasts about 70 centimeters past 2 meters. Slowing down doesn't fix it, because a
robot that crawls gives away the match. Make Auto **stop within 3 centimeters of 2.0 meters, in under
2.5 seconds**. (This also still passes the **Drive 2 meters** checkpoint.)

Things to think about:

- Let the power depend on how far is left: big when far, small when close. Plot `Robot/Distance` and see what happens.
- That alone overshoots, because a 50 kg robot carries its speed. What else do you know in
  `robotPeriodic()` that says it's about to overshoot?
- Power can be negative. Pushing backward is the only real brake a robot has.
- Keep the numbers as constants so you can tune them, and change one at a time.
- Keep an `if` / `else` on `distance_meters_` in `autonomousPeriodic()`: "close enough to 2.0, so stop" is a
  natural one, and the **Deadband and if statements** checkpoint still looks for it.

**How it's checked:** run Auto with the simulator running. The check uses where the robot **really** is
(`Drive/TruePose`), and judges each run once the robot has stopped for half a second. Plot
`Check/Challenge/StopErrorMeters` and `Check/Challenge/StopSeconds` to see how close you got.

## Bonus challenges

- Drive **backward** 2 meters.
- Drive at `0.2` instead of `0.4`. How far past 2.0 m does it stop now?
- Log the speed of each side separately (`Robot/LeftSpeed`, `Robot/RightSpeed`).
