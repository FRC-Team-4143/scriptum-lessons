package frc.robot.subsystems.drive;

import choreo.trajectory.DifferentialSample;
import choreo.trajectory.Trajectory;
import com.marswars.auto.ChoreoEventTracker;
import com.marswars.auto.ChoreoTrajectory;
import com.marswars.logging.MwLog;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
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

  // Choreo path following (state CHOREO_PATH). Provided: you do not need to change any of this.
  private final DifferentialPathFollower pathFollower;
  private final ChoreoEventTracker eventTracker;
  private final Timer pathTimer = new Timer(); // how far along the path we are, in seconds
  private Trajectory<DifferentialSample> path = null;

  private DrivetrainSubsystem() {
    super(DriveStates.IDLE, new DrivetrainConstants());
    drive =
        new DifferentialDriveMech(
            DrivetrainConstants.LEFT_MOTORS, DrivetrainConstants.RIGHT_MOTORS);
    pathFollower = new DifferentialPathFollower(CONSTANTS, drive.getKinematics());
    // The event tracker watches the clock and says when each marker you put on the path in Choreo
    // has been passed. It also knows the robot's pose, for markers that trigger by position.
    eventTracker = new ChoreoEventTracker(getSubsystemKey() + "Choreo/Events/", this::getPose);
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
      case CHOREO_PATH:
        followPath();
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

  /** Tells the robot where it is on the field (autonomous uses this to start on the path). */
  public void resetPose(Pose2d pose) {
    drive.resetPose(pose);
  }

  /** Where the robot REALLY is (the simulation knows; on a real robot it is just the estimate). */
  public Pose2d getTruePose() {
    return drive.getTruePose();
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

  // ---------------------------------------------------------------------------------------------
  // Choreo path following. This is the same shape as the competition robot's swerve code: pick a
  // path, switch to the CHOREO_PATH state, and wait until the robot has arrived. Provided for you.
  // ---------------------------------------------------------------------------------------------

  /**
   * Picks the path to follow next. The path's clock restarts at zero and its event markers are
   * reset.
   */
  public void setDesiredChoreoTrajectory(ChoreoTrajectory trajectory) {
    path = trajectory.getDifferentialTrajectory();
    eventTracker.setEvents(trajectory);
    eventTracker.start();
    pathTimer.stop();
    pathTimer.reset();
    MwLog.log(getSubsystemKey() + "Choreo/Trajectory", path.getPoses());
    MwLog.log(getSubsystemKey() + "Choreo/TrajName", path.name());
    MwLog.log(getSubsystemKey() + "Choreo/TotalTime", path.getTotalTime());
  }

  /** A command that picks the path to follow next (see {@link #setDesiredChoreoTrajectory}). */
  public Command setDesiredChoreoTrajectoryCommand(Supplier<ChoreoTrajectory> trajectory) {
    return Commands.runOnce(() -> setDesiredChoreoTrajectory(trajectory.get()));
  }

  /** Runs every loop in the CHOREO_PATH state: drive the part of the path for "right now". */
  private void followPath() {
    if (path == null) {
      drive.setDutyCycles(0.0, 0.0);
      return;
    }
    Pose2d robot = getPose();
    DifferentialSample ref = DifferentialPathFollower.sampleAt(path, pathTimer.get());
    // Wait for the robot if it has fallen far behind the path, instead of leaving it behind.
    boolean tooFarBehind =
        ref.getPose().getTranslation().getDistance(robot.getTranslation())
            > CONSTANTS.CHOREO_LOOK_AHEAD_METERS;
    if (tooFarBehind) {
      pathTimer.stop();
    } else {
      pathTimer.start();
    }
    eventTracker.update(pathTimer.get());

    // Follow the path while its clock is running; after that, settle onto the end point.
    boolean pathOver = pathTimer.get() >= path.getTotalTime();
    double[] duty =
        pathOver
            ? pathFollower.settle(robot, path.getFinalPose(false).get())
            : pathFollower.calculate(robot, ref);
    drive.setDutyCycles(duty[0], duty[1]);
    MwLog.log(getSubsystemKey() + "Choreo/TimerValue", pathTimer.get());
    MwLog.log(getSubsystemKey() + "Choreo/DesiredPose", ref.getPose());
  }

  /** True once the path's clock has run to the end of the path. */
  public boolean hasChoreoTimeElapsed() {
    return system_state_ == DriveStates.CHOREO_PATH
        && path != null
        && pathTimer.get() >= path.getTotalTime();
  }

  /** True when the path has finished, the robot is at its end point, and it has stopped moving. */
  public boolean isAtChoreoSetpoint() {
    if (!hasChoreoTimeElapsed()) {
      return false;
    }
    Pose2d end = path.getFinalPose(false).get();
    boolean closeEnough =
        getPose().getTranslation().getDistance(end.getTranslation())
            < CONSTANTS.CHOREO_TRANSLATION_TOLERANCE_METERS;
    boolean stopped =
        Math.abs(drive.getChassisSpeeds().vxMetersPerSecond)
            < CONSTANTS.CHOREO_STOPPED_SPEED_METERS_PER_SECOND;
    return closeEnough && stopped;
  }

  /** Stops watching for event markers (the path is over). */
  public void stopChoreoEvents() {
    eventTracker.stop();
  }

  /**
   * A trigger that turns true once the path's clock passes the event marker with this name, which
   * you place on the path in Choreo. Use it like a button: {@code .onTrue(command)}.
   */
  public Trigger getChoreoEventTimeTrigger(String eventName) {
    return eventTracker.getTimeTrigger(eventName);
  }

  /** How long the current path takes, in seconds. */
  public double getChoreoTotalTime() {
    return path == null ? 0.0 : path.getTotalTime();
  }
}
