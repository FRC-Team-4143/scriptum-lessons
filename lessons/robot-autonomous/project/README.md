# Autonomous

The first 15 seconds of a match are **autonomous**: nobody holds a controller and the robot drives
itself. The competition robot's autonomous routines are built in two jobs: first you **draw paths** in
Choreo, then you **build a behavior tree** in Arbor that drives them. In this lesson you do both. Your
robot drives to a game piece, drives to a scoring spot, **aims at the goal** with the aim state you built
and tuned in the Computer Vision lesson, and **shoots**. Last, you teach the robot's `AIM` state to wait
until the robot is really aimed, so the tree doesn't shoot too early.

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Before you start

- You finished **Computer Vision**. This lesson reuses the robot's `AIM` state and your `isAimed()` method,
  and the robot's position estimate, which now comes from the AprilTag cameras as well as the wheels. In
  the simulator the cameras send their tag solutions through MW-Lib's proxy server, like real camera computers.
  They must work (hold the left bumper, **E** on the keyboard, in Teleop to test the aim).
- You finished the **Choreo** lesson. It teaches the Choreo pane: waypoints, constraints and Generate.
  This lesson only says which waypoints to draw.
- You finished the **Arbor** lesson. It teaches the Arbor pane: trees, nodes, ports, Sequence, Timeout
  and Sleep. This lesson only says which steps to put in the tree.

## Where things live

Everything is under `src/main/`:

- `deploy/choreo/robot.chor` is the Choreo project: a **Differential** drive with the robot's numbers
  (24 inch track width, 50 kg) and four named poses, `Start`, `Pickup`, `ScoreSpot` and `Goal`. The paths
  you draw are saved next to it as `ToPickup.traj` and `ToScore.traj`.
- `deploy/autos/trees/PickupAndScore.xml` is the routine, a **behavior tree**. You build it in the Arbor
  pane, which saves it here. The starter tree only drives the first path. Every tree in this folder is a
  choice in the dashboard's **Auto Chooser**, named after its file.
- `deploy/autos/nodes.xml` tells Arbor which robot steps exist (`FollowPath`, `SetWantedState`,
  `IsSystemState`). Provided, don't edit.
- `java/frc/robot/RobotContainer.java` is the robot's state machine. Your tree asks it for robot states
  (`AIM`, `SHOOT`, back to `AUTO`) with `SetWantedState`, the same way the buttons do with
  `setWantedState(...)`. Asking is instant, like a button press. To wait until the robot gets there,
  the tree asks `IsSystemState` again and again. One `SHOOT` state serves both the right bumper and your tree, so shooting is
  written once. It has **one TODO**, in `handleStateTransition`: the only Java you write.
- `java/frc/robot/autos/AutoNodes.java` is where the robot's steps come from: `FollowPath` asks
  RobotContainer to follow a path and succeeds once the robot has arrived. Read it, don't change it.
- `java/frc/robot/FieldTargets.java` has the same poses in code, so the checks can measure how close the
  robot got. Provided, don't edit.
- `DifferentialPathFollower.java` and the Choreo methods in `DrivetrainSubsystem.java` turn the path into
  wheel speeds. They are already tuned: read them to see how a path becomes motor power.
- `subsystems/localization/LocalizationSubsystem.java` is the pose estimate you finished in the Computer
  Vision lesson (wheels plus cameras). The path follower, the aim and the distance checks all use it.
  Read it, don't change it.

## Session plan (about 3 hours)

A suggested pace. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 15 | Read: Autos = paths + a tree |
| 20 | Read: behavior trees, Timeout and Sleep (the Arbor lesson's guides, again) |
| 25 | Part 1: draw and generate **ToPickup** |
| 20 | Part 1: draw and generate **ToScore** |
| 30 | Part 2: build the tree in Arbor |
| 25 | Part 3: make AIM wait until aimed |
| 20 | Part 4: run it and watch it in Arbor and AdvantageScope |
| 20 | Fix whatever a check says is wrong |
| 5 | Verify |

Go slower on anything that is new. The stretch ideas at the end are not expected.

## What you need to do

**Part 1: draw the paths (Choreo pane)**

1. **Set up the robot first.** In the Choreo pane press the menu (the three-line icon at the top left,
   next to the Choreo title), then **Document Settings**, then the **Robot Config** tab. Slide **Drive
   Type** from Swerve to **Differential**, then type these numbers with their units (Choreo shows its own defaults, not
   this project's, and converts what you type to the unit it shows):

   | Setting | Value |
   | --- | --- |
   | Drive Type | Differential |
   | Trackwidth | 0.61 m |
   | Mass | 50 kg |
   | MOI | 7.5 kg m^2 |
   | Bumpers (front, back, sides) | 0.43 m |
   | Wheel Radius | 0.0762 m |
   | Motor Rev / Wheel Rev | 8.45 |
   | Motor Max Speed | 628 rad / s |
   | Motor Max Torque | 1.0 N*m |
   | Wheel COF | 1.5 |

   If you skip this, Choreo makes Swerve paths and the Verify check **Two paths drawn** says the paths were
   not made for a Differential drivetrain (the `paths-drawn` check).

2. **Make a path named `ToPickup`.** Press **+** in the paths list and rename it. The spelling and the
   capital letters must be exact, because your Java loads the path by that name.
3. **Give it three waypoints:** a **pose** waypoint on **Start** (x 1.5, y 1.5, heading 0 rad), a
   **translation** waypoint anywhere between (x 3.5, y 2.0 works), and a **pose** waypoint on **Pickup**
   (x 5.5, y 2.5, heading 0 rad). Click a waypoint in the list and type its numbers instead of
   dragging it by eye.
4. **Optional: add a Max Velocity** constraint of about 1.8 m/s, as you did in the Choreo lesson. A gentler
   robot is easier for the follower to keep on the line.
5. **Press Generate.** The path is only usable once Choreo has solved it.
6. **Make a second path named `ToScore`.** It must start **where ToPickup ended**: a pose waypoint on
   Pickup (x 5.5, y 2.5, heading 0 rad), a translation waypoint (x 6.0, y 3.8 works) and a pose waypoint on
   **ScoreSpot** (x 6.5, y 5.0, heading 1.5708 rad, which is pi/2). Generate it too.

**Part 2: build the tree (Arbor pane)**

7. **Open the tree.** In the Arbor pane's **Trees** list, click **PickupAndScore**. It is a `Sequence`
   with one step, `FollowPath` with the `trajectory` `ToPickup`.
8. **Add the steps**, in this order, under the Sequence (drag them from the **Nodes** list, then fill in
   their ports on the right):
   1. after the first path, a `Sleep` with `msec` `500`, standing in for picking up the game piece
   2. a second `FollowPath` with `trajectory` `ToScore`
   3. a `SetWantedState` with `state` `AIM`. Asking succeeds at once, before the robot has turned
   4. the wait for AIM: a `Timeout` with `msec` `3000`; **inside it** a `RetryUntilSuccessful` with
      `num_attempts` `-1` (retry forever); and **inside that** an `IsSystemState` with `state` `AIM`.
      `IsSystemState` answers "is the robot in AIM right now?", the retry asks again every tick until
      the answer is yes, and the Timeout gives up after 3 seconds, so a robot that cannot aim cannot
      hold up the routine
   5. a `SetWantedState` with `state` `SHOOT`
   6. a `Sleep` with `msec` `2000`: asking for SHOOT succeeds at once too, so this is what keeps it
      shooting for a while
   7. a `SetWantedState` with `state` `AUTO`, which stops the shooter
9. **Save** (Ctrl+S). Arbor writes the tree to `deploy/autos/trees/PickupAndScore.xml`, the file the
   robot runs; you never edit it by hand. The robot reads it every time it starts, so **changing a tree
   needs no rebuild**.

**Part 3: make AIM wait until aimed (`RobotContainer.java`)**

Run the tree now and the robot shoots without turning. The tree waits until `IsSystemState AIM` is
true, and right now `handleStateTransition` puts the robot in AIM the moment it is asked. So the wait
ends at once and the tree moves straight on to SHOOT.

10. **Find the TODO** in `handleStateTransition`, in `case AIM:`. Make AIM a sequence with a **step
    state**, like the shooter's SPIN_UP: while the robot is still turning it is in `RobotStates.AIMING`,
    and only once `drive_.isAimed()` is true is it in `RobotStates.AIM`. Write it as an `if`/`else` that
    sets `system_state_` in each branch, and keep the `break;`.

Both states turn toward the goal (see `updateLogic`), so the left bumper still aims the same way. The
difference is *when* the robot counts as being in AIM, and that is what the tree waits for. This is how
the competition robot writes its sequences: once, in the robot's state machine, used by buttons and
autos alike.

**Part 4: run it**

11. Press **Start** and wait for **Running** (Java changes need a rebuild). In Elastic, add a **ComboBox
   Chooser** widget for **Auto Chooser** (under SmartDashboard) and choose **PickupAndScore** (the
   default, Do_Nothing, leaves the robot still). Keep the Driver Station on a **Blue** alliance: the paths
   are drawn for blue. Then in the Driver Station pick **Auto** and press **Enable**. Keep your hands off
   the keys: the tree drives the robot. (To practice by hand in **Teleop**, drive with **W/S** and turn
   with **A/D**, the left stick; the left bumper, **E**, aims at the goal, and the right bumper, **U**,
   shoots.)
12. **Watch the tree in Arbor.** Press **Connect** in Arbor's toolbar, then run again. Each step turns
   orange while it runs, then green (SUCCESS) or red (FAILURE). Each run is also saved in `logs/bt` as a
   `.btlog` file, which Arbor can replay step by step.
13. **Watch it in AdvantageScope** (below). Fix what looks wrong, press Start again, and run again.
14. **Click Verify.**

## Watching it in AdvantageScope

On the 2D Field tab add these, and plot the others:

- `Drive/TruePose`: where the simulated robot **really** is.
- `Subsystem/Localization/Pose`: where the robot **thinks** it is, from your Localization subsystem.
  The cameras keep it close to the true pose: usually within a few centimeters, and up to about 20
  centimeters apart while the robot drives, and within a few centimeters once it stops. It is not exactly the same, so the checks use the true pose.
- `Subsystem/Localization/VisibleTags`: the AprilTags the cameras are using right now. Drag it onto the
  field's Vision Targets to see them.
- `Subsystem/Drivetrain/Choreo/Trajectory`: the path being followed, and
  `Subsystem/Drivetrain/Choreo/DesiredPose`: where the path says the robot should be right now.
- `Drive/DistanceToPickup` and `Drive/DistanceToScoreSpot`: true distances in meters, to plot.
- `Drive/TrueAimErrorRad`: how far the robot truly is from facing the goal, in radians.
- `Subsystem/Shooter/ShotCount` and `Subsystem/Shooter/AimErrorAtShotRad`.
- `Robot/State`: the robot state right now. During the aim you should see `AIMING`, then `AIM`.
- `BehaviorTree/PickupAndScore/Result`: `SUCCESS` or `FAILURE` once the tree has finished, and
  `BehaviorTree/PickupAndScore/Error` if the tree could not load (a misspelled node or a missing path).
- `Check/...`: the smallest and biggest values since Start, which Verify reads.

## How each checkpoint is checked

| Checkpoint | What it looks for |
| --- | --- |
| **Two paths drawn** | Two generated paths, each with at least three waypoints. |
| **Paths named ToPickup and ToScore** | Paths named exactly `ToPickup` and `ToScore`, and FollowPath steps in your trees that drive both by those names. |
| **Paths go where they should** | ToPickup starts on Start and ends on Pickup. ToScore starts where ToPickup ended and ends on ScoreSpot. Each end must be within about 30 centimeters, the join within 15. |
| **Autonomous tree built** | `PickupAndScore.xml` drives ToPickup, then ToScore, then asks for AIM and waits for it (IsSystemState AIM, retried inside a Timeout), then SHOOT, a Sleep, and AUTO, in that order. |
| **AIM waits until aimed** | `handleStateTransition` puts the robot in AIMING until `drive_.isAimed()`, and only then in AIM. |
| **Reaches the pickup** | The robot's true position got within 0.4 meters of Pickup during a run. |
| **Reaches the scoring spot** | The robot's true position got within 0.4 meters of ScoreSpot. |
| **Aims before it shoots** | When each shot started, the robot truly faced the goal within 0.087 rad (about 5 degrees). |
| **Shoots** | The tree shot at least once. |

The last four read a running simulation, so run **PickupAndScore** in Auto before you Verify. They
remember the best the robot did since you pressed **Start**, so after a fix, press Start again.

If a run check fails, read it as a clue, and look for the red step in Arbor. Pickup failed: the first
path or its FollowPath. Scoring spot failed: the second path never ran, or doesn't start where the first
ended. Aim failed: the wait for AIM is missing or comes after the shot, or the robot reaches AIM before it is
aimed (Part 3). Shoots failed: the tree stopped before SHOOT (a Timeout that ran out fails the tree), or
there is no Sleep after SHOOT.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Java you will meet

- A **step state**: a state nothing asks for, which `handleStateTransition` picks on the way to the state
  that was asked for (AIMING on the way to AIM).
- An **`if`/`else` inside a `switch` case**, where each branch picks the next state.

## Stretch

- **Plan B.** Put the Timeout around the wait for AIM inside a `Fallback`, with a second child that just shoots
  anyway (`SetWantedState SHOOT`). What happens when you move `GOAL` in `DrivetrainConstants.java` so the
  robot can't aim in time?
- **Spin up early.** Use a `ParallelDeadline` so the flywheel spins up while the robot drives the second
  path.
- **A tunable wait.** Give the tree an input port such as `pickup_wait_msec` (as in the Arbor lesson) and
  write `{pickup_wait_msec}` in the first Sleep. Change it from Elastic without a rebuild.
- **Replay a run.** Open a `.btlog` from `logs/bt` in Arbor and step through it.
- Change the Max Velocity and see how it changes the total time (`Subsystem/Drivetrain/Choreo/TotalTime`).
- Draw a third path that goes back to Start, and drive it at the end.
