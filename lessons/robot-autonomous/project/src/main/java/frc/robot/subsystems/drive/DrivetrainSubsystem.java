package frc.robot.subsystems.drive;

import choreo.trajectory.DifferentialSample;
import choreo.trajectory.Trajectory;
import com.marswars.auto.ChoreoEventTracker;
import com.marswars.auto.ChoreoTrajectory;
import com.marswars.logging.MwLog;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.FieldTargets;
import frc.robot.OI;
import frc.robot.mechanisms.DifferentialDriveMech;
import frc.robot.subsystems.drive.DrivetrainConstants.DriveStates;
import java.util.List;
import java.util.function.Supplier;

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

  // The speeds autonomous commands ask for. Only used in the COMMANDED state.
  private double commanded_forward_ = 0.0;
  private double commanded_turn_ = 0.0;

  // The PID that turns the robot to face the goal (state AIM). You built and tuned this in the
  // State Machines lesson; here it is finished, and the autonomous routine just uses it.
  private final PIDController aim_pid_ =
      new PIDController(
          DrivetrainConstants.AIM_KP, DrivetrainConstants.AIM_KI, DrivetrainConstants.AIM_KD);
  private double aim_turn_ = 0.0; // the turn the aim state last sent to the drive

  // Choreo path following (state CHOREO_PATH). Provided: you do not need to change any of this.
  private final DifferentialPathFollower path_follower_;
  private final ChoreoEventTracker event_tracker_;
  private final Timer path_timer_ = new Timer(); // how far along the path we are, in seconds
  private Trajectory<DifferentialSample> path_ = null;

  private DrivetrainSubsystem() {
    super(DriveStates.IDLE, new DrivetrainConstants());
    drive_ =
        new DifferentialDriveMech(
            DrivetrainConstants.LEFT_MOTORS, DrivetrainConstants.RIGHT_MOTORS);
    path_follower_ = new DifferentialPathFollower(CONSTANTS, drive_.getKinematics());
    // The event tracker watches the clock and says when each marker you put on the path in Choreo
    // has been passed. It also knows the robot's pose, for markers that trigger by position.
    event_tracker_ = new ChoreoEventTracker(getSubsystemKey() + "Choreo/Events/", this::getPose);

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
        resetAim();
        drive_.arcadeDrive(OI.getForward(), OI.getTurn());
        break;
      case COMMANDED:
        resetAim();
        drive_.arcadeDrive(commanded_forward_, commanded_turn_);
        break;
      case CHOREO_PATH:
        resetAim();
        followPath();
        break;
      case IDLE:
      default:
        resetAim();
        drive_.arcadeDrive(0.0, 0.0);
        break;
    }

    // Logged every loop (in every state) so you can plot them in AdvantageScope.
    MwLog.log(getSubsystemKey() + "AimErrorDegrees", Math.toDegrees(getAimErrorRadians()));
    MwLog.log(getSubsystemKey() + "AimOutput", aim_turn_);
    MwLog.log(getSubsystemKey() + "IsAimed", isAimed());
  }

  /** Forget the aim PID's old error so it does not carry into the next aim. */
  private void resetAim() {
    aim_pid_.reset();
    aim_turn_ = 0.0;
  }

  /**
   * Asks the drivetrain to drive at these speeds. Commands call this. It only has an effect while
   * the drivetrain is in the COMMANDED state.
   *
   * @param forward -1.0 (backward) to 1.0 (forward)
   * @param turn -1.0 (turn left) to 1.0 (turn right)
   */
  public void setCommandedSpeeds(double forward, double turn) {
    commanded_forward_ = forward;
    commanded_turn_ = turn;
  }

  /** Puts the robot back at the origin facing forward, with fresh encoders. */
  public void resetPose() {
    drive_.resetPose();
  }

  /** Tells the robot where it is on the field (autonomous uses this to start on the path). */
  public void resetPose(Pose2d pose) {
    drive_.resetPose(pose);
  }

  /** Where the robot REALLY is (the simulation knows; on a real robot it is just the estimate). */
  public Pose2d getTruePose() {
    return drive_.getTruePose();
  }

  /** The constants for this subsystem (autonomous commands read their gains from here). */
  public DrivetrainConstants getConstants() {
    return CONSTANTS;
  }

  /** How fast the robot is turning in radians per second. Positive is turning left. */
  public double getAngularSpeed() {
    return drive_.getChassisSpeeds().omegaRadiansPerSecond;
  }

  /** Where the robot thinks it is (the drive mechanism works this out). */
  public Pose2d getPose() {
    return drive_.getPose();
  }

  /** The direction from the robot to the goal, as a field heading (0 = along +x, left is +). */
  public Rotation2d getAngleToGoal() {
    return FieldTargets.GOAL.getTranslation().minus(getPose().getTranslation()).getAngle();
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

  /**
   * How far the robot REALLY is from facing the goal, in degrees (the simulation knows; positive
   * means the goal is to the left). Used by the lesson's checks, so a drifting pose estimate cannot
   * fool them.
   */
  public double getTrueAimErrorDegrees() {
    Pose2d truth = getTruePose();
    Rotation2d to_goal =
        FieldTargets.GOAL.getTranslation().minus(truth.getTranslation()).getAngle();
    return to_goal.minus(truth.getRotation()).getDegrees();
  }

  // ---------------------------------------------------------------------------------------------
  // Choreo path following. This is the same shape as the competition robot's swerve code: pick a
  // path, switch to the CHOREO_PATH state, and wait until the robot has arrived. Provided for you.
  // ---------------------------------------------------------------------------------------------

  /**
   * Picks the path to follow next. The path's clock restarts at zero and its event markers are
   * reset.
   */
  public void setDesiredChoreoTrajectory(ChoreoTrajectory trajectory) {
    path_ = trajectory.getDifferentialTrajectory();
    event_tracker_.setEvents(trajectory);
    event_tracker_.start();
    path_timer_.stop();
    path_timer_.reset();
    MwLog.log(getSubsystemKey() + "Choreo/Trajectory", path_.getPoses());
    MwLog.log(getSubsystemKey() + "Choreo/TrajName", path_.name());
    MwLog.log(getSubsystemKey() + "Choreo/TotalTime", path_.getTotalTime());
  }

  /** A command that picks the path to follow next (see {@link #setDesiredChoreoTrajectory}). */
  public Command setDesiredChoreoTrajectoryCommand(Supplier<ChoreoTrajectory> trajectory) {
    return Commands.runOnce(() -> setDesiredChoreoTrajectory(trajectory.get()));
  }

  /** Runs every loop in the CHOREO_PATH state: drive the part of the path for "right now". */
  private void followPath() {
    if (path_ == null) {
      drive_.setDutyCycles(0.0, 0.0);
      return;
    }
    Pose2d robot = getPose();
    DifferentialSample ref = DifferentialPathFollower.sampleAt(path_, path_timer_.get());
    // Wait for the robot if it has fallen far behind the path, instead of leaving it behind.
    boolean too_far_behind =
        ref.getPose().getTranslation().getDistance(robot.getTranslation())
            > CONSTANTS.CHOREO_LOOK_AHEAD_METERS;
    if (too_far_behind) {
      path_timer_.stop();
    } else {
      path_timer_.start();
    }
    event_tracker_.update(path_timer_.get());

    // Follow the path while its clock is running; after that, settle onto the end point.
    boolean path_over = path_timer_.get() >= path_.getTotalTime();
    double[] duty =
        path_over
            ? path_follower_.settle(robot, path_.getFinalPose(false).get())
            : path_follower_.calculate(robot, ref);
    drive_.setDutyCycles(duty[0], duty[1]);
    MwLog.log(getSubsystemKey() + "Choreo/TimerValue", path_timer_.get());
    MwLog.log(getSubsystemKey() + "Choreo/DesiredPose", ref.getPose());
  }

  /** True once the path's clock has run to the end of the path. */
  public boolean hasChoreoTimeElapsed() {
    return system_state_ == DriveStates.CHOREO_PATH
        && path_ != null
        && path_timer_.get() >= path_.getTotalTime();
  }

  /** True when the path has finished, the robot is at its end point, and it has stopped moving. */
  public boolean isAtChoreoSetpoint() {
    if (!hasChoreoTimeElapsed()) {
      return false;
    }
    Pose2d end = path_.getFinalPose(false).get();
    boolean close_enough =
        getPose().getTranslation().getDistance(end.getTranslation())
            < CONSTANTS.CHOREO_TRANSLATION_TOLERANCE_METERS;
    boolean stopped =
        Math.abs(drive_.getChassisSpeeds().vxMetersPerSecond)
            < CONSTANTS.CHOREO_STOPPED_SPEED_METERS_PER_SECOND;
    return close_enough && stopped;
  }

  /** Stops watching for event markers (the path is over). */
  public void stopChoreoEvents() {
    event_tracker_.stop();
  }

  /**
   * A trigger that turns true once the path's clock passes the event marker with this name, which
   * you place on the path in Choreo. Use it like a button: {@code .onTrue(command)}.
   */
  public Trigger getChoreoEventTimeTrigger(String event_name) {
    return event_tracker_.getTimeTrigger(event_name);
  }

  /** How long the current path takes, in seconds. */
  public double getChoreoTotalTime() {
    return path_ == null ? 0.0 : path_.getTotalTime();
  }
}
