# Choreo Autonomous

> **Work in progress.** The robot code and the Choreo project for this lesson are ready; the
> step-by-step lesson text and Dozer's guides are still being rewritten.

The first 15 seconds of a match are **autonomous**: the robot drives itself. In this lesson you draw
two paths in Choreo and build an autonomous routine that drives them, picks up a game piece and
shoots.

## Where things live

- `src/main/deploy/choreo/robot.chor` is the Choreo project: a **Differential** drive with the
  robot's numbers (24 inch track width, 50 kg) and three named poses, `Start`, `Pickup` and
  `ScoreSpot`. Open it in the Choreo pane and draw two paths, **ToPickup** (Start to Pickup) and
  **ToScore** (Pickup to ScoreSpot).
- `autos/Autos.java` is the routine. You load the two paths by name and put the commands in order:
  drive ToPickup, wait while the game piece is picked up, drive ToScore, shoot.
- `subsystems/drive/DrivetrainCommands.java`, `DifferentialPathFollower.java` and the Choreo
  methods in `DrivetrainSubsystem.java` follow a path for you. They are already tuned: read them to
  see how a path turns into motor power.
- `autos/DriveDistanceCommand.java` and `autos/TurnToAngleCommand.java` are finished example
  commands.
- `FieldTargets.java` has the three poses in code, so the checks can measure how close the robot got.

Run **Pickup And Score** from the Driver Station chooser (**Auto**, **Enable**) and watch
`Drive/Pose` on the 2D Field. The robot should reach the pickup point, then the scoring spot, and
shoot from there.
