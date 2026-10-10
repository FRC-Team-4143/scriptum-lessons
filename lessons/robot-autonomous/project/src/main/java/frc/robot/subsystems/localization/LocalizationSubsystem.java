package frc.robot.subsystems.localization;

import com.marswars.logging.MwLog;
import com.marswars.proxy_server.ProxyServerThread;
import com.marswars.proxy_server.TagSolutionPacket.TagSolutionData;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.DifferentialDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.localization.LocalizationConstants.LocalizationStates;
import frc.robot.vision.VisionConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Works out where the robot is on the field. It owns the pose estimator: every loop it feeds the
 * estimator the wheel readings (odometry), and it passes along each new camera solution. The wheels
 * are good for a short while but drift; the cameras are noisy but never drift. The estimator blends
 * them.
 *
 * <p>The camera solutions arrive through MW-Lib's ProxyServerThread, the way they do on a real
 * robot: the camera coprocessors send them over the network, and the proxy server keeps the latest
 * ones. In simulation the SimulationSubsystem plays the coprocessors' part.
 */
public class LocalizationSubsystem extends MwSubsystem<LocalizationStates, LocalizationConstants> {
  // There is only ever one localization subsystem, so everyone shares it through getInstance().
  private static LocalizationSubsystem instance_ = null;

  public static LocalizationSubsystem getInstance() {
    if (instance_ == null) {
      instance_ = new LocalizationSubsystem();
    }
    return instance_;
  }

  private final DrivetrainSubsystem drive_ = DrivetrainSubsystem.getInstance();
  private final DifferentialDrivePoseEstimator pose_estimator_;
  private Pose2d pose_ = new Pose2d();
  private int vision_measurements_seen_ = 0;
  private int vision_measurements_dropped_ = 0;
  // The proxy server hands back the same recent solutions on every call, so remember the newest
  // picture time already looked at for each camera.
  private final Map<String, Double> last_seen_seconds_ = new HashMap<>();
  // Solutions from pictures taken before this time are ignored (see resetPose()).
  private double ignore_before_seconds_ = 0.0;

  private LocalizationSubsystem() {
    super(LocalizationStates.ACTIVE, new LocalizationConstants());
    pose_estimator_ =
        new DifferentialDrivePoseEstimator(
            drive_.getKinematics(),
            drive_.getYaw(),
            drive_.getLeftMeters(),
            drive_.getRightMeters(),
            new Pose2d(),
            LocalizationConstants.ODOMETRY_STD_DEVS,
            // The vision standard deviations are given with each measurement instead; this is only
            // the default.
            VecBuilder.fill(
                LocalizationConstants.VISION_XY_STD_METERS,
                LocalizationConstants.VISION_XY_STD_METERS,
                LocalizationConstants.VISION_HEADING_STD_RADIANS));
  }

  /** This subsystem has no motors or sensors of its own: it reads the drivetrain's wheels. */
  @Override
  public List<SubsystemIoBase> getIos() {
    return List.of();
  }

  @Override
  public void reset() {
    system_state_ = LocalizationStates.ACTIVE;
  }

  /** Runs every 20 ms: add up the wheel movement, then use any new camera measurements. */
  @Override
  public void updateLogic(double timestamp) {
    // Odometry: the heading (from the wheels, standing in for a gyro) and how far each side drove.
    pose_estimator_.update(drive_.getYaw(), drive_.getLeftMeters(), drive_.getRightMeters());

    // Vision: the latest tag solutions from the proxy server. (This also logs them, under
    // Proxy/TagSolutions.) Each one is a pose, the time its picture was taken, and the ids of the
    // AprilTags it was worked out from.
    List<TagSolutionData> solutions = ProxyServerThread.getInstance().getLatestTagSolutions();
    for (TagSolutionData solution : solutions) {
      if (!isNew(solution)) {
        continue;
      }
      if (!isSensible(solution)) {
        vision_measurements_dropped_++;
        continue;
      }
      vision_measurements_seen_++;
      MwLog.log(getSubsystemKey() + "LastVisionPose", solution.pose);

      // How far off could the camera be? The standard deviations: x in meters, y in meters,
      // heading in radians. The smaller they are, the more the estimator pulls toward the camera.
      pose_estimator_.addVisionMeasurement(
          solution.pose,
          solution.timestamp.getSeconds(),
          VecBuilder.fill(
              LocalizationConstants.VISION_XY_STD_METERS,
              LocalizationConstants.VISION_XY_STD_METERS,
              LocalizationConstants.VISION_HEADING_STD_RADIANS));
    }

    pose_ = pose_estimator_.getEstimatedPosition();
    logPose();
  }

  /** Where the robot thinks it is on the field. */
  public Pose2d getPose() {
    return pose_;
  }

  /**
   * Starts the estimate over at a pose. The drivetrain calls this whenever it puts the robot
   * somewhere new; the wheel readings are zero at that moment.
   */
  public void resetPose(Pose2d new_pose) {
    pose_estimator_.resetPosition(drive_.getYaw(), 0.0, 0.0, new_pose);
    pose_ = pose_estimator_.getEstimatedPosition();
    // Camera pictures taken before the move (some are still on their way) show the OLD spot. Ignore
    // them, plus a couple of loops after the move while the simulated cameras catch up.
    ignore_before_seconds_ = Timer.getFPGATimestamp() + 0.05;
  }

  /**
   * True the first time this solution is seen. The proxy server keeps its last 20 solutions and
   * returns all of them on every call, so most of what comes back has already been used.
   */
  private boolean isNew(TagSolutionData solution) {
    double picture_seconds = solution.timestamp.getSeconds();
    double last_seen = last_seen_seconds_.getOrDefault(solution.cameraSerial, -1.0);
    if (picture_seconds <= last_seen) {
      return false;
    }
    last_seen_seconds_.put(solution.cameraSerial, picture_seconds);
    return true;
  }

  /**
   * True if a solution can be trusted at all: its picture is not from before a reset, the robot was
   * not spinning (a blurred picture), it used enough tags, and its pose is on the field.
   */
  private boolean isSensible(TagSolutionData solution) {
    return solution.timestamp.getSeconds() >= ignore_before_seconds_
        && Math.abs(drive_.getAngularSpeed()) <= VisionConstants.MAX_YAW_RATE_RADIANS_PER_SECOND
        && solution.detectedIds.size() >= VisionConstants.MIN_TAG_COUNT
        && isOnField(solution.pose);
  }

  /** True if the pose is inside the walls of the field. */
  private static boolean isOnField(Pose2d pose) {
    return pose.getX() >= 0.0
        && pose.getX() <= VisionConstants.FIELD_LAYOUT.getFieldLength()
        && pose.getY() >= 0.0
        && pose.getY() <= VisionConstants.FIELD_LAYOUT.getFieldWidth();
  }

  /** Logged every loop so you can plot them in AdvantageScope. */
  private void logPose() {
    MwLog.log(getSubsystemKey() + "Pose", pose_);
    MwLog.log(getSubsystemKey() + "PoseX", pose_.getX());
    MwLog.log(getSubsystemKey() + "PoseY", pose_.getY());
    MwLog.log(getSubsystemKey() + "PoseYawDeg", pose_.getRotation().getDegrees());
    MwLog.log(getSubsystemKey() + "VisionMeasurementsSeen", vision_measurements_seen_);
    MwLog.log(getSubsystemKey() + "VisionMeasurementsDropped", vision_measurements_dropped_);
    if (RobotBase.isSimulation()) {
      // How far the estimate is from where the simulated robot REALLY is.
      Pose2d truth = drive_.getTruePose();
      MwLog.log(
          getSubsystemKey() + "PositionErrorMeters",
          truth.getTranslation().getDistance(pose_.getTranslation()));
      MwLog.log(
          getSubsystemKey() + "HeadingErrorDegrees",
          Math.abs(truth.getRotation().minus(pose_.getRotation()).getDegrees()));
    }
  }
}
