package frc.robot;

import com.marswars.auto.ChoreoTrajectory;
import com.marswars.logging.MwLog;
import com.marswars.subsystem.MwSuperStateMachine;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.autos.Autos;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.localization.LocalizationSubsystem;
import frc.robot.subsystems.shooter.ShooterConstants.ShooterStates;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.simulation.SimulationSubsystem;
import java.util.function.Supplier;

/**
 * The robot container owns the list of subsystems and the autonomous chooser, the drop-down menu
 * the drive team uses to pick an autonomous routine before the match. It is also the robot's own
 * state machine: buttons and autonomous routines ask the robot for a {@link RobotStates}, and
 * every loop, before the subsystems run, the robot asks each subsystem for the state it needs.
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
  private final SendableChooser<Command> auto_chooser_ = new SendableChooser<>();

  private RobotContainer() {
    super(RobotStates.IDLE, BuildConstants.class);

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
   * Fills the autonomous chooser. Robot calls this once at startup. (Not in the constructor: the
   * routines ask the robot for states, so the robot has to exist first.)
   */
  public void configureAutos() {
    // The default choice does nothing at all.
    auto_chooser_.setDefaultOption("Do Nothing", Commands.none());
    // The routines you can pick from. Pickup And Score is the one you build in Autos.java.
    auto_chooser_.addOption("Pickup And Score", Autos.pickupAndScore());
    SmartDashboard.putData("Auto Choices", auto_chooser_);
  }

  /** The routine currently picked in the chooser. */
  public Command getSelectedAuto() {
    return auto_chooser_.getSelected();
  }

  /** Asks each subsystem for the state the robot's current state needs. */
  @Override
  protected void updateLogic(double timestamp) {
    switch (getSystemState()) {
      case SHOOT:
        // In autonomous nobody is on the sticks, so stand still while shooting.
        drive_.setWantedState(
            DriverStation.isAutonomous() ? DriveStates.COMMANDED : DriveStates.ARCADE);
        shooter_.setWantedState(ShooterStates.SHOOT);
        break;
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
   * Drives a Choreo path from start to finish, then stops. It finishes when the robot has arrived
   * at the end of the path, or after 10 seconds (so a robot that gets stuck cannot hold up the rest
   * of the autonomous routine forever).
   *
   * @param trajectory the path, as given by {@code getTrajectory("Name")} inside an {@code Auto}
   */
  public Command followPath(Supplier<ChoreoTrajectory> trajectory) {
    return Commands.sequence(
        // 1. Pick the path.
        drive_.setDesiredChoreoTrajectoryCommand(trajectory),
        // 2. Ask the robot to follow it, and wait there until the robot has arrived.
        Commands.startEnd(
                () -> setWantedState(RobotStates.FOLLOW_PATH),
                () -> {
                  drive_.stopChoreoEvents();
                  drive_.setCommandedSpeeds(0.0, 0.0);
                  setWantedState(RobotStates.AUTO);
                })
            .until(drive_::isAtChoreoSetpoint)
            .withTimeout(10.0));
  }
}
