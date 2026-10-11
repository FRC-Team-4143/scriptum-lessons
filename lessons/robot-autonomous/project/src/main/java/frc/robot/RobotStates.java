package frc.robot;

/** The robot's states. RobotContainer is in exactly one of them at a time. */
public enum RobotStates {
  /** Disabled: nothing moves. */
  IDLE,
  /** Teleop driving from the sticks, shooter stopped. */
  DRIVE,
  /** Shooting (driving from the sticks in teleop, standing still in autonomous). */
  SHOOT,
  /**
   * Facing the goal, shooter stopped. This is what buttons and autos ask for. The robot only gets
   * here once it is aimed: until then it is in the step state AIMING.
   */
  AIM,
  /**
   * Step state: still turning to face the goal. Nothing asks for it; handleStateTransition picks it
   * on the way to AIM.
   */
  AIMING,
  /** Autonomous: the drivetrain drives whatever speeds the routine asks for, shooter stopped. */
  AUTO,
  /** Autonomous: following a Choreo path (see {@link RobotContainer#followPath}). */
  FOLLOW_PATH
}
