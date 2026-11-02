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
- `RobotConstants.java`: the robot's own constants, starting with its states, the `RobotStates` enum:
  `IDLE`, `DRIVE` and `SHOOT` (the same way `ShooterConstants` holds `ShooterStates`). `Robot.java` asks for `IDLE`
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
| 20 | Part 1: what each state does |
| 30 | Part 1: transitions |
| 15 | Part 1: test the shooter |
| 25 | Part 2: the robot's state machine |
| 20 | The robot's states |
| 20 | Bind the buttons |
| 20 | Try it and watch the states in AdvantageScope |
| 5 | Verify |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

**Part 1: the state machine**

1. **`updateLogic()`** in `ShooterSubsystem.java`. This `switch` says what the motors do in each state.
   `IDLE` is done for you. Add `SPIN_UP`, which runs only the flywheel, and `SHOOT`, which runs the
   flywheel and the roller.
2. **`handleStateTransition()`.** This sets `system_state_` to the state the shooter is allowed to be
   in. It's written as a **table**: a `switch` on `system_state_` (where the shooter is), and inside each
   case a `switch` on `wanted` (what the robot asked for). The `IDLE` row is done for you. Copy its shape
   for `SPIN_UP` and `SHOOT`, using the rows in the comment above the method. A request with no row is
   ignored, which is how you forbid a move.
3. **Test the shooter.** A temporary line in `RobotContainer.java` lets the right bumper ask the shooter
   directly. Start, enable Teleop and hold **U**. The shooter's `State` should go `SPIN_UP`, then `SHOOT`.
   Click **Verify**: **Spins up before shooting** should pass. The robot won't drive yet: that comes in
   Part 2.

**Part 2: the robot's state machine**

4. **Delete the temporary code**: the `shooter_.setWantedState(OI.getTestShootButton() ...)` line in
   `RobotContainer.java`, and `getTestShootButton()` in `OI.java`. Buttons should ask the robot for a
   state, not reach into a subsystem.
5. **`updateLogic()`** in `RobotContainer.java`. `IDLE` is done for you. Add a case for `DRIVE` and a
   case for `SHOOT`. Each one asks the drivetrain (`drive_`) and the shooter (`shooter_`) for a state
   with `setWantedState(...)`. The comment above the method lists them.
6. **Bind the buttons** in `OI.java`. The right bumper: `whileTrue(Commands.startEnd(...))`, asking
   the robot for `SHOOT` when it starts and `DRIVE` when it ends. **A**: `onTrue(Commands.runOnce(...))`
   that calls the shooter's `simulateBallLaunch()`.
7. **Try it.** Start, enable Teleop, hold **U**. Watch `Robot/State` go to `SHOOT`, and the shooter's
   `State` go `SPIN_UP` then `SHOOT`. Tap **K**: it falls back to `SPIN_UP`, then returns to `SHOOT`.
8. **Why bother?** The button no longer talks to the shooter. Later, an autonomous routine will ask the
   robot for the same `SHOOT` state, so shooting is written once.
9. **Click Verify.**

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Advanced challenge (optional)

This one is **optional**. It's for students who finish early and want something harder. It isn't needed to finish the lesson, and Dozer doesn't walk through it: you work it out, the way you would on a real robot. It shows as **Optional** in the Checkpoints list.

### Clear a jam

Game pieces jam. Add a sequence the driver starts with **one tap**, that finishes by itself. It's made of
states, so it's written once and an auto could use it too.

1. **Shooter states.** Add `SPIN_DOWN` (flywheel off, roller still) and `UNJAM` (flywheel off, roller
   backward at about `-0.5`). Asking the shooter for `UNJAM` always goes through `SPIN_DOWN`. It stays there until
   the flywheel is under `100` rad/s, because a roller running backward into a fast wheel is how parts break. It stays in
   `UNJAM` for `0.5` seconds (a `Timer`), then goes to `IDLE`.
2. **A robot state.** Add `UNJAM` to `RobotStates`. Asking for it must not be cancelled partway: once it starts, it
   finishes, even if the driver lets go of the right bumper (which asks for `DRIVE`). Then the robot goes
   back to `DRIVE` on its own. You will need to override `handleStateTransition` in `RobotContainer`, and give the shooter a way to say it's done.
3. **The button.** **Y** (`I` on the keyboard), with `onTrue`.

Follow the team's rule: every condition goes in `handleStateTransition`, and `updateLogic` is a plain `switch`
that says what each state does. The state names must be exactly `SPIN_DOWN` and `UNJAM`.

**How it's checked:** with the simulator running, hold **U** until the flywheel is at speed, tap **I**, and let
go of everything. Watch `Robot/State` and `Subsystem/Shooter/State`. `Check/Challenge/UnjamStep` says how far you got
(5 is all the way), `UnjamSeconds` how long the roller reversed, and `UnjamMistakes` how often the roller ran
backward with the flywheel still fast. Press Start to clear a mistake.

## Bonus challenges

- Add a `READY` light: log a boolean `Shooter/ReadyToShoot` that is true while the state is `SHOOT`.
- Make the driver's trigger spin the flywheel to a *different* speed for a close shot.
