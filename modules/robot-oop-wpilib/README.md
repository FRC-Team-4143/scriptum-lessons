# OOP and WPILib

Your robot knows how far it has driven. Now it learns **where it is** on the field. You will use
WPILib's classes for **kinematics** and **pose estimation**, and you will make your own classes to
organize the code.

Companion docs page: [OOP and WPILib](https://docs.marswars.org/docs/software/training/oop-wpilib)

## Java you will learn in this lesson

**Classes and objects.** A **class** is a blueprint; an **object** is one thing built from it. WPILib has a
`Pose2d` class (the blueprint for "a position and heading"), and each pose in your robot is an object made
from it. You make an object with `new`:

```java
Rotation2d turned = new Rotation2d(0.5);   // an angle object, 0.5 radians
```

Objects have **methods** you call with a dot: `pose.getX()`. They also remember information in
**fields** (their own variables). To use a class that lives in another file or library, you write an
`import` line at the top, such as `import edu.wpi.first.math.MathUtil;`.

**Static: belonging to the class itself.** Most methods belong to an object. A `static` method
belongs to the class, so you call it on the class name, with no object at all: `Math.abs(-3.0)` or
`OI.getForward()`. Our competition robot's `OI` class (the human's controls) works this way.

**Abstract classes.** An `abstract` class is one nobody makes objects from. `OI` is abstract because it
only holds static things: you would never write `new OI()`.

## Where the code lives

- `Robot.java` has the numbered **STEP** comments for the controller and for autonomous.
- `OI.java` is a small class you will finish. **OI** (Operator Interface) is "the human's controls."
- `mechanisms/DifferentialDriveMech.java` is the drivetrain. This is where the robot learns its
  position: the kinematics and pose estimator live here, the same way they live in the competition
  robot's drive mechanism. Last lesson it only reported meters and speeds. Now it also knows
  *where it is*.
- `DriveMath.java` and the other drivetrain methods are finished from last lesson.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Read about classes, objects, static and abstract |
| 30 | Step 1: the OI class |
| 30 | Step 2: kinematics |
| 20 | Step 3: yaw |
| 25 | Step 4: pose |
| 25 | Steps 5-6: watch the pose and compare it with the true pose |
| 20 | Step 7: drive to x = 3 m in autonomous |
| 10 | Verify |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

1. **Finish the `OI` class.** Right now `Robot.java` reads the Xbox controller directly. A better
   plan is one class that owns the controller and answers questions about it, so the rest of the
   code never cares which buttons or sticks are involved. Our competition robot is organized exactly
   this way. `OI.java` already holds the controller. You write the two methods:
   - `public static double getForward()` returns the left stick Y, with the minus sign.
   - `public static double getTurn()` returns the right stick X.

   Then in `Robot.java`, delete the `XboxController` field and call `OI.getForward()` and
   `OI.getTurn()` in `teleopPeriodic()`. (`static` means you call the method on the class itself,
   `OI.getForward()`, rather than on an OI object. `abstract` means nobody ever makes an OI object.)
2. **Kinematics.** Wheel speeds are not the same as robot speeds. WPILib's
   `DifferentialDriveKinematics` does the conversion. In `DifferentialDriveMech.java`, finish
   `getChassisSpeeds()`: create a `DifferentialDriveWheelSpeeds` with the left and right speeds and
   pass it to `kinematics.toChassisSpeeds(...)`. The result, `ChassisSpeeds`, has
   `vxMetersPerSecond` (forward) and `omegaRadiansPerSecond` (turning). The mech logs it as
   `Drive/LinearSpeed` and `Drive/AngularSpeed`, right next to `Drive/LinearSpeedByHand` and
   `Drive/AngularSpeedByHand`, the numbers from your own `DriveMath` methods. In AdvantageScope the
   two pairs should match.
3. **Yaw.** A robot without a gyro can still estimate which way it faces from its wheel
   distances: `(right - left) / Constants.TRACK_WIDTH_METERS`, in radians. Finish `getYaw()` so it
   returns that as a `Rotation2d`.
4. **Pose.** Finish `updatePose()`: call `poseEstimator.update(getYaw(), getLeftMeters(),
   getRightMeters())`, then store `poseEstimator.getEstimatedPosition()` in `pose`. The mech calls
   `updatePose()` for you every loop, and logs the result as `Drive/Pose`.
5. **Watch it move.** Open AdvantageScope's **2D Field** tab and drag `Drive/Pose` onto it. Drive
   around and watch the robot's position. (A reminder from the AdvantageScope lesson if you need it.)
6. **Compare with the truth.** The simulator knows where the robot REALLY is, and logs it as
   `Drive/TruePose`. Drag it onto the same 2D Field as `Drive/Pose`. Your estimate will be close but
   not identical, and the gap grows the farther you drive. Real encoders are never perfect: wheels
   are never exactly the size you think, they slip a little, and the readings jitter. This is why
   real robots add cameras and gyros to correct their pose. Drive a long way, then reset with Auto,
   and see how far apart they end up.
7. **Use the pose in autonomous.** Replace the `TODO` in `autonomousPeriodic()`: drive forward at
   `0.4` until `drive.getPose().getX()` is at least `3.0` meters, then stop.
8. **Click Verify** to check your work.

## Check your thinking

WPILib also has `DifferentialDrive.arcadeDriveIK(forward, turn, false)`, a ready-made version of
the arcade math you wrote last lesson. Try calling it with a few numbers and compare its answers
with your `DriveMath.arcadeToWheelSpeeds`. They should agree. (Notice that WPILib says
**clockwise** is positive for the turn input, exactly like yours.)

## Bonus challenges

- Add `getSlowMode()` to `OI` that returns whether a bumper is held, and halve the speed in
  `Robot` while it is. All the controller knowledge stays inside `OI`.
- Reset the pose to `new Pose2d(2.0, 4.0, new Rotation2d())` at the start of auto, so the robot
  starts in the middle of the field.

## Words to know

- **Static:** belongs to the class itself, not to any one object.
- **Abstract class:** a class you can't make objects from. Our `OI` uses it to hold only static members.
- **Kinematics:** the math that connects wheel speeds to robot movement.
- **Pose:** the robot's position (x, y) and heading together.
- **Odometry:** adding up small wheel movements to track where the robot is.
