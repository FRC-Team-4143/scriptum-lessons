package frc.robot.subsystems.drive;

import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.mechanisms.DifferentialDriveMech;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import java.util.List;

/**
 * The drivetrain subsystem. It owns the drive mechanism and drives it from the driver's sticks. The
 * mechanism itself works out the robot's speeds and pose (you wrote that in the last lesson), so
 * this class just asks it.
 */
public class DrivetrainSubsystem extends MwSubsystem<DriveStates, DrivetrainConstants> {
  // There is only ever one drivetrain, so everyone shares it through getInstance().
  private static DrivetrainSubsystem instance = null;

  public static DrivetrainSubsystem getInstance() {
    if (instance == null) {
      instance = new DrivetrainSubsystem();
    }
    return instance;
  }

  private final DifferentialDriveMech drive;

  private DrivetrainSubsystem() {
    super(DriveStates.ARCADE, new DrivetrainConstants());
    drive =
        new DifferentialDriveMech(
            DrivetrainConstants.LEFT_MOTORS, DrivetrainConstants.RIGHT_MOTORS);
  }

  /** The mechanisms this subsystem owns. MWLib reads and writes them for us every loop. */
  @Override
  public List<SubsystemIoBase> getIos() {
    return List.of(drive);
  }

  @Override
  public void reset() {
    system_state_ = DriveStates.ARCADE;
  }

  /** Runs every 20 ms. Drive from the driver's sticks. */
  @Override
  public void updateLogic(double timestamp) {
    // TODO: drive with arcade drive using the driver's sticks: drive.arcadeDrive(forward, turn)
    // Get the sticks from the OI class.
  }

  /** Where the robot thinks it is (the drive mechanism works this out). */
  public Pose2d getPose() {
    return drive.getPose();
  }
}
