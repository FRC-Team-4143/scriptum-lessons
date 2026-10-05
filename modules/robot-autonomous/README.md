# Autonomous

The first 15 seconds of a match are **autonomous**: the robot drives itself with nobody on the
sticks. In this lesson you will build two autonomous routines from small commands, and let the drive
team pick between them with a drop-down menu.

Companion docs pages: [Autonomous](https://docs.marswars.org/docs/software/training/autonomous) and
[Autonomous (robot dev)](https://docs.marswars.org/docs/software/robot_dev/autonomous). To draw
smooth paths by hand later, see [Choreo](https://docs.marswars.org/docs/software/tools/choreo).

## The big idea

An autonomous routine is a **sequence** of commands. Each command runs until it is finished, then the
next one starts:

```text
drive 5 m  ->  turn to 45 degrees  ->  drive 2 m  ->  shoot
```

Every step uses the **pose** you built earlier: the robot knows where it is, so a command can
compare that to where it *wants* to be and correct the difference. That is feedback control, the same
idea as the flywheel, but for position.

## Java you will learn in this lesson

**Loops again.** You used a `for` loop in the Drive Math and Methods lesson. Here it builds a list of commands instead of adding up numbers:

```java
for (int i = 1; i <= 4; i++) {
  // this runs 4 times, with i equal to 1, then 2, then 3, then 4
}
```

**Writing your own command: inheritance again.** A command is a class that `extends Command`. You
`@Override` the four lifecycle methods you need: `initialize()` (once, at the start), `execute()`
(every 20 ms), `isFinished()` (the command stops when this returns `true`) and `end()` (once, when it
stops). Any code can then treat your class like any other `Command`.

**Collections of commands.** A `SequentialCommandGroup` holds commands and runs them one after another.
You add to it with `addCommands(...)`, which a loop can call as many times as you like.

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

1. **Read `TurnToAngleCommand`.** A command has four parts: `initialize()` (runs once at the start),
   `execute()` (runs every 20 ms), `isFinished()` (says when to stop) and `end()` (runs once when it
   stops). Find where it works out the *error* (how far from the goal) and how the error becomes a
   motor command.
2. **Finish `DriveDistanceCommand`.** Follow the `TODO` comments:
   - `initialize()`: remember the starting pose.
   - `execute()`: the error is the distance still to go. Drive at `DRIVE_KP * error`, no faster
     than `MAX_AUTO_SPEED`. (A helper keeps the speed from dropping below `MIN_AUTO_SPEED`.)
   - `isFinished()`: done when the error is smaller than `DRIVE_TOLERANCE_METERS`.
3. **Build `leftAuto()`** in `Autos.java` with `Commands.sequence(...)`: drive forward 5 meters, turn
   to 45 degrees, drive forward 2 meters, then shoot (`ShooterCommands.shoot().withTimeout(2.0)` shoots
   for two seconds and stops).
4. **Build `rightAuto()`** the same way, but turn to **-45** degrees.
5. **Build `squareAuto()`.** It should drive a 1 meter square: forward 1 meter, then turn to 90
   degrees, forward 1 meter, turn to 180, and so on, four times. Do **not** write the eight
   commands by hand. Create a `SequentialCommandGroup`, then use a `for` loop that runs four times and
   calls `addCommands(...)` with a `DriveDistanceCommand(1.0)` and a `TurnToAngleCommand(90.0 * i)`.
6. **Put all three in the chooser.** In `RobotContainer.java`, add them with
   `autoChooser.addOption(...)`: "Left Auto", "Right Auto" and "Square Auto".
7. **Run them.** Start the robot. In the Driver Station pick **Left Auto** from the autonomous
   chooser, choose **Auto** mode and click **Enable**. Open AdvantageScope's 2D Field and watch
   `Drive/Pose`. Then pick **Right Auto** and run it again, then **Square Auto**: the robot should drive a square and finish back near where it started. The robot should end up about 6.4 m
   forward and 1.4 m to the side, and the shooter should fire.
8. **Click Verify.**

## Bonus challenges

- Make `squareAuto()` take a side length: `squareAuto(double meters)`. Then add a 2 meter square to the chooser too.
- Add a `centerAuto()` that drives straight 4 meters and shoots, and add it to the chooser.
- Add `Commands.waitSeconds(1.0)` before the shot. Where does the extra second go?
- Tune `DRIVE_KP` and `TURN_KP` in `DrivetrainConstants.java`. Too low is slow. Too high overshoots and
  wobbles. What does the robot do with `TURN_KP = 1.5`?

## Words to know

- **Autonomous:** the part of the match where the robot runs on its own.
- **Command sequence:** commands that run one after another.
- **Proportional control (P):** an output that grows with the error.
- **Tolerance:** how close counts as "there."
