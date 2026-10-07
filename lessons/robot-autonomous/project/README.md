# Autonomous

The first 15 seconds of a match are **autonomous**: the robot drives itself with nobody on the
sticks. In this lesson you will build two autonomous routines from small commands, and let the drive
team pick between them with a drop-down menu.

Companion docs pages: [Autonomous](https://docs.marswars.org/docs/software/training/autonomous) and
[Autonomous (robot dev)](https://docs.marswars.org/docs/software/robot_dev/autonomous). To draw
smooth paths by hand later, see [Choreo](https://docs.marswars.org/docs/software/tools/choreo).

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Where the code lives

- `autos/DriveDistanceCommand.java` drives straight for a distance. You finish it.
- `autos/TurnToAngleCommand.java` turns to a heading. It is already finished: read it first and use it
  as a model.
- `autos/Autos.java` builds the routines out of commands. You write it.
- `RobotContainer.java` holds the autonomous chooser. You add your routines to it.
- `Robot.java` runs the chosen routine when autonomous starts. It is already done.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Read about commands, loops and sequences |
| 15 | Step 1: read TurnToAngleCommand |
| 45 | Step 2: finish DriveDistanceCommand |
| 25 | Steps 3-4: left and right autos |
| 25 | Step 5: the square auto |
| 10 | Step 6: the chooser |
| 25 | Step 7: run and watch each auto |
| 10 | Verify and wrap-up |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

1. **Read `TurnToAngleCommand`.** Find where it works out the error and turns it into a motor command.
2. **Finish `DriveDistanceCommand`** by following its `TODO` comments:
   - `initialize()`: remember the starting pose.
   - `execute()`: error = distance still to go; drive at `DRIVE_KP * error`, no faster than
     `MAX_AUTO_SPEED`.
   - `isFinished()`: done when the error is below `DRIVE_TOLERANCE_METERS`.
3. **`leftAuto()`** in `Autos.java`, with `Commands.sequence(...)`: drive 5 m, turn to 45 degrees,
   drive 2 m, then `ShooterCommands.shoot().withTimeout(2.0)`.
4. **`rightAuto()`**: the same, but turn to **-45** degrees.
5. **`squareAuto()`**: a 1 m square (forward 1 m, turn to 90, forward 1 m, turn to 180, ...). Use a
   `SequentialCommandGroup` and a `for` loop that runs four times, calling `addCommands(...)` with a
   `DriveDistanceCommand(1.0)` and a `TurnToAngleCommand(90.0 * i)`.
6. **`RobotContainer.java`**: add "Left Auto", "Right Auto" and "Square Auto" with
   `autoChooser.addOption(...)`.
7. **Run them.** Start, pick an auto in the Driver Station chooser, choose **Auto**, **Enable**, and
   watch `Drive/Pose` on the 2D Field. Left Auto should end about 6.4 m forward and 1.4 m to the side
   and fire; Square Auto should finish near where it started.
8. **Click Verify.**

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- Make `squareAuto()` take a side length: `squareAuto(double meters)`. Then add a 2 meter square to the chooser too.
- Add a `centerAuto()` that drives straight 4 meters and shoots, and add it to the chooser.
- Add `Commands.waitSeconds(1.0)` before the shot. Where does the extra second go?
- Tune `DRIVE_KP` and `TURN_KP` in `DrivetrainConstants.java`. Too low is slow. Too high overshoots and
  wobbles. What does the robot do with `TURN_KP = 1.5`?

