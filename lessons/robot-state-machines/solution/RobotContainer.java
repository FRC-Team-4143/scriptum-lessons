package frc.robot;

import com.marswars.subsystem.MwSuperStateMachine;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.shooter.ShooterConstants.ShooterStates;
import frc.robot.subsystems.shooter.ShooterSubsystem;

/**
 * The robot container owns the list of subsystems, and it is also the robot's own state machine
 * (the "super state machine"). Buttons only ask the robot for a {@link RobotStates}; the robot then
 * asks each subsystem for the state it needs. Every loop it runs first, then each subsystem reads
 * its sensors, runs its logic, and writes its motor outputs.
 */
public class RobotContainer extends MwSuperStateMachine<RobotStates> {
  private static RobotContainer instance_ = null;

  public static RobotContainer getInstance() {
    if (instance_ == null) {
      instance_ = new RobotContainer();
    }
    return instance_;
  }

  private final DrivetrainSubsystem drive_ = DrivetrainSubsystem.getInstance();
  private final ShooterSubsystem shooter_ = ShooterSubsystem.getInstance();

  private RobotContainer() {
    super(RobotStates.IDLE, BuildConstants.class);

    registerSubsystem(drive_);
    registerSubsystem(shooter_);
  }

  /**
   * Asks each subsystem for the state the robot's current state needs. Runs every loop, before the
   * subsystems.
   */
  @Override
  protected void updateLogic(double timestamp) {
    switch (system_state_) {
      case SHOOT:
        drive_.setWantedState(DriveStates.ARCADE);
        shooter_.setWantedState(ShooterStates.SHOOT);
        break;
      case DRIVE:
        drive_.setWantedState(DriveStates.ARCADE);
        shooter_.setWantedState(ShooterStates.IDLE);
        break;
      case IDLE:
      default:
        drive_.setWantedState(DriveStates.IDLE);
        shooter_.setWantedState(ShooterStates.IDLE);
        break;
    }
  }
}
