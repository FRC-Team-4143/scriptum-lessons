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

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Where the code lives

- `subsystems/shooter/ShooterConstants.java`: pick the style with `FLYWHEEL_CONTROL` and set the gains
  `FLYWHEEL_KV` and `FLYWHEEL_KP`.
- `subsystems/shooter/ShooterSubsystem.java`: `bangBang()` and `feedforward()` (you write these) and
  `runFlywheel()` (it picks the style).
- Everything from last lesson (the drivetrain and shooter subsystems and their buttons) is already
  finished. The shooter still reads its buttons directly; next lesson replaces that with a state machine,
  and makes the robot itself a state machine that the buttons ask for states.
- Last lesson the flywheel ran at a fixed power (`SHOOT_DUTY_CYCLE`). That has no idea how fast the
  wheel is actually spinning, so here the shooter gets a **target speed** (`SHOOT_VELOCITY`) and you
  decide how to reach and hold it. This is where velocity control and closed loop control begin.

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

Hold **U** (right bumper) to spin the flywheel and tap **K** (A) to simulate a launched game piece.
After each change, run the robot and watch `Subsystem/Shooter/FlywheelVelocity` and `FlywheelError` in
AdvantageScope. Each run starts the simulation fresh.

1. **Bang-bang.** Write `bangBang()` in `ShooterSubsystem.java`: duty cycle `1.0` while the flywheel is
   slower than `SHOOT_VELOCITY`, `0.0` otherwise. Run with `FLYWHEEL_CONTROL = BANG_BANG`.
2. **Feedforward.** Write `feedforward()`: volts = `FLYWHEEL_KV` x `SHOOT_VELOCITY` (both in rad/s),
   then divide by 12 for a duty cycle.
3. **Find `kV`.** Set `FLYWHEEL_CONTROL = FEEDFORWARD` and try `FLYWHEEL_KV` = `0.008`, `0.016`, `0.0187`
   (volts per rad/s).
   Tap **K** and watch the dip.
4. **Add feedback.** Set `FLYWHEEL_CONTROL = PID`, keep your `kV`, and try `FLYWHEEL_KP` = `0.008`,
   `0.032`, `0.16`, `0.48`.
5. **Pick your final settings.** Reach full speed in under about 2 s and recover within about 1 s
   after a launch (logged as `Subsystem/Shooter/SpinUpSeconds` and `RecoverySeconds`). Run once more
   with those settings, hold the bumper, and tap **K** once.
6. **Click Verify.** It checks your latest run.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Advanced challenge (optional)

This one is **optional**. It's for students who finish early and want something harder. It isn't needed to finish the lesson, and Dozer doesn't walk through it: you work it out, the way you would on a real robot. It shows as **Optional** in the Checkpoints list.

### Write your own PID

The motor controller has been running PID for you. Write one yourself, in a new file,
`subsystems/shooter/FlywheelPid.java`, in plain Java:

```java
public class FlywheelPid {
  public FlywheelPid(double kV, double kP, double kI, double kD) { ... }
  /** Returns the volts for the motor, from -12 to 12. */
  public double calculate(double target, double measured, double dtSeconds) { ... }
  /** Forgets the added-up error and the last speed. */
  public void reset() { ... }
}
```

```text
volts = kV x target  +  kP x error  +  kI x (error added up over time)  -  kD x (how fast measured is changing)
```

Three things separate a real controller from the formula:

1. **Clamp.** The battery only has 12 volts.
2. **Anti-windup.** During a long spin-up the error is huge, so the added-up error grows and grows, and later
   overshoots the target. Only add up the error while it's small (within 30 rad/s of the target) and the
   output isn't stuck at the limit.
3. **No derivative kick.** The `kD` part uses the change in `measured`, not in the error, so changing the target doesn't slam the motor. The first call after a reset has no change to look at, and a `dtSeconds` of 0 must not divide by zero.

Then use it. In `ShooterSubsystem`, make one from your gains, and in the `PID` case of `runFlywheel()` send
`calculate(CONSTANTS.SHOOT_VELOCITY, flywheel_.getCurrentVelocity(), 0.02) / 12.0` to `setTargetDutyCycle`, instead
of `setTargetVelocity`. Call `reset()` whenever the shoot button is up. A `kI` of about `0.4` works well. Leave
`FLYWHEEL_CONTROL` on `PID`, and check your numbers still pass the other checkpoints.

**How it's checked:** Verify tests your class on exact numbers, then flies it against a model of the real
flywheel: a long spin-up, a `kV` that's wrong on purpose, and a launch. No simulator needed, but do run it on the robot: that's the fun part.

## Bonus challenges

- The docs have two more tuning exercises on WPILib's own simulator: the
  [elevator](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-elevator.html)
  and the
  [vertical arm](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-vertical-arm.html).
  Do them after this lesson.
- Tune live without rebuilding: in AdvantageScope's Tuning mode change the numbers under
  `Tuning/Subsystem/Shooter/Flywheel/VelocityGains`, then copy the best values into the constants file.
- Change `SHOOT_VELOCITY` to `400`. Which of your gains still work?
