package frc.robot.subsystems.simulation;

import com.marswars.logging.MwLog;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import com.marswars.vision.MwVisionSim;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.simulation.SimulationConstants.SimulationStates;
import frc.robot.vision.TagVision;
import frc.robot.vision.VisionConstants;
import frc.robot.vision.VisionMeasurement;
import java.util.List;
import org.photonvision.targeting.MultiTargetPNPResult;
import org.photonvision.targeting.PhotonPipelineResult;

/**
 * Simulates the robot's cameras. Provided for you, no need to edit. It is only registered in
 * simulation (see RobotContainer): each loop it looks at where the simulated robot REALLY is, works
 * out which AprilTags each camera could see from there, and passes the poses those tags give to
 * TagVision, the way a real camera's coprocessor would.
 */
public class SimulationSubsystem extends MwSubsystem<SimulationStates, SimulationConstants> {
  private static SimulationSubsystem instance_ = null;

  public static SimulationSubsystem getInstance() {
    if (instance_ == null) {
      instance_ = new SimulationSubsystem();
    }
    return instance_;
  }

  private final MwVisionSim vision_sim_;

  private SimulationSubsystem() {
    super(SimulationStates.ACTIVE, new SimulationConstants());
    vision_sim_ = new MwVisionSim(VisionConstants.FIELD_LAYOUT);
    vision_sim_.addCamera(
        VisionConstants.FRONT_CAMERA_NAME, VisionConstants.FRONT_CAMERA_TRANSFORM);
    vision_sim_.addCamera(VisionConstants.BACK_CAMERA_NAME, VisionConstants.BACK_CAMERA_TRANSFORM);
  }

  /** This subsystem has no motors or sensors to read. */
  @Override
  public List<SubsystemIoBase> getIos() {
    return List.of();
  }

  @Override
  public void reset() {
    system_state_ = SimulationStates.ACTIVE;
  }

  /** Runs every 20 ms. The cameras are pointed from the TRUE pose, not from the estimate. */
  @Override
  public void updateLogic(double timestamp) {
    vision_sim_.update(DrivetrainSubsystem.getInstance().getTruePose());

    // Collect every new camera picture. A camera that sees two or more tags works out the robot's
    // pose from all of them at once ("multi-tag"), which is far more accurate than from one tag.
    int pictures = 0;
    for (MwVisionSim.CameraSimulation camera_sim : vision_sim_.getCameras()) {
      for (PhotonPipelineResult result : camera_sim.camera.getAllUnreadResults()) {
        if (result.getMultiTagResult().isEmpty()) {
          continue;
        }
        MultiTargetPNPResult multi_tag = result.getMultiTagResult().get();
        // The camera works out where IT is on the field; subtract where it is bolted to the robot
        // to get where the ROBOT is.
        Pose3d camera_pose = new Pose3d().plus(multi_tag.estimatedPose.best);
        Pose2d robot_pose = camera_pose.transformBy(camera_sim.robotToCamera.inverse()).toPose2d();
        TagVision.getInstance()
            .addMeasurement(
                new VisionMeasurement(
                    robot_pose,
                    result.getTimestampSeconds(),
                    multi_tag.fiducialIDsUsed.size(),
                    camera_sim.camera.getName()));
        pictures++;
      }
    }
    MwLog.log(getSubsystemKey() + "PicturesThisLoop", pictures);
  }
}
