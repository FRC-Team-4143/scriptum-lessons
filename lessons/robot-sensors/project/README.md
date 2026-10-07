# Sensors and Feedback

Your robot can drive. Now teach it to **feel**. Each drive motor has a built-in sensor (an
**encoder**) that counts how far the wheel has turned. You will use it to work out how fast the
robot is going, how far it has gone, and then drive an exact distance by yourself.

Companion docs page: [Sensors and Feedback](https://docs.marswars.org/docs/software/training/sensors-feedback)

## Java you will learn in this lesson

**Conditions: making the robot decide.** Until now your code did the same thing every time. An `if`
statement runs some lines only when something is true:

```java
if (speed < 0.5) {
  speed = 0.5;       // only runs when speed is less than 0.5
}
```

The part in parentheses is a **condition**: a question with a true or false answer. Compare numbers
with `<` (less than), `>` (greater than), `<=`, `>=`, `==` (equal) and `!=` (not equal). Add an `else`
to do something different when the answer is false:

```java
if (distance < 2.0) {
  drive.setDutyCycles(0.4, 0.4);   // not there yet: keep driving
} else {
  drive.setDutyCycles(0.0, 0.0);   // there: stop
}
```

Two helpers you will use: `Math.abs(x)` gives the size of `x` without its sign (`Math.abs(-0.3)` is
`0.3`), which lets one `if` handle both directions.

**Constants: naming a number that never changes.** If a number appears in your code, give it a name
so everyone knows what it means. Put this at the top of the class (above your methods):

```java
private static final double DEADBAND = 0.1;
```

`final` means it can never change. We write constant names in `CAPITAL_LETTERS`.

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
forward and back, **Arrow Left / Right** to turn). In AdvantageScope, graph `Robot/LinearSpeed`,
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

1. **Rotations to meters.** The robot gives you wheel rotations, but we think in meters. One
   rotation moves the robot one wheel circumference. The starter already converts the left side:
   do the same for `rightMeters` and `rightMetersPerSecond` (look at how the left ones are written).
2. **Describe the whole robot.** Fill in the three `TODO` lines in STEP 2:
   - `linearSpeed`: how fast the robot moves forward, in meters per second. It is the **average**
     of the left and right speeds.
   - `angularSpeed`: how fast it turns, in radians per second. It is
     `(right - left) / Constants.TRACK_WIDTH_METERS`. A positive number means turning left
     (counterclockwise).
   - `distanceMeters`: how far the robot has driven, the average of the left and right distances.
3. **Check your numbers in AdvantageScope.** Drive forward: linear speed goes up and distance
   grows. Spin in place with the arrow keys: angular speed jumps and linear speed stays near zero.
4. **Add a deadband.** Real sticks never rest at exactly `0.0`: they drift a little, and the robot would
   creep. In `teleopPeriodic()`, declare the `DEADBAND` constant (see above) and use an `if`
   statement on `forward` and another on `turn`: if the absolute value is less than `DEADBAND`, set it
   to `0.0`.
5. **Drive exactly 2 meters, by yourself.** In `autonomousPeriodic()`, write an `if` / `else` on
   `distanceMeters`: drive at `0.4` while the distance is below `2.0`, and stop otherwise. This is
   called **bang-bang control**: the motors are fully on or fully off. Run Auto and look at where
   the robot stops. Does it stop at exactly 2.0 m?
6. **Click Verify** to check your work.

## Think about it

The robot almost certainly stops a little **past** 2.0 m. Why? When you cut the power, the robot is
still moving. Bang-bang control cannot slow down smoothly. Later you will learn **PID control**,
which is a smarter way to do the same job.

The simulated encoders are a little imperfect, just like real ones, so your measured 2.0 m is
not exactly 2.0 m of real driving. Sensors always have some error.

## Bonus challenges

- Drive **backward** 2 meters.
- Drive at `0.2` instead of `0.4`. How far past 2.0 m does it stop now?
- Log the speed of each side separately (`Robot/LeftSpeed`, `Robot/RightSpeed`).

## Words to know

- **Encoder:** a sensor that counts how far something has turned.
- **Feedback:** using what a sensor says to decide what to do next.
- **Bang-bang control:** full power or no power, based on whether you have reached a target.
- **Condition:** a true-or-false question that an `if` statement checks.
- **Constant:** a named value that never changes.
- **Deadband:** a small zone around zero that is treated as zero.
