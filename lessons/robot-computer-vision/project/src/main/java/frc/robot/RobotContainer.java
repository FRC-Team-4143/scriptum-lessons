package frc.robot;

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
 * The robot container owns the list of subsystems, and it is also the robot's own state machine.
 * Buttons only ask the robot for a {@link RobotStates}; every loop, before the subsystems run, the
 * robot asks each subsystem for the state it needs.
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

  /** Asks each subsystem for the state the robot's current state needs. */
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
        // TODO: add a case AIM: the drivetrain wants DriveStates.AIM (add that state first) and the
        // shooter wants ShooterStates.IDLE. The left bumper already asks the robot for AIM.
      case IDLE:
      default:
        drive_.setWantedState(DriveStates.IDLE);
        shooter_.setWantedState(ShooterStates.IDLE);
        break;
    }
  }
}
