# Mechanisms and Subsystems

Until now all your code lived in one file. Real robots have many parts (a drivetrain, a shooter, an
intake...), and each one gets its own **subsystem**: a class that owns the part and decides what it
does. In this lesson you will see the drivetrain as a subsystem, then build a **shooter**
subsystem from two **mechanisms**, a spinning flywheel and a feeding roller.

Companion docs pages: [Mechanisms and Subsystems](https://docs.marswars.org/docs/software/training/mechanisms-subsystems),
[Mechanisms](https://docs.marswars.org/docs/software/robot_dev/mechanisms/) and
[Subsystems](https://docs.marswars.org/docs/software/robot_dev/subsystems)

## The big idea

- A **mechanism** is a reusable MWLib class for one kind of motor job: `FlywheelMech` spins a wheel
  at a speed, `RollerMech` runs a roller. A mechanism owns its motors, sensors and simulation.
  You have already used one: the drivetrain was a mechanism.
- A **subsystem** owns one or more mechanisms and holds the logic: "when the driver holds the
  button, spin the flywheel."
- The **robot container** lists every subsystem. MWLib then runs all of them every 20 ms.

## Java you will learn in this lesson

**Enums and `switch`.** An **enum** is a type with a short, fixed list of allowed values, perfect for
"which mode is it in?":

```java
public enum DriveStates { IDLE, ARCADE }
```

A `switch` runs different code for each value:

```java
switch (system_state_) {
  case ARCADE:
    // drive from the sticks
    break;
  case IDLE:
  default:
    // stop
    break;
}
```

Every `case` ends with `break;`. `default` runs when nothing else matched.

**Inheritance: building on an existing class.** `ShooterSubsystem extends MwSubsystem<...>` means "a
shooter subsystem **is a** subsystem, plus more". It automatically gets everything `MwSubsystem` has, and
`@Override` marks the methods where it replaces the parent's version with its own. The `<ShooterStates,
ShooterConstants>` after the name fills in blanks: it tells `MwSubsystem` which enum and which constants
class this subsystem uses.

**Constructors and one shared object.** A **constructor** is the special method that runs when you
write `new`; it sets up the object. A subsystem has a `private` constructor and a `static`
`getInstance()` method so the whole robot shares exactly one of it.

**Lists.** `List.of(flywheel, roller)` makes a fixed list of things, like an array that is easy to write.

## Where the code lives

All of this lives under `src/main/java/frc/robot/`:

- `subsystems/drive/DrivetrainSubsystem.java` is the drivetrain as a subsystem. Read it first: it
  owns the drive mechanism (the one whose kinematics and pose code you wrote) and decides what the
  drivetrain does in each state.
- `subsystems/shooter/ShooterConstants.java` and `ShooterSubsystem.java` are the new shooter.
- `OI.java` reads the driver's controller.
- `RobotContainer.java` registers the subsystems.
- `Robot.java` just connects things. You should not need to change it.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 25 | Read about enums, inheritance and constructors; read DrivetrainSubsystem |
| 15 | Step 1: drive from the subsystem |
| 25 | Step 2: shooter constants |
| 45 | Step 3: the shooter subsystem |
| 15 | Step 4: OI buttons |
| 10 | Step 5: register the shooter |
| 30 | Step 6: try it, graph the flywheel, debug |
| 10 | Verify |
| 5 | Wrap-up |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

1. **Drive from the subsystem.** In `DrivetrainSubsystem.java`, find `case ARCADE:` in
   `updateLogic()`. Add the line that drives with arcade drive using the OI sticks.
   (`OI.getForward()` and `OI.getTurn()` are from last lesson.) The subsystem has *states*; the robot
   is in `ARCADE` during teleop and `IDLE` otherwise. You will write your own states next lesson.
2. **Finish the shooter constants.** In `ShooterConstants.java`, add `ROLLER_MOTOR_CONFIG` (use the
   flywheel's line as a pattern) and `INDEX_DUTY_CYCLE` (use `0.5`). A constants class keeps every
   number about a part in one place, so the subsystem has no mystery numbers.
3. **Build the shooter subsystem.** In `ShooterSubsystem.java`:
   - Create the roller as a `RollerMech`, the same way the flywheel is created.
   - Make `getIos()` return both mechanisms so MWLib runs them.
   - In `updateLogic()`, set `rollerDuty` to `CONSTANTS.INDEX_DUTY_CYCLE` while the index button is
     held, and `0.0` otherwise. The flywheel part is already written as an example.
4. **Read the buttons.** In `OI.java`, finish `getShootButton()` (right bumper) and
   `getIndexButton()` (left bumper).
5. **Register the shooter.** In `RobotContainer.java`, add the shooter next to the drivetrain. Without
   this the shooter never runs. (Forgetting this step is a classic mistake.)
6. **Try it.** Start the robot and enable Teleop. Hold the **right bumper** (**U**) to spin the flywheel
   (graph `Subsystem/Shooter/FlywheelVelocity` in AdvantageScope) and the **left bumper** (**E**) to run
   the roller. On the keyboard, **U** is the right bumper and **E** is the left bumper.
7. **Click Verify.**

## Bonus challenges

- Change `SHOOT_VELOCITY` in `ShooterConstants.java` and watch the new target in AdvantageScope.
  Does the flywheel always reach it? How long does it take?
- Add `OI.getReverseButton()` (the `Y` button) that runs the roller backward, to unjam a game piece.

## Words to know

- **Mechanism:** a reusable class that controls one kind of motor system.
- **Subsystem:** a class that owns mechanisms and decides what they do.
- **Singleton / `getInstance()`:** a class with exactly one shared object.
- **Constants class:** one file that holds every number about a part.
