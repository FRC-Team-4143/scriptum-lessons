# Objects and Odometry

Your robot knows how far it has driven. Now it learns **where it is** on the field. You will use
WPILib's classes for **kinematics** and **pose estimation**, read the robot's heading from a real
**NavX gyro**, and make your own classes to organize the code.

Companion docs page: [Objects and Odometry](https://docs.marswars.org/docs/software/training/objects-odometry)

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## Where the code lives

- `Robot.java` has the numbered **STEP** comments for the controller and for autonomous.
- `OI.java` is a small class you will finish. **OI** (Operator Interface) is "the human's controls."
- `mechanisms/DifferentialDriveMech.java` is the drivetrain. This is where the robot learns its
  position: the kinematics and pose estimator live here, the same way they live in the competition
  robot's drive mechanism. Last lesson it only reported meters and speeds. Now it also knows
  *where it is*.
- The drivetrain also reads a **NavX gyro** plugged into the roboRIO's top (MXP) port. It is
  already read for you every loop into `inputs_.gyroYawRad` (radians, counterclockwise positive)
  and `inputs_.gyroConnected`. In the simulator the NavX reports the simulated robot's real heading.
- `DriveMath.java` and the other drivetrain methods are finished from last lesson.

### About the NavX

The NavX is an **IMU** (Inertial Measurement Unit): a gyroscope (how fast it's turning), an
accelerometer (speeding up, and which way is down) and a magnetometer (a compass, which robots
rarely use because motors confuse it) on one board. It reports three angles: **yaw** (turning left
and right, our heading), **pitch** (nose up or down) and **roll** (tipping side to side).

The drivetrain creates it with `new AHRS(NavXComType.kMXP_SPI)`, which talks to it through the
MXP port. On a real robot:

- **Hold the robot still while it boots.** The NavX calibrates at power-on.
- **Mount it flat and solid**, near the middle of the robot.
- **Check it's connected.** `isConnected()` is false when it's unplugged, and that's why `getYaw()`
  has a backup.
- **Watch the sign.** The NavX's `getYaw()` wraps at -180 to 180 degrees, but it counts clockwise.
  The code flips it with a minus sign, so counterclockwise is positive like the rest of WPILib.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Read about classes, objects, static and abstract |
| 30 | Step 1: the OI class |
| 30 | Step 2: kinematics |
| 20 | Step 3: yaw, from the wheels and from the gyro |
| 25 | Step 4: pose |
| 25 | Steps 5-6: watch the pose and compare it with the true pose |
| 20 | Step 7: drive to x = 3 m in autonomous |
| 10 | Verify |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

1. **Finish `OI.java`.** Write two methods:
   - `public static double getForward()` returns the left stick Y, with the minus sign.
   - `public static double getTurn()` returns the left stick X.

   Then in `Robot.java`, delete the `XboxController` field and call `OI.getForward()` and
   `OI.getTurn()` in `teleopPeriodic()`.
2. **`getChassisSpeeds()`** in `DifferentialDriveMech.java`. Create a `DifferentialDriveWheelSpeeds`
   from the left and right speeds and pass it to `kinematics_.toChassisSpeeds(...)`. In
   AdvantageScope, `Drive/LinearSpeed` and `Drive/AngularSpeed` should match
   `Drive/LinearSpeedByHand` and `Drive/AngularSpeedByHand`.
3. **Yaw.** Two short methods in `DifferentialDriveMech.java`:
   - **`getWheelYaw()`**: return `(right - left) / Constants.TRACK_WIDTH_METERS` (radians) as a
     `Rotation2d`. That is the heading worked out from the wheels alone.
   - **`getYaw()`**: if `inputs_.gyroConnected` is true, return `new Rotation2d(inputs_.gyroYawRad)`,
     the gyro's heading. Otherwise return `backupYaw()`. It is written for you: it carries on from the
     gyro's last heading using the wheels, so an unplugged gyro doesn't make the heading jump.
4. **`updatePose()`.** Call `pose_estimator_.update(getYaw(), getLeftMeters(), getRightMeters())`, then
   store `pose_estimator_.getEstimatedPosition()` in `pose_`.
5. **Watch it move.** In AdvantageScope's **2D Field** tab, drag `Drive/Pose` onto the field, then
   drive around.
6. **Compare with the truth.** Drag `Drive/TruePose` onto the same field and drive a long way. See how
   far the two end up apart. Then graph `Drive/GyroYawRad` and `Drive/WheelYawRad` together and drive
   in circles: the wheel heading slowly wanders off, and the gyro's doesn't. That is why `getYaw()`
   uses the gyro.
7. **Use the pose in autonomous.** Replace the `TODO` in `autonomousPeriodic()`: drive forward at `0.4`
   until `drive_.getPose().getX()` is at least `3.0` meters, then stop.
8. **Click Verify** to check your work.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- Add `getSlowMode()` to `OI` that returns whether a bumper is held, and halve the speed in
  `Robot` while it is. All the controller knowledge stays inside `OI`.
- Reset the pose to `new Pose2d(2.0, 4.0, new Rotation2d())` at the start of auto, so the robot
  starts in the middle of the field.
