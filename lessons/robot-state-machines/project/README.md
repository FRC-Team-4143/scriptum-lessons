# State Machines and Commands

Your flywheel now holds its speed well, but the shooter is still clumsy: the driver has to hold one
button to spin up and another to feed, and can feed a game piece into a wheel that is not up to speed
yet. The subsystem also reads the buttons itself, which gets messy as robots grow. In this lesson you will give the shooter a
**state machine** and control it with **commands**.

Companion docs pages: [State Machines and Commands](https://docs.marswars.org/docs/software/training/state-machines-commands),
[State Machines](https://docs.marswars.org/docs/software/controls/state-machines),
[Commands](https://docs.marswars.org/docs/software/robot_dev/commands)

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Where the code lives

Everything is under `src/main/java/frc/robot/`:

- `subsystems/shooter/ShooterSubsystem.java`: the state machine.
- `subsystems/shooter/ShooterCommands.java`: the commands.
- `subsystems/shooter/ShooterConstants.java`: the states and thresholds.
- `OI.java`: connects buttons to commands.
- `subsystems/drive/DrivetrainSubsystem.java`: already finished, with one new idea. The drivetrain now
  has two states, `IDLE` and `ARCADE`, and `Robot.java` asks for one or the other as the robot is
  disabled or enabled. Its `updateLogic()` uses a `switch` on the state; read it first, because you
  write one just like it for the shooter.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 25 | Read about the state diagram, boolean logic and lambdas |
| 35 | Part 1: transitions |
| 30 | Part 1: what each state does |
| 25 | Part 2: commands |
| 20 | The launch command |
| 20 | Bind the buttons |
| 20 | Try it and watch the states in AdvantageScope |
| 5 | Verify |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

**Part 1: the state machine**

1. **`handleStateTransition()`** in `ShooterSubsystem.java`. Set `system_state_` to the state the shooter
   is allowed to be in. The rules are in the comment above the method.
2. **`updateLogic()`.** Write a `switch` with a case for each state: `IDLE` stops everything,
   `SPIN_UP` runs only the flywheel, `SHOOT` runs the flywheel and the roller.

**Part 2: commands**

3. **`shoot()`** in `ShooterCommands.java`: sets the wanted state to `SHOOT` when it starts and `IDLE`
   when it ends.
4. **`simulateBallLaunch()`** in `ShooterCommands.java`: an instant command that calls the
   subsystem's `simulateBallLaunch()`.
5. **Bind them** in `OI.java`: right bumper `whileTrue(...)` shoot, **A** `onTrue(...)` simulate a launch.
6. **Try it.** Start, enable Teleop, hold **U**. Watch `State` in AdvantageScope go `SPIN_UP` then
   `SHOOT`. Tap **K**: it falls back to `SPIN_UP`, then returns to `SHOOT`.
7. **Click Verify.**

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- Add a `READY` light: log a boolean `Shooter/ReadyToShoot` that is true while the state is `SHOOT`.
- Make the driver's trigger spin the flywheel to a *different* speed for a close shot.
