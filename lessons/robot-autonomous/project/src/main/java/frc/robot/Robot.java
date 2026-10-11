package frc.robot;

import com.marswars.auto.AutoManager;
import com.marswars.auto.AutoRoutine;
import com.marswars.auto.BehaviorTreeAuto;
import com.marswars.bt.debug.BtLiveServer;
import com.marswars.bt.monitor.BehaviorTreeFileLogger;
import com.marswars.logging.MwLog;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.autos.AutoNodes;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import lesson.Checks;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * The main robot class. It connects RobotContainer, the buttons and the autonomous trees. You
 * should not need to change it.
 */
public class Robot extends LoggedRobot {
  private final RobotContainer container_ = RobotContainer.getInstance();

  public Robot() {
    // Put the (simulated) robot at the start spot, and the pose estimate with it.
    DrivetrainSubsystem.getInstance().resetPose(FieldTargets.START);
    // Connect the controller buttons to robot states.
    OI.configureBindings();
    // Every tree in deploy/autos/trees is an option in the Auto Chooser, named after its file.
    AutoManager.getInstance()
        .registerAutos(
            BehaviorTreeAuto.loadAll(
                AutoNodes.createFactory(),
                Filesystem.getDeployDirectory().toPath().resolve("autos/trees")));
    if (!MwLog.isReplay()) {
      // Arbor's live view connects here (port 1670) to color the nodes while a tree runs.
      try {
        BtLiveServer.start(BtLiveServer.Options.defaults().withRobotName("sim"));
      } catch (RuntimeException e) {
        DataLogManager.log("Behavior tree live view not started: " + e.getMessage());
      }
    }
    // Each autonomous run is also saved as a .btlog file in logs/bt, which Arbor can replay.
    BehaviorTreeFileLogger.enableDefault();
  }

  @Override
  public void robotPeriodic() {
    // The buttons are commands, so the command scheduler still runs.
    CommandScheduler.getInstance().run();
    // Ticks the running autonomous tree, before the robot runs, so the states it asks for apply
    // in this same loop.
    AutoManager.getInstance().periodic();
    container_.doControlLoop();
    Checks.update(); // lesson helper, used by the Verify button
  }

  @Override
  public void disabledInit() {
    AutoManager.getInstance().stop();
    container_.setWantedState(RobotStates.IDLE);
  }

  @Override
  public void autonomousInit() {
    container_.setWantedState(RobotStates.AUTO);
    ShooterSubsystem.getInstance().resetShotCount();
    // Start the tree picked in the Auto Chooser, from where its first path starts. (Do_Nothing has
    // no path, so it starts at the origin.)
    AutoManager.getInstance().start();
    Pose2d start_pose =
        AutoManager.getInstance()
            .getRunningAuto()
            .map(AutoRoutine::getStartPose)
            .orElse(Pose2d.kZero);
    DrivetrainSubsystem.getInstance().resetPose(start_pose);
  }

  @Override
  public void teleopInit() {
    // Stop the autonomous tree if it is still running, then give the driver control.
    AutoManager.getInstance().stop();
    container_.setWantedState(RobotStates.DRIVE);
  }
}
