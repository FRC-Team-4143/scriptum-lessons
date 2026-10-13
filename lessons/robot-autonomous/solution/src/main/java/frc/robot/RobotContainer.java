package frc.robot;

import com.marswars.auto.ChoreoTrajectory;
import com.marswars.logging.MwLog;
import com.marswars.subsystem.MwSuperStateMachine;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.RobotConstants.RobotStates;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.localization.LocalizationSubsystem;
import frc.robot.subsystems.shooter.ShooterConstants.ShooterStates;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.simulation.SimulationSubsystem;

/**
 * The robot container owns the list of subsystems. It is also the robot's own state machine,
 * written like a subsystem: buttons and autonomous trees ask the robot for a {@link RobotStates},
 * and every loop, before the subsystems run, {@link #handleStateTransition} decides which state the
 * robot is in and {@link #updateLogic} asks each subsystem for the state it needs.
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

  // Decided in handleStateTransition, used in updateLogic
  private DriveStates shoot_drive_ = DriveStates.ARCADE;

  private RobotContainer() {
    super(RobotStates.IDLE, new RobotConstants(), BuildConstants.class);

    registerSubsystem(drive_);
    // Localization reads the wheels the drivetrain just read, so it comes right after it.
    registerSubsystem(LocalizationSubsystem.getInstance());
    registerSubsystem(shooter_);
    // The simulated cameras only exist in simulation (and not while replaying a log).
    if (RobotBase.isSimulation() && !MwLog.isReplay()) {
      try {
        registerSubsystem(SimulationSubsystem.getInstance());
      } catch (LinkageError e) {
        // PhotonLib has no native library for some computers (for example ARM ones). The robot
        // still runs, it just has no cameras.
        DriverStation.reportError("The camera simulation could not start: " + e, false);
      }
    }
  }

  /**
   * Decides which state the robot is in, from the state it was asked for. Every decision lives
   * here; updateLogic only asks the subsystems for what the current state needs.
   */
  @Override
  protected void handleStateTransition(RobotStates wanted) {
    // Nobody is on the sticks in autonomous, so shooting there stands still.
    if (DriverStation.isAutonomous()) {
      shoot_drive_ = DriveStates.COMMANDED;
    } else {
      shoot_drive_ = DriveStates.ARCADE;
    }

    switch (wanted) {
      case AIM:
        // AIM is a sequence: turn (the step state AIMING) until the robot faces the goal.
        if (drive_.isAimed()) {
          system_state_ = RobotStates.AIM;
        } else {
          system_state_ = RobotStates.AIMING;
        }
        break;
      default:
        system_state_ = wanted;
        break;
    }
  }

  /** Asks each subsystem for the state the robot's current state needs. */
  @Override
  protected void updateLogic(double timestamp) {
    switch (system_state_) {
      case SHOOT:
        drive_.setWantedState(shoot_drive_);
        shooter_.setWantedState(ShooterStates.SHOOT);
        break;
      case AIMING:
      case AIM:
        drive_.setWantedState(DriveStates.AIM);
        shooter_.setWantedState(ShooterStates.IDLE);
        break;
      case FOLLOW_PATH:
        drive_.setWantedState(DriveStates.CHOREO_PATH);
        shooter_.setWantedState(ShooterStates.IDLE);
        break;
      case AUTO:
        drive_.setWantedState(DriveStates.COMMANDED);
        shooter_.setWantedState(ShooterStates.IDLE);
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

  /**
   * Starts driving a Choreo path: picks the path, then asks for FOLLOW_PATH. The FollowPath node in
   * your autonomous tree calls this (see autos/AutoNodes.java).
   */
  public void followPath(ChoreoTrajectory trajectory) {
    drive_.setDesiredChoreoTrajectory(trajectory);
    setWantedState(RobotStates.FOLLOW_PATH);
  }

  /** True once the robot has arrived at the end of the path and stopped. */
  public boolean isPathDone() {
    return drive_.isAtChoreoSetpoint();
  }

  /** Stops following the path: stop the wheels and go back to AUTO. */
  public void endPath() {
    drive_.stopChoreoEvents();
    drive_.setCommandedSpeeds(0.0, 0.0);
    setWantedState(RobotStates.AUTO);
  }
}
