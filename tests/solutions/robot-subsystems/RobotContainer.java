package frc.robot;

import com.marswars.subsystem.SubsystemManager;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;

/**
 * The robot container owns the list of subsystems. A subsystem only runs if it is registered here.
 * Every loop, SubsystemManager reads each subsystem's sensors, runs its logic, then writes its
 * motor outputs.
 */
public class RobotContainer extends SubsystemManager {
  public RobotContainer() {
    super(BuildConstants.class);

    registerSubsystem(DrivetrainSubsystem.getInstance());
    registerSubsystem(ShooterSubsystem.getInstance());
  }
}
