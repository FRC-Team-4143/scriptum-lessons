package frc.robot.subsystems.simulation;

import com.marswars.proxy_server.ProxyServerThread;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import com.marswars.vision.MwVisionSim;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.simulation.SimulationConstants.SimulationStates;
import frc.robot.vision.VisionConstants;
import java.util.List;

/**
 * Simulates the robot's cameras. Provided for you, no need to edit. It is only registered in
 * simulation (see RobotContainer). On a real robot the cameras live on a coprocessor that works out
 * the robot's pose from the AprilTags it sees and sends it to the robot over the network, into
 * MW-Lib's ProxyServerThread. Here the simulator plays the coprocessor's part: each loop it looks
 * at where the simulated robot REALLY is, works out what each camera would see from there, and puts
 * the tag solutions into the same ProxyServerThread. LocalizationSubsystem reads them back out,
 * exactly as it would on the real robot.
 */
public class SimulationSubsystem extends MwSubsystem<SimulationStates, SimulationConstants> {
  private static SimulationSubsystem instance_ = null;

  public static SimulationSubsystem getInstance() {
    if (instance_ == null) {
      instance_ = new SimulationSubsystem();
    }
    return instance_;
  }

  private SimulationSubsystem() {
    super(SimulationStates.ACTIVE, new SimulationConstants());
    MwVisionSim vision_sim =
        ProxyServerThread.getInstance().initializeVisionSimulation(VisionConstants.FIELD_LAYOUT);
    vision_sim.addCamera(VisionConstants.FRONT_CAMERA_NAME, VisionConstants.FRONT_CAMERA_TRANSFORM);
    vision_sim.addCamera(VisionConstants.BACK_CAMERA_NAME, VisionConstants.BACK_CAMERA_TRANSFORM);
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
    ProxyServerThread.getInstance()
        .updateVisionSimulation(DrivetrainSubsystem.getInstance().getTruePose());
  }
}
