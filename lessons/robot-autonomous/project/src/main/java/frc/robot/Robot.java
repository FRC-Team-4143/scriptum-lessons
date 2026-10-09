package frc.robot;

import com.marswars.auto.Auto;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import lesson.Checks;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * The main robot class. It connects the subsystems, commands and the autonomous chooser. You should
 * not need to change it.
 */
public class Robot extends LoggedRobot {
  private final RobotContainer container = new RobotContainer();
  private Command autoCommand = null;

  public Robot() {
    OI.configureBindings();
  }

  @Override
  public void robotPeriodic() {
    container.doControlLoop();
    CommandScheduler.getInstance().run();
    Checks.update(); // lesson helper, used by the Verify button
  }

  @Override
  public void disabledInit() {
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.IDLE);
  }

  @Override
  public void autonomousInit() {
    // Run the routine picked in the chooser, starting from where its first path starts.
    autoCommand = container.getSelectedAuto();
    Pose2d startPose = new Pose2d(); // routines without a path start at the origin
    if (autoCommand instanceof Auto auto) {
      try {
        // Load the paths the routine named with loadTrajectory(...). false = we are the blue
        // alliance, so the paths are used as drawn, not mirrored to the other side of the field.
        auto.cacheTrajetories(false);
        startPose = auto.getStartPose();
      } catch (RuntimeException e) {
        DriverStation.reportError(
            "Could not load a path for "
                + auto.getName()
                + ". Did you draw it in Choreo and "
                + "generate it? "
                + e,
            false);
        autoCommand = Commands.none();
      }
    }
    DrivetrainSubsystem.getInstance().resetPose(startPose);
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.COMMANDED);
    ShooterSubsystem.getInstance().resetShotCount();
    CommandScheduler.getInstance().schedule(autoCommand);
  }

  @Override
  public void teleopInit() {
    // Stop the autonomous routine if it is still running, then give the driver control.
    if (autoCommand != null) {
      autoCommand.cancel();
    }
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.ARCADE);
  }
}
