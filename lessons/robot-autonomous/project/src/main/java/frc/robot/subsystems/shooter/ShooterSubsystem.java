package frc.robot.subsystems.shooter;

import com.marswars.logging.MwLog;
import com.marswars.mechanisms.FlywheelMech;
import com.marswars.mechanisms.RollerMech;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.FieldTargets;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.shooter.ShooterConstants.ShooterStates;
import java.util.List;

/**
 * The shooter, now with a state machine. The driver (through a command) only says what they WANT:
 * "shoot" or "idle". The subsystem decides what state it can actually be in, one safe step at a
 * time.
 */
public class ShooterSubsystem extends MwSubsystem<ShooterStates, ShooterConstants> {
  private static ShooterSubsystem instance_ = null;

  public static ShooterSubsystem getInstance() {
    if (instance_ == null) {
      instance_ = new ShooterSubsystem();
    }
    return instance_;
  }

  private final FlywheelMech flywheel_;
  private final RollerMech roller_;

  // Bookkeeping for the lesson's checks. You do not need to change it.
  private final Timer spin_up_timer_ = new Timer();
  private ShooterStates last_state_ = ShooterStates.IDLE;
  private long shot_count_ = 0;
  private double spin_up_seconds_ = 0.0;
  private double shot_distance_from_score_spot_ =
      0.0; // how far from the scoring spot the last shot was
  // How far from facing the goal (in degrees) the last shot was, and the worst of all the shots so
  // far. The worst starts at 180 so that "no shot yet" can never look like "aimed well".
  private double aim_error_at_shot_ = 0.0;
  private double worst_aim_error_at_shot_ = NO_SHOT_YET_DEGREES;
  private static final double NO_SHOT_YET_DEGREES = 180.0;

  private ShooterSubsystem() {
    super(ShooterStates.IDLE, new ShooterConstants());

    flywheel_ =
        new FlywheelMech(
            getSubsystemKey(),
            "Flywheel",
            List.of(CONSTANTS.FLYWHEEL_MOTOR_CONFIG),
            CONSTANTS.FLYWHEEL_GEAR_RATIO,
            CONSTANTS.FLYWHEEL_INERTIA,
            CONSTANTS.FLYWHEEL_RADIUS);
    roller_ =
        new RollerMech(
            getSubsystemKey(),
            "Roller",
            List.of(CONSTANTS.ROLLER_MOTOR_CONFIG),
            CONSTANTS.ROLLER_GEAR_RATIO,
            CONSTANTS.ROLLER_INERTIA);
  }

  @Override
  public List<SubsystemIoBase> getIos() {
    return List.of(flywheel_, roller_);
  }

  @Override
  public void reset() {
    system_state_ = ShooterStates.IDLE;
  }

  /** Starts the shot counter over (used at the start of autonomous). */
  public void resetShotCount() {
    shot_count_ = 0;
    shot_distance_from_score_spot_ = 0.0;
    aim_error_at_shot_ = 0.0;
    worst_aim_error_at_shot_ = NO_SHOT_YET_DEGREES;
  }

  /** True when the flywheel is close enough to the target speed. */
  public boolean isAtSpeed() {
    return flywheel_.getCurrentVelocity()
        >= CONSTANTS.SHOOT_VELOCITY * (1.0 - CONSTANTS.AT_SPEED_TOLERANCE);
  }

  /** True when the flywheel has slowed down too much (for example after launching a piece). */
  public boolean hasDipped() {
    return flywheel_.getCurrentVelocity()
        < CONSTANTS.SHOOT_VELOCITY * (1.0 - CONSTANTS.DIP_TOLERANCE);
  }

  /** Pretends a game piece was just launched, which pushes back on the flywheel. */
  public void simulateBallLaunch() {
    flywheel_.applyLoadTorque(CONSTANTS.BALL_LOAD_TORQUE);
  }

  /**
   * Decides which state the shooter is in. MWLib calls this every loop with the state the driver
   * WANTS. Set system_state_ to the state we are actually allowed to go to.
   *
   * <p>Rules: IDLE always goes straight to IDLE. If SHOOT is wanted, you may NOT jump from IDLE to
   * SHOOT: go IDLE -> SPIN_UP first. From SPIN_UP go to SHOOT once the flywheel isAtSpeed(). From
   * SHOOT go back to SPIN_UP if the flywheel hasDipped().
   */
  @Override
  protected void handleStateTransition(ShooterStates wanted) {
    if (wanted == ShooterStates.IDLE) {
      system_state_ = ShooterStates.IDLE;
      return;
    }

    // The driver wants to shoot. Take one safe step at a time.
    switch (system_state_) {
      case IDLE:
        system_state_ = ShooterStates.SPIN_UP;
        break;
      case SPIN_UP:
        if (isAtSpeed()) {
          system_state_ = ShooterStates.SHOOT;
        }
        break;
      case SHOOT:
        if (hasDipped()) {
          system_state_ = ShooterStates.SPIN_UP;
        }
        break;
      default:
        break;
    }
  }

  /** Runs every 20 ms. Decide what each mechanism does in the current state. */
  @Override
  public void updateLogic(double timestamp) {
    trackShots();

    switch (system_state_) {
      case SPIN_UP:
        runFlywheel();
        roller_.setTargetDutyCycle(0.0);
        break;
      case SHOOT:
        runFlywheel();
        roller_.setTargetDutyCycle(CONSTANTS.INDEX_DUTY_CYCLE);
        break;
      case IDLE:
      default:
        flywheel_.setTargetDutyCycle(0.0);
        roller_.setTargetDutyCycle(0.0);
        break;
    }

    MwLog.log(getSubsystemKey() + "FlywheelVelocity", flywheel_.getCurrentVelocity());
    MwLog.log(getSubsystemKey() + "FlywheelTarget", CONSTANTS.SHOOT_VELOCITY);
    MwLog.log(getSubsystemKey() + "ShotCount", shot_count_);
    MwLog.log(getSubsystemKey() + "SpinUpSeconds", spin_up_seconds_);
    MwLog.log(getSubsystemKey() + "ShotDistanceFromScoreSpot", shot_distance_from_score_spot_);
    MwLog.log(getSubsystemKey() + "AimErrorAtShotDegrees", aim_error_at_shot_);
    // The lesson's check reads this one: the worst aim of any shot, 180 until the first shot.
    MwLog.log("Check/" + getSubsystemKey() + "AimErrorAtShotDegrees/Max", worst_aim_error_at_shot_);
  }

  /** Holds the flywheel at its target speed using whichever control style the constants choose. */
  private void runFlywheel() {
    switch (CONSTANTS.FLYWHEEL_CONTROL) {
      case BANG_BANG:
        bangBang();
        break;
      case FEEDFORWARD:
        feedforward();
        break;
      case PID:
      default:
        // The motor controller runs feedforward (kV) plus feedback (kP) for us.
        flywheel_.setTargetVelocity(CONSTANTS.SHOOT_VELOCITY);
        break;
    }
  }

  /**
   * Bang-bang control: full power while the flywheel is too slow, no power once it is fast enough.
   */
  private void bangBang() {
    if (flywheel_.getCurrentVelocity() < CONSTANTS.SHOOT_VELOCITY) {
      flywheel_.setTargetDutyCycle(1.0);
    } else {
      flywheel_.setTargetDutyCycle(0.0);
    }
  }

  /**
   * Feedforward control: no measuring at all. Work out how much power the target speed needs and
   * apply it.
   */
  private void feedforward() {
    double target_rps = CONSTANTS.SHOOT_VELOCITY / (2.0 * Math.PI);
    double volts = CONSTANTS.FLYWHEEL_KV * target_rps;
    flywheel_.setTargetDutyCycle(volts / 12.0);
  }

  /** Counts launches and times each spin-up. Used by the lesson checks. */
  private void trackShots() {
    if (system_state_ != last_state_) {
      if (system_state_ == ShooterStates.SPIN_UP) {
        spin_up_timer_.restart();
      }
      if (system_state_ == ShooterStates.SHOOT) {
        shot_count_++;
        // Where the robot REALLY was when this shot started (the simulation knows).
        shot_distance_from_score_spot_ =
            DrivetrainSubsystem.getInstance()
                .getTruePose()
                .getTranslation()
                .getDistance(FieldTargets.SCORE_SPOT.getTranslation());
        // How far from facing the goal the robot REALLY was when this shot started.
        aim_error_at_shot_ = Math.abs(DrivetrainSubsystem.getInstance().getTrueAimErrorDegrees());
        worst_aim_error_at_shot_ =
            shot_count_ == 1
                ? aim_error_at_shot_
                : Math.max(worst_aim_error_at_shot_, aim_error_at_shot_);
        spin_up_seconds_ = spin_up_timer_.get();
      }
      last_state_ = system_state_;
    }
  }
}
