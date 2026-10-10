# State Machines

Your flywheel now holds its speed well, but the shooter is still clumsy: the driver has to hold one
button to spin up and another to feed, and can feed a game piece into a wheel that is not up to speed
yet. The subsystem also reads the buttons itself, which gets messy as robots grow. In this lesson you will give the shooter a
**state machine**, and make the robot itself a state machine too: buttons ask the **robot** for a
state, and the robot asks each subsystem for the state it needs.

Companion docs pages: [State Machines](https://docs.marswars.org/docs/software/training/state-machines),
[State Machines (controls)](https://docs.marswars.org/docs/software/controls/state-machines),
[Commands](https://docs.marswars.org/docs/software/robot_dev/commands)

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Where the code lives

Everything is under `src/main/java/frc/robot/`:

- `subsystems/shooter/ShooterSubsystem.java`: the shooter's state machine.
- `subsystems/shooter/ShooterConstants.java`: the shooter's states and thresholds.
- `RobotContainer.java`: the robot's own state machine (the "super state machine"). It lists the
  subsystems, and every loop, before they run, it asks each one for the state the robot needs.
- `RobotStates.java`: the robot's states, `IDLE`, `DRIVE` and `SHOOT`. `Robot.java` asks for `IDLE`
  when the robot is disabled and `DRIVE` when teleop starts.
- `OI.java`: connects buttons to robot states.
- `subsystems/drive/DrivetrainSubsystem.java`: already finished, with one new idea. The drivetrain now
  has two states, `IDLE` and `ARCADE`. Its `updateLogic()` uses a `switch` on the state; read it
  first, because you write one just like it for the shooter and for the robot.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 25 | Read about the state diagram, boolean logic and lambdas |
| 35 | Part 1: transitions |
| 30 | Part 1: what each state does |
| 25 | Part 2: the robot's state machine |
| 20 | The robot's states |
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

**Part 2: the robot's state machine**

3. **`updateLogic()`** in `RobotContainer.java`: a `switch (getSystemState())` with a case for each
   robot state. Each case asks the drivetrain (`drive_`) and the shooter (`shooter_`) for a state
   with `setWantedState(...)`. The comment above the method lists them.
4. **Bind the buttons** in `OI.java`. The right bumper: `whileTrue(Commands.startEnd(...))`, asking
   the robot for `SHOOT` when it starts and `DRIVE` when it ends. **A**: `onTrue(Commands.runOnce(...))`
   that calls the shooter's `simulateBallLaunch()`.
5. **Try it.** Start, enable Teleop, hold **U**. Watch `Robot/State` go to `SHOOT`, and the shooter's
   `State` go `SPIN_UP` then `SHOOT`. Tap **K**: it falls back to `SPIN_UP`, then returns to `SHOOT`.
6. **Why bother?** The button never talks to the shooter. Later, an autonomous routine will ask the
   robot for the same `SHOOT` state, so shooting is written once.
7. **Click Verify.**

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- Add a `READY` light: log a boolean `Shooter/ReadyToShoot` that is true while the state is `SHOOT`.
- Make the driver's trigger spin the flywheel to a *different* speed for a close shot.
