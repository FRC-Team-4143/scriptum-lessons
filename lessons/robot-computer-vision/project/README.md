# Computer Vision

Your robot knows where it is by adding up how far its wheels have turned (odometry). That is good for a
few seconds and then it drifts: after a long drive it can be half a meter off, which is far too much to
aim a shot from. A camera that can see the AprilTags on the field never drifts, but each picture is a
little noisy and arrives a little late. In this lesson you will **combine the two** so the robot always
knows where it is, then use that position to **turn and face the goal**.

Companion docs page: [Computer Vision](https://docs.marswars.org/docs/software/training/computer-vision)

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## What you will build

1. **Vision.** Give each camera measurement to the pose estimator, and choose how much to trust the
   cameras. When it works, your estimate stays within 5 centimeters and 1 degree of where the robot
   really is, even after a long drive.
2. **Aiming.** Add an `AIM` state to the drivetrain. While the driver holds the left bumper, the robot
   turns in place to face the goal at (4.0, 4.0), using your estimate of where it is. You tune a PID so
   it gets there quickly without swinging past.

## Before you start

You should have finished **State Machines and Commands**. This lesson starts from that robot, and uses
the same ideas: a subsystem with states and a `switch` (the drivetrain), commands that start and end a
state, and the PID tuning from **Control Theory**.

## Two words, in plain terms

- **Localization** is just "working out where the robot is on the field". The `LocalizationSubsystem`
  does it. It adds up the wheel readings every loop, and blends in the camera answers.
- A **vision measurement** is one camera answer: "from what I can see, the robot is *here*, and I took
  this picture *then*". It is provided for you as a `VisionMeasurement`. The **pose estimator** (a
  WPILib class) blends many of them with the wheels. Each measurement comes with a **standard
  deviation**: how far off it could be. Small means "trust it", big means "barely listen".

## Where the code lives

Everything is under `src/main/java/frc/robot/`:

- `subsystems/localization/LocalizationSubsystem.java`: owns the pose estimator. It adds up the wheel
  readings every loop and passes along each camera measurement. **This is where you add the vision
  measurement** (look for `TODO`).
- `subsystems/localization/LocalizationConstants.java`: how much to trust the wheels and the cameras.
  **You choose the vision numbers.**
- `subsystems/drive/DrivetrainSubsystem.java`, `DrivetrainConstants.java`, `DrivetrainCommands.java`:
  **the aim state, its PID and its command** (look for `TODO (aim`).
- `subsystems/simulation/SimulationSubsystem.java`: the simulated cameras. Provided; it points them from
  the simulator's TRUE position, not from your estimate.
- `vision/`: `TagVision` hands you clean `VisionMeasurement`s (a pose, the time of the picture and how
  many tags were seen). Provided.
- `FieldTargets.java`: `START` and `GOAL` on the field.
- `OI.java`: the left bumper (**E** on the keyboard) already starts `DrivetrainCommands.aim()`.

In AdvantageScope, `Drive/TruePose` is where the simulated robot really is and
`Subsystem/Localization/Pose` is what your code thinks. `Subsystem/Localization/PositionErrorMeters` and
`HeadingErrorDegrees` are the difference.

## Session plan (about 3 hours)

A suggested pace. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Read the Dozer guide "Where am I? Odometry vs vision" and open `LocalizationSubsystem.java` |
| 25 | Part 1: the `addVisionMeasurement(...)` call |
| 30 | Part 1: choose the trust numbers, drive, plot the error |
| 15 | The "How much should I trust the camera?" guide and a second try if the check fails |
| 35 | Part 2: the AIM state, PID, `getAngleToGoal()`, `isAimed()` and the command |
| 25 | Tune `AIM_KP` until it reaches the goal |
| 20 | Damp the overshoot with `AIM_KD` |
| 10 | Final clean run and Verify |

Go slower on anything that is new. The stretch below is optional.

## What you need to do

**Part 1: vision**

1. **Add the call.** In `LocalizationSubsystem.updateLogic()`, inside the loop, give each measurement
   to the pose estimator with `pose_estimator_.addVisionMeasurement(...)`. It takes the pose the
   camera saw, **the time the picture was taken** (not the time now) and the standard deviations.
2. **Choose the trust.** The starter `VISION_XY_STD_METERS` and `VISION_HEADING_STD_RADIANS` are both
   5.0, which means "ignore the cameras". Pick better numbers in `LocalizationConstants.java`.
3. **Look at it.** Start, enable Teleop, and drive with **W/A/S/D**. In AdvantageScope plot
   `Subsystem/Localization/PositionErrorMeters`, and put `Subsystem/Localization/Pose` and
   `Drive/TruePose` on the 2D Field.
4. **Drive more than 5 meters, stop, wait 2 seconds**, then click **Verify**.

**Part 2: aiming**

5. **The state.** Add `AIM` to the `DriveStates` enum in `DrivetrainConstants.java`.
6. **The PID.** In `DrivetrainSubsystem` make `aim_pid_` from `AIM_KP`, `AIM_KI` and `AIM_KD`, with
   continuous input (headings wrap around) and a tolerance.
7. **The direction.** Write `getAngleToGoal()`: the goal's position minus the robot's, as an angle. Use
   `getPose()`, the estimate.
8. **The case.** Add `case AIM:` to the `switch` in `updateLogic()`: ask the PID for a turn, flip its
   sign, and spin in place with `drive_.arcadeDrive(0.0, turn)`. Reset the PID in the other states.
9. **`isAimed()`.** True only when the error is small **and** the robot has stopped turning.
10. **The command.** `DrivetrainCommands.aim()` is a `Commands.startEnd(...)` that sets the wanted
    state to `AIM` and back to `ARCADE`.
11. **Tune it.** Start, enable Teleop, hold **E**. Plot `Subsystem/Drivetrain/AimErrorDegrees` and
    `AimOutput`, and change `AIM_KP` and `AIM_KD` in `DrivetrainConstants.java`.
12. **Click Verify.**

## How each checkpoint is checked

- **Camera measurements added.** Reads your `LocalizationSubsystem.java`. It looks for an
  `addVisionMeasurement(...)` call that passes the measurement's pose, the measurement's own timestamp
  and the standard deviations.
- **Estimate within 5 cm and 1 degree.** Needs a simulator running. The check watches the robot and only
  judges after you have **driven more than 5 meters in total and then sat still for 1.5 seconds**. It then
  compares your estimate with the true pose. It reads failing until you have done that, so drive, stop,
  wait 2 seconds, then Verify. Start fresh to try again.
- **Aim state written.** Reads your drivetrain files. It looks for the `AIM` state, a `case AIM:`, a
  `PIDController` built from the `AIM_` constants with continuous input, `getAngleToGoal()` aiming
  from the estimate (not the true pose), an `isAimed()` with `&&`, and a `startEnd` command.
- **Aims within 2 degrees.** Needs a simulator running. When you press **E**, the robot has to get
  within 2 degrees of the goal and **stay there for half a second, within 4 seconds** of the press.
  The check uses the robot's *true* heading, so the aim is only as good as your estimate.
- **No wild overshoot.** On the same presses, the robot may not swing more than 10 degrees *past* the
  goal. The check **remembers the worst swing from every press** of **E**, so a bad press stays
  recorded until you press **Start** again. Start fresh and do one clean press with your final numbers.

## Stretch (optional)

- Make a bad estimate visible: set `VISION_XY_STD_METERS` very small (like 0.01) and watch the 2D Field.
  Does the estimate shake? What does that tell you about the cameras?
- Aim while driving: have the driver still control forward and back while the robot turns toward the
  goal. What changes in `case AIM:`?
- Find the largest `AIM_KP` that still settles with no `AIM_KD`, then find the fastest settle time you
  can reach with both.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.
