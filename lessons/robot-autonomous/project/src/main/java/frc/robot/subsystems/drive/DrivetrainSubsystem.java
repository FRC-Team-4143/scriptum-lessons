package frc.robot.subsystems.drive;

import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.Constants;
import frc.robot.OI;
import frc.robot.mechanisms.DifferentialDriveMech;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import java.util.List;

/**
 * The drivetrain subsystem. It owns the drive mechanism and decides what the drivetrain should do
 * in each state. The mechanism itself works out the robot's speeds and pose (you wrote that in the
 * last lesson), so this class just asks it.
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

  // The speeds autonomous commands ask for. Only used in the COMMANDED state.
  private double commandedForward = 0.0;
  private double commandedTurn = 0.0;

  private DrivetrainSubsystem() {
    super(DriveStates.IDLE, new DrivetrainConstants());
    drive = new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  }

  /** The mechanisms this subsystem owns. MWLib reads and writes them for us every loop. */
  @Override
  public List<SubsystemIoBase> getIos() {
    return List.of(drive);
  }

  @Override
  public void reset() {
    system_state_ = DriveStates.IDLE;
  }

  /** Runs every 20 ms. Decide what the drivetrain does based on its current state. */
  @Override
  public void updateLogic(double timestamp) {
    switch (system_state_) {
      case ARCADE:
        drive.arcadeDrive(OI.getForward(), OI.getTurn());
        break;
      case COMMANDED:
        drive.arcadeDrive(commandedForward, commandedTurn);
        break;
      case IDLE:
      default:
        drive.arcadeDrive(0.0, 0.0);
        break;
    }
  }

  /**
   * Asks the drivetrain to drive at these speeds. Commands call this. It only has an effect while
   * the drivetrain is in the COMMANDED state.
   *
   * @param forward -1.0 (backward) to 1.0 (forward)
   * @param turn -1.0 (turn left) to 1.0 (turn right)
   */
  public void setCommandedSpeeds(double forward, double turn) {
    commandedForward = forward;
    commandedTurn = turn;
  }

  /** Puts the robot back at the origin facing forward, with fresh encoders. */
  public void resetPose() {
    drive.resetPose();
  }

  /** The constants for this subsystem (autonomous commands read their gains from here). */
  public DrivetrainConstants getConstants() {
    return CONSTANTS;
  }

  /** How fast the robot is turning in radians per second. Positive is turning left. */
  public double getAngularSpeed() {
    return drive.getChassisSpeeds().omegaRadiansPerSecond;
  }

  /** Where the robot thinks it is (the drive mechanism works this out). */
  public Pose2d getPose() {
    return drive.getPose();
  }
}
