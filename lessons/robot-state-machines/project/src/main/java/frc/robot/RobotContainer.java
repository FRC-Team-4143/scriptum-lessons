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
public class RobotContainer extends MwSuperStateMachine<RobotStates, RobotConstants> {
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
    super(RobotStates.IDLE, new RobotConstants(), BuildConstants.class);

    registerSubsystem(drive_);
    registerSubsystem(shooter_);
  }

  /**
   * Asks each subsystem for the state the robot's current state needs. Runs every loop, before the
   * subsystems.
   *
   * <p>TODO: write a switch (system_state_) with a case for each robot state:
   *
   * <ul>
   *   <li>IDLE: drive_ wants DriveStates.IDLE, shooter_ wants ShooterStates.IDLE
   *   <li>DRIVE: drive_ wants DriveStates.ARCADE, shooter_ wants ShooterStates.IDLE
   *   <li>SHOOT: drive_ wants DriveStates.ARCADE, shooter_ wants ShooterStates.SHOOT
   * </ul>
   *
   * Ask with setWantedState, for example drive_.setWantedState(DriveStates.ARCADE);
   */
  @Override
  protected void updateLogic(double timestamp) {}
}
