package frc.robot;

import com.marswars.logging.MwLog;
import com.marswars.subsystem.SubsystemManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.localization.LocalizationSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.simulation.SimulationSubsystem;

/**
 * The robot container owns the list of subsystems. A subsystem only runs if it is registered here.
 * Every loop, SubsystemManager reads each subsystem's sensors, runs its logic, then writes its
 * motor outputs.
 */
public class RobotContainer extends SubsystemManager {
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
  }
}
