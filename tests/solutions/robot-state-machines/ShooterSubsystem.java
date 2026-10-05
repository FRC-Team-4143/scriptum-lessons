package frc.robot.subsystems.shooter;

import com.marswars.logging.MwLog;
import com.marswars.mechanisms.FlywheelMech;
import com.marswars.mechanisms.RollerMech;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.shooter.ShooterConstants.ShooterStates;
import java.util.List;

/**
 * The shooter, now with a state machine. The driver (through a command) only says what they WANT:
 * "shoot" or "idle". The subsystem decides what state it can actually be in, one safe step at a
 * time.
 */
public class ShooterSubsystem extends MwSubsystem<ShooterStates, ShooterConstants> {
  private static ShooterSubsystem instance = null;

  public static ShooterSubsystem getInstance() {
    if (instance == null) {
      instance = new ShooterSubsystem();
    }
    return instance;
  }

  private final FlywheelMech flywheel;
  private final RollerMech roller;

  // Bookkeeping for the lesson's checks. You do not need to change it.
  private final Timer spinUpTimer = new Timer();
  private ShooterStates lastState = ShooterStates.IDLE;
  private long shotCount = 0;
  private double spinUpSeconds = 0.0;

  private ShooterSubsystem() {
    super(ShooterStates.IDLE, new ShooterConstants());

    flywheel =
        new FlywheelMech(
            getSubsystemKey(),
            "Flywheel",
            List.of(CONSTANTS.FLYWHEEL_MOTOR_CONFIG),
            CONSTANTS.FLYWHEEL_GEAR_RATIO,
            CONSTANTS.FLYWHEEL_INERTIA,
            CONSTANTS.FLYWHEEL_RADIUS);
    roller =
        new RollerMech(
            getSubsystemKey(),
            "Roller",
            List.of(CONSTANTS.ROLLER_MOTOR_CONFIG),
            CONSTANTS.ROLLER_GEAR_RATIO,
            CONSTANTS.ROLLER_INERTIA);
  }

  @Override
  public List<SubsystemIoBase> getIos() {
    return List.of(flywheel, roller);
  }

  @Override
  public void reset() {
    system_state_ = ShooterStates.IDLE;
  }

  /** True when the flywheel is close enough to the target speed. */
  public boolean isAtSpeed() {
    return flywheel.getCurrentVelocity()
        >= CONSTANTS.SHOOT_VELOCITY * (1.0 - CONSTANTS.AT_SPEED_TOLERANCE);
  }

  /** True when the flywheel has slowed down too much (for example after launching a piece). */
  public boolean hasDipped() {
    return flywheel.getCurrentVelocity()
        < CONSTANTS.SHOOT_VELOCITY * (1.0 - CONSTANTS.DIP_TOLERANCE);
  }

  /** Pretends a game piece was just launched, which pushes back on the flywheel. */
  public void simulateBallLaunch() {
    flywheel.applyLoadTorque(CONSTANTS.BALL_LOAD_TORQUE);
  }

  /**
   * Decides which state the shooter is in. MWLib calls this every loop with the state the driver
   * WANTS. Set system_state_ to the state we are actually allowed to go to.
   *
   * <p>Rules: IDLE always goes straight to IDLE. If SHOOT is wanted, you may NOT jump from IDLE
   * to SHOOT: go IDLE -> SPIN_UP first. From SPIN_UP go to SHOOT once the flywheel isAtSpeed().
   * From SHOOT go back to SPIN_UP if the flywheel hasDipped().
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
        flywheel.setTargetVelocity(CONSTANTS.SHOOT_VELOCITY);
        roller.setTargetDutyCycle(0.0);
        break;
      case SHOOT:
        flywheel.setTargetVelocity(CONSTANTS.SHOOT_VELOCITY);
        roller.setTargetDutyCycle(CONSTANTS.INDEX_DUTY_CYCLE);
        break;
      case IDLE:
      default:
        flywheel.setTargetDutyCycle(0.0);
        roller.setTargetDutyCycle(0.0);
        break;
    }

    MwLog.log(getSubsystemKey() + "FlywheelVelocity", flywheel.getCurrentVelocity());
    MwLog.log(getSubsystemKey() + "FlywheelTarget", CONSTANTS.SHOOT_VELOCITY);
    MwLog.log(getSubsystemKey() + "ShotCount", shotCount);
    MwLog.log(getSubsystemKey() + "SpinUpSeconds", spinUpSeconds);
  }

  /** Counts launches and times each spin-up. Used by the lesson checks. */
  private void trackShots() {
    if (system_state_ != lastState) {
      if (system_state_ == ShooterStates.SPIN_UP) {
        spinUpTimer.restart();
      }
      if (system_state_ == ShooterStates.SHOOT) {
        shotCount++;
        spinUpSeconds = spinUpTimer.get();
      }
      lastState = system_state_;
    }
  }
}
