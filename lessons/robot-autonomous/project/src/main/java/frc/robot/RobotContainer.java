package frc.robot;

import com.marswars.logging.MwLog;
import com.marswars.subsystem.SubsystemManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.autos.Autos;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.localization.LocalizationSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.simulation.SimulationSubsystem;

/**
 * The robot container owns the list of subsystems and the autonomous chooser, the drop-down menu
 * the drive team uses to pick an autonomous routine before the match.
 */
public class RobotContainer extends SubsystemManager {
  private final SendableChooser<Command> auto_chooser_ = new SendableChooser<>();

  public RobotContainer() {
    super(BuildConstants.class);

    registerSubsystem(DrivetrainSubsystem.getInstance());
    // Localization reads the wheels the drivetrain just read, so it comes right after it.
    registerSubsystem(LocalizationSubsystem.getInstance());
    registerSubsystem(ShooterSubsystem.getInstance());
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
}
