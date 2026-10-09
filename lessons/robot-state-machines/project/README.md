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
- `OI.java`: connects buttons to commands, and names the aim button (`getAimButton()`, the left bumper).
- `subsystems/drive/DrivetrainSubsystem.java`: the drivetrain has two states, `IDLE` and `ARCADE`, and
  `Robot.java` asks for one or the other as the robot is disabled or enabled. Its `updateLogic()` uses a
  `switch` on the state; read it first, because you write one just like it for the shooter. In Part 3
  you add a third state to it, `AIM`.
- `subsystems/drive/DrivetrainConstants.java`: the drive states, the `GOAL` position on the field and the
  aim gains `AIM_KP`, `AIM_KI` and `AIM_KD`.
- `subsystems/drive/DrivetrainCommands.java`: the aim command.

## Session plan (about 4 hours)

A suggested pace that adds up to a 4 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 25 | Read about the state diagram, boolean logic and lambdas |
| 15 | Read about aiming at a spot and how a PID works |
| 35 | Part 1: transitions |
| 30 | Part 1: what each state does |
| 25 | Part 2: commands |
| 20 | The launch command |
| 20 | Bind the buttons |
| 20 | Try it and watch the states in AdvantageScope |
| 25 | Part 3: build the aim state |
| 20 | Part 3: tune kP, then kD |
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
**Part 3: aim at the goal**

7. **Add the `AIM` state** to `DriveStates` in `DrivetrainConstants.java`.
8. **Build the PID.** In `DrivetrainSubsystem.java` make a `PIDController` called `aimPid` from the three
   `AIM_` gains, with `enableContinuousInput(-Math.PI, Math.PI)` (headings wrap around) and a tolerance.
9. **Find the direction to the goal.** Write `getAngleToGoal()`: the goal's position minus the robot's
   position is an arrow, and `getAngle()` is its heading. (`getAimErrorRadians()` is given to you.)
10. **Write the `AIM` case** in the `switch`: ask the PID for a turn and send it with
    `drive.arcadeDrive(0.0, turn)`. A positive error means the goal is on the left, but a positive turn
    command turns the robot right, so flip the sign. Call `aimPid.reset()` in the other cases.
11. **Write `isAimed()`**: true when the error is smaller than `AIM_TOLERANCE_DEGREES` **and** the robot
    has stopped turning. The autonomous lesson waits on this before it shoots.
12. **Write `DrivetrainCommands.aim()`**: a `startEnd` command that wants `AIM`, then `ARCADE` when it
    ends. The left bumper is already bound to it in `OI.java`.
13. **Tune it by hand.** The starter gains (`AIM_KP = 0.2`) do not aim well. Start, enable Teleop and
    hold the **left bumper (E)**, with `Subsystem/Drivetrain/AimErrorDegrees` and `AimOutput` plotted in
    AdvantageScope. The recipe:
    1. Keep `AIM_KI` and `AIM_KD` at 0. Raise `AIM_KP` (try 0.5, 0.7, 1.0, 2.0, 3.0) until the robot
       reaches the goal quickly but starts to overshoot. Restart the simulation after every change.
    2. Too low and the robot stalls a few degrees short. Too high and it swings past the goal and
       wobbles. Back off to a value that works.
    3. Add `AIM_KD` (0.1, 0.2, 0.3) to damp the swing, so you can keep a larger `AIM_KP`.
    4. Only use `AIM_KI` (tiny, like 0.1) if the robot stops short and stays there.
14. **Click Verify.**

The aim checks look at the simulated robot's *true* heading (`Drive/TrueAimErrorDegrees`), so the small
errors in its estimated pose cannot fool them. You pass **Aims within 2 degrees** by getting within 2
degrees of the goal, and staying there for half a second, within 4 seconds of pressing the bumper. You pass
**No wild overshoot** by never swinging more than 10 degrees past the goal.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- Add a `READY` light: log a boolean `Shooter/ReadyToShoot` that is true while the state is `SHOOT`.
- Make the driver's trigger spin the flywheel to a *different* speed for a close shot.
- Get inside 2 degrees in under 0.5 seconds with less than 3 degrees of overshoot. Which kP and kD?
- Drive somewhere else with the sticks (W/A/S/D), then hold the left bumper again. Does it still face
  the goal? Why is that different from turning to a fixed heading?
- Move `GOAL` in `DrivetrainConstants.java`. Does the same tuning still work?
- `isAimed()` is logged as `Subsystem/Drivetrain/IsAimed`. Make a command that aims, waits for it, then shoots.
