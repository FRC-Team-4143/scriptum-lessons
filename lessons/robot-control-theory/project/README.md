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
  finished. The shooter still reads its buttons directly; next lesson replaces that with a state machine
  and commands.

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
2. **Feedforward.** Write `feedforward()`: volts = `FLYWHEEL_KV` x speed in rotations per second
   (`SHOOT_VELOCITY` is in rad/s, so divide by `2 * Math.PI`), then divide by 12 for a duty cycle.
3. **Find `kV`.** Set `FLYWHEEL_CONTROL = FEEDFORWARD` and try `FLYWHEEL_KV` = `0.05`, `0.10`, `0.12`.
   Tap **K** and watch the dip.
4. **Add feedback.** Set `FLYWHEEL_CONTROL = PID`, keep your `kV`, and try `FLYWHEEL_KP` = `0.05`, `0.2`,
   `1.0`, `3.0`.
5. **Pick your final settings.** Reach full speed in under about 1.5 s and recover within about 0.8 s
   after a launch (logged as `Subsystem/Shooter/SpinUpSeconds` and `RecoverySeconds`). Run once more
   with those settings, hold the bumper, and tap **K** once.
6. **Click Verify.** It checks your latest run.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- The docs have two more tuning exercises on WPILib's own simulator: the
  [elevator](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-elevator.html)
  and the
  [vertical arm](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-vertical-arm.html).
  Do them after this lesson.
- Tune live without rebuilding: in AdvantageScope's Tuning mode change the numbers under
  `Tuning/Subsystem/Shooter/Flywheel/VelocityGains`, then copy the best values into the constants file.
- Change `SHOOT_VELOCITY` to `400`. Which of your gains still work?

