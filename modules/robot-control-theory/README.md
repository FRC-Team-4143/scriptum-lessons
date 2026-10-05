# Control Theory

Your shooter's flywheel has to spin at exactly the right speed, shot after shot, even when
launching a game piece slows it down. Deciding **how much power to give a motor so it holds a
target** is the heart of control theory. In this lesson you will make the flywheel hold its speed
three different ways, watch how each one behaves, and then tune the best one.

Companion docs pages: [Control Theory](https://docs.marswars.org/docs/software/training/control-theory),
[Control Types](https://docs.marswars.org/docs/software/controls/system-control/control-types),
[Bang Bang](https://docs.marswars.org/docs/software/controls/system-control/bang-bang),
[Feed Forward](https://docs.marswars.org/docs/software/controls/system-control/feed-fwd) and
[PID](https://docs.marswars.org/docs/software/controls/system-control/pid)

## The big idea

The flywheel should spin at `SHOOT_VELOCITY`. The difference between where it *should* be and where it
*is* is the **error**. Three ways to use that idea:

| Style | The idea | Good at | Bad at |
| --- | --- | --- | --- |
| **Bang-bang** | Full power when too slow, zero power when fast enough. | Simple, spins up very fast. | Wobbles around the target and slams the motor on and off. |
| **Feedforward** | Never measure anything: work out the power the target needs and apply it. | Smooth. Gets close right away. | Cannot notice or fix a mistake or a disturbance. |
| **PID** | Feedforward as the starting guess **plus** a correction that grows with the error. | Accurate *and* it recovers when something slows the wheel. | Needs its numbers (the **gains**) tuned. |

PID has up to three parts. This lesson uses the two that matter most:
**`kV`** (the feedforward gain) and **`kP`** (the "proportional" feedback gain).

## Java you will learn in this lesson

This lesson adds no new Java syntax. It is practice with what you have learned, plus one useful skill:

- **Unit conversion with operators.** The target speed is in radians per second but the motor wants
  rotations per second. One rotation is `2 * Math.PI` radians, so `rotationsPerSecond = radiansPerSecond / (2 *
  Math.PI)`. Volts to a duty cycle is `volts / 12.0`. Getting units right is half of robot programming.
- **`switch` on an enum** picks which control style runs.
- **Small methods** (`bangBang()`, `feedforward()`) each do one job, so the `switch` stays easy to read.

## Where the code lives

- `subsystems/shooter/ShooterConstants.java`: pick the style with `FLYWHEEL_CONTROL` and set the gains
  `FLYWHEEL_KV` and `FLYWHEEL_KP`.
- `subsystems/shooter/ShooterSubsystem.java`: `bangBang()` and `feedforward()` (you write these) and
  `runFlywheel()` (it picks the style).
- Everything from last lesson (states, commands, buttons) is already finished.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Read the big idea |
| 35 | Step 1: bang-bang |
| 30 | Steps 2-3: feedforward and finding kV |
| 40 | Step 4: add feedback and tune kP |
| 20 | Compare how each style recovers from a launch |
| 10 | Steps 5-6: final settings and Verify |
| 25 | Extension: the WPILib elevator tuning exercise from the docs |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

Hold the **right bumper** (**U**) to shoot and tap **A** (**K**) to simulate a launched game piece. Run the robot
after each change and watch `Subsystem/Shooter/FlywheelVelocity` and `FlywheelError` in
AdvantageScope. Each run starts the simulation fresh.

1. **Bang-bang.** Write `bangBang()` in `ShooterSubsystem.java`. If the flywheel is slower than
   `SHOOT_VELOCITY`, set the duty cycle to `1.0`. Otherwise set it to `0.0`. Run with
   `FLYWHEEL_CONTROL = BANG_BANG`. Look at the graph around the target: it overshoots a little and the
   output flips on and off constantly. That is hard on a real motor and on the battery.
2. **Feedforward.** Write `feedforward()`. The voltage needed to hold a speed is
   `FLYWHEEL_KV` times the speed in *rotations per second* (`SHOOT_VELOCITY` is in radians per second,
   so divide by `2 * Math.PI`). A duty cycle is a fraction of 12 volts, so divide the voltage by 12.
3. **Find `kV`.** Set `FLYWHEEL_CONTROL = FEEDFORWARD`. With `FLYWHEEL_KV = 0.0` the flywheel does
   not even start. Try `0.05`, then `0.10`, then `0.12`. A good first guess is `12 volts` divided by the
   flywheel's top speed in rotations per second (a Kraken X60 does about 100 with no load).
   Watch what happens to the final speed. Then tap **A**: feedforward never notices the dip, so it
   does not fight back.
4. **Add feedback.** Set `FLYWHEEL_CONTROL = PID`, keep your `kV`, and raise `FLYWHEEL_KP` from `0.0`
   upward (`0.05`, `0.2`, `1.0`, `3.0`). Watch the spin-up and the recovery after tapping **A**. A bigger
   `kP` fights error harder. Too big and a real robot starts to buzz or oscillate.
5. **Pick your final settings.** Choose `PID` with a `kV` and `kP` that reach `SHOOT` in under about
   1.5 seconds and recover quickly after a launch. Run the robot fresh one more time with those
   settings, hold the bumper, and tap **A** once.
6. **Click Verify.** It checks your latest run.

## Think about it

- Why does bang-bang spin up *fastest* but still lose to PID everywhere else?
- If the flywheel were twice as heavy, which gain would you have to change, `kV` or `kP`? (Hint: which
  one is "the power to hold a speed"?)
- Last lesson's `DriveDistance` and turn commands in the next lesson use the same idea: an output that
  grows with the error. That is proportional control, `kP`, for position instead of speed.

## Bonus challenges

- The docs have two more tuning exercises on WPILib's own simulator: the
  [elevator](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-elevator.html)
  and the
  [vertical arm](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-vertical-arm.html).
  Do them after this lesson.
- Tune live without rebuilding: in AdvantageScope's Tuning mode change the numbers under
  `Tuning/Subsystem/Shooter/Flywheel/VelocityGains`, then copy the best values into the constants file.
- Change `SHOOT_VELOCITY` to `400`. Which of your gains still work?

## Words to know

- **Error:** the target minus the measurement.
- **Feedforward (`kV`):** a guess made *before* measuring, from knowing the system.
- **Feedback (`kP`):** a correction made *after* measuring, proportional to the error.
- **Gain:** a number that scales part of a controller. Tuning means choosing good gains.
- **Overshoot:** going past the target before settling.
