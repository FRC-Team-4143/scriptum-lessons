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
  private final RobotContainer container_ = new RobotContainer();
  private Command auto_command_ = null;

  public Robot() {
    // Put the (simulated) robot at the start spot, and the pose estimate with it.
    DrivetrainSubsystem.getInstance().resetPose(FieldTargets.START);
    // Connect the controller buttons to commands.
    OI.configureBindings();
  }

  @Override
  public void robotPeriodic() {
    container_.doControlLoop();
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
    auto_command_ = container_.getSelectedAuto();
    Pose2d start_pose = new Pose2d(); // routines without a path start at the origin
    if (auto_command_ instanceof Auto auto) {
      try {
        // Load the paths the routine named with loadTrajectory(...). false = we are the blue
        // alliance, so the paths are used as drawn, not mirrored to the other side of the field.
        auto.cacheTrajetories(false);
        start_pose = auto.getStartPose();
      } catch (RuntimeException e) {
        DriverStation.reportError(
            "Could not load a path for "
                + auto.getName()
                + ". Did you draw it in Choreo and "
                + "generate it? "
                + e,
            false);
        auto_command_ = Commands.none();
      }
    }
    DrivetrainSubsystem.getInstance().resetPose(start_pose);
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.COMMANDED);
    ShooterSubsystem.getInstance().resetShotCount();
    CommandScheduler.getInstance().schedule(auto_command_);
  }

  @Override
  public void teleopInit() {
    // Stop the autonomous routine if it is still running, then give the driver control.
    if (auto_command_ != null) {
      auto_command_.cancel();
    }
    DrivetrainSubsystem.getInstance().setWantedState(DriveStates.ARCADE);
  }
}
