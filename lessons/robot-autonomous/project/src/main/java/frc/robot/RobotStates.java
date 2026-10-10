package frc.robot;

/** The robot's states. RobotContainer is in exactly one of them at a time. */
public enum RobotStates {
  /** Disabled: nothing moves. */
  IDLE,
  /** Teleop driving from the sticks, shooter stopped. */
  DRIVE,
  /** Shooting (driving from the sticks in teleop, standing still in autonomous). */
  SHOOT,
  /** Turning to face the goal, shooter stopped. */
  AIM,
  /** Autonomous: the drivetrain drives whatever speeds the routine asks for, shooter stopped. */
  AUTO,
  /** Autonomous: following a Choreo path (see {@link RobotContainer#followPath}). */
  FOLLOW_PATH
}
