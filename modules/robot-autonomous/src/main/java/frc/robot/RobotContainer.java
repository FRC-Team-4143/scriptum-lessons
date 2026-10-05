package frc.robot;

import com.marswars.subsystem.SubsystemManager;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;

/**
 * The robot container owns the list of subsystems and the autonomous chooser, the drop-down menu
 * the drive team uses to pick an autonomous routine before the match.
 */
public class RobotContainer extends SubsystemManager {
  private final SendableChooser<Command> autoChooser = new SendableChooser<>();

  public RobotContainer() {
    super(BuildConstants.class);

    registerSubsystem(DrivetrainSubsystem.getInstance());
    registerSubsystem(ShooterSubsystem.getInstance());

    // The default choice does nothing at all.
    autoChooser.setDefaultOption("Do Nothing", Commands.none());
    // TODO: add the autos to the chooser. addOption takes a name and a command:
    //   autoChooser.addOption("Left Auto", Autos.leftAuto());
    // Add "Left Auto", "Right Auto" and "Square Auto".
    SmartDashboard.putData("Auto Choices", autoChooser);
  }

  /** The routine currently picked in the chooser. */
  public Command getSelectedAuto() {
    return autoChooser.getSelected();
  }
}
