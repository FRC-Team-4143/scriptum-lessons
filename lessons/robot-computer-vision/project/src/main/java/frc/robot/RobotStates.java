package frc.robot;

/** The robot's states. RobotContainer is in exactly one of them at a time. */
public enum RobotStates {
  /** Disabled: nothing moves. */
  IDLE,
  /** Teleop driving from the sticks, shooter stopped. */
  DRIVE,
  /** Driving and shooting. */
  SHOOT,
  /** Turning to face the goal, shooter stopped. */
  AIM
}
