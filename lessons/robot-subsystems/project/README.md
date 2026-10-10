# Mechanisms and Subsystems

Until now all your code lived in one file. Real robots have many parts (a drivetrain, a shooter, an
intake...), and each one gets its own **subsystem**: a class that owns the part and decides what it
does. In this lesson you will see the drivetrain as a subsystem, then build a **shooter**
subsystem from two **mechanisms**, a spinning flywheel and a feeding roller.

Companion docs pages: [Mechanisms and Subsystems](https://docs.marswars.org/docs/software/training/mechanisms-subsystems),
[Mechanisms](https://docs.marswars.org/docs/software/robot_dev/mechanisms/) and
[Subsystems](https://docs.marswars.org/docs/software/robot_dev/subsystems)

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Where the code lives

All of this lives under `src/main/java/frc/robot/`:

- `subsystems/drive/DrivetrainSubsystem.java` is the drivetrain as a subsystem. Read it first: it
  owns the drive mechanism (the one whose kinematics and pose code you wrote) and drives it from the
  sticks every loop.
- `subsystems/drive/DrivetrainConstants.java` holds every number that describes the drivetrain: the
  wheel radius, gear ratio, track width, mass and motors that used to live in `Constants.java`. That
  file is gone; each subsystem now keeps its own constants.
- `subsystems/drive/DriveMath.java` is your `DriveMath` from the Drive Math lesson. It moved here, next
  to the drivetrain it serves; the code in it is the same.
- `subsystems/shooter/ShooterConstants.java` and `ShooterSubsystem.java` are the new shooter.
- `OI.java` reads the driver's controller.
- `RobotContainer.java` registers the subsystems.
- `Robot.java` just connects things. You should not need to change it.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 25 | Read about inheritance and constructors; read DrivetrainSubsystem |
| 15 | Step 1: drive from the subsystem |
| 25 | Step 2: shooter constants |
| 45 | Step 3: the shooter subsystem (including the shoot button logic) |
| 15 | Step 4: OI buttons |
| 10 | Step 5: register the shooter |
| 30 | Step 6: try it, graph the flywheel, debug |
| 10 | Verify |
| 5 | Wrap-up |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

1. **`DrivetrainSubsystem.java`.** In `updateLogic()`, call
   `drive_.arcadeDrive(OI.getForward(), OI.getTurn())`. `updateLogic()` runs every 20 ms, so the robot
   keeps following the sticks.
2. **`ShooterConstants.java`.** Add `ROLLER_MOTOR_CONFIG` (copy the flywheel's line as a pattern) and
   `INDEX_DUTY_CYCLE = 0.5`.
3. **`ShooterSubsystem.java`.**
   - Create the roller as a `RollerMech`, the same way the flywheel is created.
   - Make `getIos()` return both mechanisms.
   - In `updateLogic()`, write the shoot button logic yourself with an `if` / `else`: while
     `OI.getShootButton()` is true, call `flywheel_.setTargetDutyCycle(CONSTANTS.SHOOT_DUTY_CYCLE)`;
     otherwise set the flywheel's duty cycle to `0.0` so it coasts to a stop.
   - Set `roller_duty` to `CONSTANTS.INDEX_DUTY_CYCLE` while the index button is held, `0.0`
     otherwise.
4. **`OI.java`.** Finish `getShootButton()` (right bumper) and `getIndexButton()` (left bumper).
5. **`RobotContainer.java`.** Register the shooter next to the drivetrain.
6. **Try it.** Start, enable Teleop, hold **U** (right bumper) to spin the flywheel and **E** (left
   bumper) to run the roller. Graph `Subsystem/Shooter/FlywheelVelocity`.
7. **Click Verify.**

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- Change `SHOOT_DUTY_CYCLE` in `ShooterConstants.java` (try `0.3`, then `1.0`) and watch
  `Subsystem/Shooter/FlywheelVelocity` in AdvantageScope. How does the top speed change? Notice you
  choose a *power*, not a speed: the next lessons are about making the flywheel hold an exact speed.
- Add `OI.getReverseButton()` (the `Y` button) that runs the roller backward, to unjam a game piece.
