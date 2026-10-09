package frc.robot.subsystems.drive;

import com.marswars.logging.MwLog;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
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
  private static DrivetrainSubsystem instance_ = null;

  public static DrivetrainSubsystem getInstance() {
    if (instance_ == null) {
      instance_ = new DrivetrainSubsystem();
    }
    return instance_;
  }

  private final DifferentialDriveMech drive_;

  // TODO (aim, part 1): create the PIDController that turns the robot to face the goal. Build it
  // from DrivetrainConstants.AIM_KP, AIM_KI and AIM_KD, so you can tune the gains there.
  // private final PIDController aim_pid_ = ...

  // The turn command the aim state last sent to the drive (logged so you can plot it).
  private double aim_turn_ = 0.0;

  private DrivetrainSubsystem() {
    super(DriveStates.IDLE, new DrivetrainConstants());
    drive_ =
        new DifferentialDriveMech(
            DrivetrainConstants.LEFT_MOTORS, DrivetrainConstants.RIGHT_MOTORS);

    // TODO (aim, part 1): configure the PID here. Headings wrap around (179 degrees and -179
    // degrees are only 2 degrees apart), so call enableContinuousInput(-Math.PI, Math.PI), and
    // set its tolerance with setTolerance(...) from DrivetrainConstants.AIM_TOLERANCE_DEGREES
    // (in radians).
  }

  /** The mechanisms this subsystem owns. MWLib reads and writes them for us every loop. */
  @Override
  public List<SubsystemIoBase> getIos() {
    return List.of(drive_);
  }

  @Override
  public void reset() {
    system_state_ = DriveStates.IDLE;
  }

  /** Runs every 20 ms. Decide what the drivetrain does based on its current state. */
  @Override
  public void updateLogic(double timestamp) {
    // TODO (aim, part 2): add a case for your new AIM state to this switch (below).
    //   - Ask the PID for a turn: aim_pid_.calculate(measurement, setpoint), where the measurement
    //     is the robot's heading in radians and the setpoint is the angle to the goal.
    //   - Send it to drive_.arcadeDrive(0.0, turn) so the robot spins in place. Save the turn in
    //     aim_turn_ so it is logged. Check the sign: a positive error means the goal is to the
    // LEFT,
    //     but a positive turn command turns the robot RIGHT.
    //   - In ARCADE and IDLE call aim_pid_.reset() so old error does not carry into the next aim.
    switch (system_state_) {
      case ARCADE:
        drive_.arcadeDrive(OI.getForward(), OI.getTurn());
        break;
      case IDLE:
      default:
        drive_.arcadeDrive(0.0, 0.0);
        break;
    }

    // Logged every loop (in every state) so you can plot them in AdvantageScope.
    MwLog.log(getSubsystemKey() + "AimErrorDegrees", Math.toDegrees(getAimErrorRadians()));
    MwLog.log(getSubsystemKey() + "AimOutput", aim_turn_);
    MwLog.log(getSubsystemKey() + "IsAimed", isAimed());
  }

  /** Where the robot thinks it is (the drive mechanism works this out). */
  public Pose2d getPose() {
    return drive_.getPose();
  }

  /**
   * The direction from the robot to the goal, as a field heading (0 = along +x, left is +).
   *
   * <p>TODO (aim, part 3): subtract the robot's position from DrivetrainConstants.GOAL (both are
   * Translation2d: pose_.getTranslation()). The result is an arrow from the robot to the goal, and
   * its getAngle() is the direction you want.
   */
  public Rotation2d getAngleToGoal() {
    return new Rotation2d();
  }

  /**
   * How far the robot still has to turn to face the goal, in radians from -pi to pi. Positive means
   * the goal is to the robot's left.
   */
  public double getAimErrorRadians() {
    return MathUtil.angleModulus(getAngleToGoal().minus(getPose().getRotation()).getRadians());
  }

  /**
   * True when the robot is facing the goal (within AIM_TOLERANCE_DEGREES) and has stopped turning.
   * A later autonomous routine waits on this before it shoots.
   *
   * <p>TODO (aim, part 4): return true only when BOTH are true: the heading error (use
   * getAimErrorRadians(), in degrees) is smaller than DrivetrainConstants.AIM_TOLERANCE_DEGREES,
   * AND the robot is no longer spinning: Math.abs(drive_.getAngularSpeed()) is under 0.15.
   */
  public boolean isAimed() {
    return false;
  }
}
