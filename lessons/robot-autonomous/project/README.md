# Choreo Autonomous

The first 15 seconds of a match are **autonomous**: nobody holds a controller and the robot drives
itself. The competition robot's autonomous routines are built in two jobs: first you **draw paths** in
Choreo, then you **write the sequence** of commands that drives them. In this lesson you do both. Your
robot drives to a game piece, drives to a scoring spot, **aims at the goal** with the aim state you built
and tuned in the Computer Vision lesson, and **shoots**.

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Before you start

- You finished **Computer Vision**. This lesson reuses your `aim()` command and your `isAimed()` method,
  and the robot's position estimate, which now comes from the AprilTag cameras as well as the wheels.
  They must work (hold the left bumper, **E** on the keyboard, in Teleop to test the aim).
- You finished the **Choreo** lesson. It teaches the Choreo pane: waypoints, constraints and Generate.
  This lesson only says which waypoints to draw.

## Where things live

Everything is under `src/main/`:

- `deploy/choreo/robot.chor` is the Choreo project: a **Differential** drive with the robot's numbers
  (24 inch track width, 50 kg) and four named poses, `Start`, `Pickup`, `ScoreSpot` and `Goal`. The paths
  you draw are saved next to it as `ToPickup.traj` and `ToScore.traj`.
- `java/frc/robot/autos/Autos.java` is the routine, and **the only Java file you edit**. It has two TODOs.
- `java/frc/robot/FieldTargets.java` has the same poses in code, so the checks can measure how close the
  robot got. Provided, don't edit.
- `subsystems/drive/DrivetrainCommands.java` has `followPath(...)` (play a path back) and your `aim()`.
  `DifferentialPathFollower.java` and the Choreo methods in `DrivetrainSubsystem.java` turn the path into
  wheel speeds. They are already tuned: read them to see how a path becomes motor power.
- `subsystems/localization/LocalizationSubsystem.java` is the pose estimate you finished in the Computer
  Vision lesson (wheels plus cameras). The path follower, `aim()` and the distance checks all use it.
  Read it, don't change it.
- `subsystems/shooter/ShooterCommands.java` has `shoot()`.
- `autos/DriveDistanceCommand.java` and `autos/TurnToAngleCommand.java` are finished example commands
  that the Dozer guides about commands use. Read them, don't change them.

## Session plan (about 3 hours)

A suggested pace. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 15 | Read: Autos = paths + a sequence |
| 30 | Read about commands: inside a command, command types, dots that change commands |
| 25 | Part 1: draw and generate **ToPickup** |
| 20 | Part 1: draw and generate **ToScore** |
| 15 | Part 2: load the paths |
| 30 | Part 2: write the sequence |
| 20 | Part 3: run it and watch it in AdvantageScope |
| 20 | Fix whatever a check says is wrong |
| 5 | Verify |

Go slower on anything that is new. The stretch ideas at the end are not expected.

## What you need to do

**Part 1: draw the paths (Choreo pane)**

1. **Make a path named `ToPickup`.** Press **+** in the paths list and rename it. The spelling and the
   capital letters must be exact, because your Java loads the path by that name.
2. **Give it three waypoints:** a **pose** waypoint on **Start** (x 1.5, y 1.5, heading 0 degrees), a
   **translation** waypoint anywhere between (x 3.5, y 2.0 works), and a **pose** waypoint on **Pickup**
   (x 5.5, y 2.5, heading 0 degrees). Click a waypoint in the list and type its numbers instead of
   dragging it by eye.
3. **Optional: add a Max Velocity** constraint of about 1.8 m/s, as you did in the Choreo lesson. A gentler
   robot is easier for the follower to keep on the line.
4. **Press Generate.** The path is only usable once Choreo has solved it.
5. **Make a second path named `ToScore`.** It must start **where ToPickup ended**: a pose waypoint on
   Pickup (x 5.5, y 2.5, heading 0), a translation waypoint (x 6.0, y 3.8 works) and a pose waypoint on
   **ScoreSpot** (x 6.5, y 5.0, heading 90 degrees). Generate it too.

**Part 2: write the sequence (`Autos.java`)**

6. **TODO 1: load both paths** by name with `loadTrajectory("ToPickup")` and `loadTrajectory("ToScore")`.
7. **TODO 2: add the commands**, in this order, inside `addCommands(...)`:
   1. follow the first path: `DrivetrainCommands.followPath(getTrajectory("ToPickup"))`
   2. wait half a second, standing in for picking up the game piece: `Commands.waitSeconds(0.5)`
   3. follow the second path
   4. aim at the goal: `DrivetrainCommands.aim()`, ended with `.until(...)` the drivetrain's `isAimed`
      method, and `.withTimeout(3.0)` so a robot that cannot aim cannot hold up the routine
   5. shoot: `ShooterCommands.shoot()` with a `.withTimeout(2.0)`
8. **Add the imports** for the classes you used (hover over a red name and use the quick fix).

**Part 3: run it**

9. Press **Start** and wait for **Running**. In Elastic, add a **ComboBox Chooser** widget for
   **Auto Choices** (under SmartDashboard) and choose **Pickup And Score** as the
   autonomous routine (the default, Do Nothing, leaves the robot still). Then in the Driver Station pick **Auto** and press **Enable**. Keep your hands off the keys: the routine drives
   itself. (To practice by hand in **Teleop**, drive with **W/S** and turn with **A/D**, the left stick;
   the left bumper, **E**, aims at the goal, and the right bumper, **U**, shoots.)
10. **Watch it in AdvantageScope** (below). Fix what looks wrong, press Start again, and run again.
11. **Click Verify.**

## Watching it in AdvantageScope

On the 2D Field tab add these, and plot the others:

- `Drive/TruePose`: where the simulated robot **really** is.
- `Subsystem/Localization/Pose`: where the robot **thinks** it is, from your Localization subsystem.
  The cameras keep it close to the true pose: about 1 to 9 centimeters apart while the robot drives,
  and about 1 centimeter once it stops. It is not exactly the same, so the checks use the true pose.
- `Subsystem/Drivetrain/Choreo/Trajectory`: the path being followed, and
  `Subsystem/Drivetrain/Choreo/DesiredPose`: where the path says the robot should be right now.
- `Drive/DistanceToPickup` and `Drive/DistanceToScoreSpot`: true distances in meters, to plot.
- `Drive/TrueAimErrorDegrees`: how far the robot truly is from facing the goal.
- `Subsystem/Shooter/ShotCount` and `Subsystem/Shooter/AimErrorAtShotDegrees`.
- `Check/...`: the smallest and biggest values since Start, which Verify reads.

## How each checkpoint is checked

| Checkpoint | What it looks for |
| --- | --- |
| **Two paths drawn** | Two generated paths, each with at least three waypoints. |
| **Paths named ToPickup and ToScore** | Paths named exactly `ToPickup` and `ToScore`, and `Autos.java` loads both by those names. |
| **Paths go where they should** | ToPickup starts on Start and ends on Pickup. ToScore starts where ToPickup ended and ends on ScoreSpot. Each end must be within about 30 centimeters, the join within 15. |
| **Autonomous sequence written** | `Autos.java` drives ToPickup, then ToScore, then aims (with `isAimed`), then shoots, in that order. |
| **Reaches the pickup** | The robot's true position got within 0.4 meters of Pickup during a run. |
| **Reaches the scoring spot** | The robot's true position got within 0.4 meters of ScoreSpot. |
| **Aims before it shoots** | When each shot started, the robot truly faced the goal within 5 degrees. |
| **Shoots** | The routine shot at least once. |

The last four read a running simulation, so run **Pickup And Score** in Auto before you Verify. They
remember the best the robot did since you pressed **Start**, so after a fix, press Start again.

If a run check fails, read it as a clue. Pickup failed: the first path or its `followPath`. Scoring spot
failed: the second path never ran, or doesn't start where the first ended. Aim failed: the aim step is
missing, comes after the shot, or has no `.until(...isAimed)`. Shoots failed: the routine ended before
`shoot()`.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Java you will meet

- A routine **extends `Auto`**, which holds a list of commands run in order (`addCommands(...)`).
- **Decorators** change a command: `.until(...)` ends it on a condition and `.withTimeout(...)` ends it
  after a time.
- A **method reference** like `DrivetrainSubsystem.getInstance()::isAimed` hands over a method so a
  command can call it again and again.

## Stretch

- **Event markers.** In Choreo, a path can carry named markers. The competition robot binds markers such
  as `Intake Out` and `Shoot` to actions that run partway along a path. `DrivetrainSubsystem` already has
  `getChoreoEventTimeTrigger("Name")`, which turns true when the path passes a marker. Add a marker to
  ToScore and make something log a message when it is reached.
- Spin the flywheel up **while** the robot drives the second path, with `.alongWith(...)`, so the shot
  starts sooner.
- Change the Max Velocity and see how it changes the total time (`Subsystem/Drivetrain/Choreo/TotalTime`).
- Move `GOAL` in `DrivetrainConstants.java`. Does the routine still aim before it shoots?
- Draw a third path that goes back to Start, and drive it at the end.
