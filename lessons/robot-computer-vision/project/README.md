# Computer Vision

> **Work in progress.** This lesson's tasks and checkpoints are still being written. What is here is the
> starting robot.

Your robot knows where it is by adding up how far its wheels have turned (odometry). That is good for a
few seconds and then it drifts. A camera that can see AprilTags on the field never drifts, but each
picture is noisy and arrives a little late. In this lesson you will combine the two with the pose
estimator, then use the result to aim at a spot on the field.

Companion docs page: [Computer Vision](https://docs.marswars.org/docs/software/training/computer-vision)

## Where the code lives

Everything is under `src/main/java/frc/robot/`:

- `subsystems/localization/LocalizationSubsystem.java`: owns the pose estimator. It adds up the wheel
  readings every loop and passes along each camera measurement. **This is where you add the vision
  measurement** (look for `TODO`).
- `subsystems/localization/LocalizationConstants.java`: how much to trust the wheels and the cameras
  (standard deviations).
- `subsystems/simulation/SimulationSubsystem.java`: the simulated cameras. Provided; it points them from
  the simulator's TRUE position, not from your estimate.
- `vision/`: `TagVision` hands you clean `VisionMeasurement`s (a pose, the time of the picture and how
  many tags were seen). Provided.
- `FieldTargets.java`: `START` and `GOAL` on the field.
- `subsystems/drive/DrivetrainSubsystem.java`: `getPose()` asks the localization subsystem.

In AdvantageScope, `Drive/TruePose` is where the simulated robot really is and
`Subsystem/Localization/Pose` is what your code thinks. `Subsystem/Localization/PositionErrorMeters` and
`HeadingErrorDegrees` are the difference.
