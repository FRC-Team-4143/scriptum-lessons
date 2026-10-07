package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.LoggedNetworkNumber;

/**
 * The robot's behavior lives here. {@link Robot} handles the AdvantageKit logging setup and calls
 * {@link #robotPeriodic()} every loop, so you can focus on the tool, not the code.
 *
 * <p>{@link #flywheelRpm}, {@link #gamePieceLoaded}, and {@link #robotPose} already publish live
 * through AdvantageKit's {@link Logger} - once the robot is running, AdvantageScope (the "Scope"
 * tab above) connects to that live stream automatically, the same way Elastic does.
 */
public class RobotContainer {
  private final Timer timer = new Timer();

  // A tunable value - unlike everything above, this one is meant to be
  // *written* from AdvantageScope's Tuning Mode. Values published under
  // "/Tuning" are the ones Tuning Mode will let you edit; nothing else
  // qualifies.
  private final LoggedNetworkNumber flywheelTargetRpm =
      new LoggedNetworkNumber("/Tuning/FlywheelTargetRPM", 3000.0);

  // Oscillates between 0 and 6000 as t increases.
  public static double flywheelRpm(double t) {
    return 3000 + 3000 * Math.sin(t);
  }

  // Toggles back and forth as t increases.
  public static boolean gamePieceLoaded(double t) {
    return ((int) t) % 2 == 0;
  }

  // A figure-8 (lemniscate) path centered on the field, facing the
  // direction of travel.
  public static Pose2d robotPose(double t) {
    double x = 4.0 + 2.0 * Math.sin(t);
    double y = 4.0 + Math.sin(2 * t);
    double dx = 2.0 * Math.cos(t);
    double dy = 2.0 * Math.cos(2 * t);
    return new Pose2d(x, y, new Rotation2d(dx, dy));
  }

  // Four swerve module states (FL, FR, BL, BR), phase-offset so each one
  // spins and speeds up/down a little differently.
  public static SwerveModuleState[] moduleStates(double t) {
    return new SwerveModuleState[] {
      new SwerveModuleState(2.0 + Math.sin(t), new Rotation2d(t)),
      new SwerveModuleState(2.0 + Math.sin(t + Math.PI / 2), new Rotation2d(t + Math.PI / 2)),
      new SwerveModuleState(2.0 + Math.sin(t + Math.PI), new Rotation2d(t + Math.PI)),
      new SwerveModuleState(
          2.0 + Math.sin(t + 3 * Math.PI / 2), new Rotation2d(t + 3 * Math.PI / 2))
    };
  }

  // Robot-relative chassis speeds: forward, sideways, and angular.
  public static ChassisSpeeds chassisSpeeds(double t) {
    return new ChassisSpeeds(2.0 * Math.cos(t), 0.5 * Math.sin(t), Math.sin(0.5 * t));
  }

  public RobotContainer() {
    timer.start();
  }

  /** Called every loop while the robot is running. Add your own logic here. */
  public void robotPeriodic() {
    // Run the command scheduler so subsystems and commands you add keep working.
    CommandScheduler.getInstance().run();

    double seconds = timer.get();

    Logger.recordOutput("FlywheelRPM", flywheelRpm(seconds));
    Logger.recordOutput("GamePieceLoaded", gamePieceLoaded(seconds));

    // Echoes back whatever the last Tuning Mode write set - confirms the
    // round trip (write in AdvantageScope -> NT4 -> robot reads it here).
    Logger.recordOutput("FlywheelTargetRPM", flywheelTargetRpm.get());

    // Published as both a 2D and a 3D pose - the 2D Field widget wants
    // Pose2d, the 3D Field widget wants Pose3d (Pose3d(Pose2d) lifts it to
    // z=0 with no roll/pitch).
    Pose2d pose = robotPose(seconds);
    Logger.recordOutput("RobotPose2d", pose);
    Logger.recordOutput("RobotPose3d", new Pose3d(pose));

    // For the Swerve tab: module states, chassis speeds, and the chassis's
    // own rotation (reusing the same heading as the field pose above, so
    // the Swerve view and the Field views agree).
    Logger.recordOutput("SwerveModuleStates", moduleStates(seconds));
    Logger.recordOutput("ChassisSpeeds", chassisSpeeds(seconds));
    Logger.recordOutput("ChassisRotation", pose.getRotation());
  }
}
