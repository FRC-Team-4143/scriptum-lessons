package frc.robot.subsystems.localization;

import com.marswars.logging.MwLog;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.DifferentialDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.localization.LocalizationConstants.LocalizationStates;
import frc.robot.vision.TagVision;
import frc.robot.vision.VisionMeasurement;
import java.util.List;

/**
 * Works out where the robot is on the field. It owns the pose estimator: every loop it feeds the
 * estimator the wheel readings (odometry), and it passes along each new camera measurement. The
 * wheels are good for a short while but drift; the cameras are noisy but never drift. The estimator
 * blends them.
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

    // Vision: every camera measurement that arrived since the last loop (TagVision has already
    // thrown out the ones that cannot be right).
    List<VisionMeasurement> measurements =
        TagVision.getInstance().takeMeasurements(drive_.getAngularSpeed());
    for (VisionMeasurement measurement : measurements) {
      vision_measurements_seen_++;
      MwLog.log(getSubsystemKey() + "LastVisionPose", measurement.getPose());

      // TODO: give this measurement to the pose estimator, with addVisionMeasurement(). It needs
      // three things: the pose the camera saw (measurement.getPose()), when the picture was taken
      // (measurement.getTimestamp()) and how far off the camera could be: a VecBuilder.fill(...)
      // holding the x error in meters, the y error in meters and the heading error in radians.
      // Use LocalizationConstants.VISION_XY_STD_METERS and VISION_HEADING_STD_RADIANS for them.
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
    TagVision.getInstance().ignorePicturesBefore(Timer.getFPGATimestamp() + 0.05);
  }

  /** Logged every loop so you can plot them in AdvantageScope. */
  private void logPose() {
    MwLog.log(getSubsystemKey() + "Pose", pose_);
    MwLog.log(getSubsystemKey() + "PoseX", pose_.getX());
    MwLog.log(getSubsystemKey() + "PoseY", pose_.getY());
    MwLog.log(getSubsystemKey() + "PoseYawDeg", pose_.getRotation().getDegrees());
    MwLog.log(getSubsystemKey() + "VisionMeasurementsSeen", vision_measurements_seen_);
    MwLog.log(
        getSubsystemKey() + "VisionMeasurementsDropped", TagVision.getInstance().getDroppedCount());
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
