package frc.robot.subsystems.drive;

import com.marswars.logging.MwLog;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
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

  // The PID controller that turns the robot to face the goal. Its gains come from
  // DrivetrainConstants, so you can tune them there.
  private final PIDController aim_pid_ =
      new PIDController(
          DrivetrainConstants.AIM_KP, DrivetrainConstants.AIM_KI, DrivetrainConstants.AIM_KD);

  // The turn command the aim state last sent to the drive (logged so you can plot it).
  private double aim_turn_ = 0.0;

  private DrivetrainSubsystem() {
    super(DriveStates.IDLE, new DrivetrainConstants());
    drive_ =
        new DifferentialDriveMech(
            DrivetrainConstants.LEFT_MOTORS, DrivetrainConstants.RIGHT_MOTORS);

    // Headings wrap around: 179 degrees and -179 degrees are only 2 degrees apart. Continuous
    // input tells the PID to take the short way around.
    aim_pid_.enableContinuousInput(-Math.PI, Math.PI);
    aim_pid_.setTolerance(Math.toRadians(DrivetrainConstants.AIM_TOLERANCE_DEGREES));
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
    switch (system_state_) {
      case AIM:
        // The PID's error is (goal - measurement). A positive error means the goal is to our left,
        // but a positive turn command turns the robot RIGHT, so the sign is flipped.
        aim_turn_ =
            -aim_pid_.calculate(
                getPose().getRotation().getRadians(), getAngleToGoal().getRadians());
        drive_.arcadeDrive(0.0, aim_turn_);
        break;
      case ARCADE:
        aim_pid_.reset();
        aim_turn_ = 0.0;
        drive_.arcadeDrive(OI.getForward(), OI.getTurn());
        break;
      case IDLE:
      default:
        aim_pid_.reset();
        aim_turn_ = 0.0;
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

  /** The direction from the robot to the goal, as a field heading (0 = along +x, left is +). */
  public Rotation2d getAngleToGoal() {
    return DrivetrainConstants.GOAL.getTranslation().minus(getPose().getTranslation()).getAngle();
  }

  /**
   * How far the robot still has to turn to face the goal, in radians from -pi to pi. Positive means
   * the goal is to the robot's left.
   */
  public double getAimErrorRadians() {
    return MathUtil.angleModulus(getAngleToGoal().minus(getPose().getRotation()).getRadians());
  }

  /** True when the robot is facing the goal (within the tolerance) and has stopped turning. */
  public boolean isAimed() {
    return Math.abs(Math.toDegrees(getAimErrorRadians()))
            < DrivetrainConstants.AIM_TOLERANCE_DEGREES
        && Math.abs(drive_.getAngularSpeed()) < 0.15;
  }
}
