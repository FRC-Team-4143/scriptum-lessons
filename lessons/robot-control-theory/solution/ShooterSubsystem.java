package frc.robot.subsystems.shooter;

import com.marswars.logging.MwLog;
import com.marswars.mechanisms.FlywheelMech;
import com.marswars.mechanisms.RollerMech;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.OI;
import frc.robot.subsystems.shooter.ShooterConstants.ShooterStates;
import java.util.List;

/**
 * The shooter: a flywheel that launches game pieces and a roller that feeds them in. This lesson is
 * about HOW the flywheel holds its speed: see runFlywheel() and the methods below it.
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

  // Bookkeeping for the lesson's checks and the launch button. You do not need to change it.
  private final Timer spin_up_timer_ = new Timer();
  private final Timer recovery_timer_ = new Timer();
  private boolean was_shooting_ = false;
  private boolean waiting_for_speed_ = false;
  private boolean waiting_for_recovery_ = false;
  private boolean dipped_after_launch_ = false;
  private boolean launch_was_pressed_ = false;
  private double spin_up_seconds_ = 0.0;
  private double recovery_seconds_ = 0.0;

  private ShooterSubsystem() {
    super(ShooterStates.MANUAL, new ShooterConstants());

    // CONSTANTS is this subsystem's ShooterConstants. getSubsystemKey() gives the mechanism a name
    // to log under.
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

  /** The mechanisms this subsystem owns. MWLib reads and writes them for us every loop. */
  @Override
  public List<SubsystemIoBase> getIos() {
    return List.of(flywheel_, roller_);
  }

  @Override
  public void reset() {}

  /** Runs every 20 ms. */
  @Override
  public void updateLogic(double timestamp) {
    // Shoot button: hold the flywheel at its target speed. Otherwise let it coast to a stop.
    if (OI.getShootButton()) {
      runFlywheel();
    } else {
      flywheel_.setTargetDutyCycle(0.0);
    }

    // The roller feeds game pieces into the flywheel while the index button is held.
    double roller_duty = OI.getIndexButton() ? CONSTANTS.INDEX_DUTY_CYCLE : 0.0;
    roller_.setTargetDutyCycle(roller_duty);

    // Tap the launch button to pretend a game piece was just fired (once per tap).
    boolean launch_pressed = OI.getLaunchButton();
    if (launch_pressed && !launch_was_pressed_) {
      simulateBallLaunch();
    }
    launch_was_pressed_ = launch_pressed;

    trackTiming();

    // Send numbers to AdvantageScope.
    MwLog.log(getSubsystemKey() + "FlywheelVelocity", flywheel_.getCurrentVelocity());
    MwLog.log(getSubsystemKey() + "FlywheelTarget", CONSTANTS.SHOOT_VELOCITY);
    MwLog.log(
        getSubsystemKey() + "FlywheelError",
        CONSTANTS.SHOOT_VELOCITY - flywheel_.getCurrentVelocity());
    MwLog.log(getSubsystemKey() + "FlywheelStyle", CONSTANTS.FLYWHEEL_CONTROL);
    MwLog.log(getSubsystemKey() + "RollerDuty", roller_duty);
    MwLog.log(getSubsystemKey() + "SpinUpSeconds", spin_up_seconds_);
    MwLog.log(getSubsystemKey() + "RecoverySeconds", recovery_seconds_);
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

  /** True when the flywheel is close enough to the target speed. */
  public boolean isAtSpeed() {
    return flywheel_.getCurrentVelocity()
        >= CONSTANTS.SHOOT_VELOCITY * (1.0 - CONSTANTS.AT_SPEED_TOLERANCE);
  }

  /** Pretends a game piece was just launched, which pushes back on the flywheel. */
  public void simulateBallLaunch() {
    flywheel_.applyLoadTorque(CONSTANTS.BALL_LOAD_TORQUE);
    if (OI.getShootButton()) {
      recovery_timer_.restart();
      waiting_for_recovery_ = true;
      dipped_after_launch_ = false;
    }
  }

  /** Times each spin-up and each recovery after a launch. Used by the lesson checks. */
  private void trackTiming() {
    boolean shooting = OI.getShootButton();
    if (shooting && !was_shooting_) {
      spin_up_timer_.restart();
      waiting_for_speed_ = true;
    }
    if (!shooting) {
      waiting_for_speed_ = false;
      waiting_for_recovery_ = false;
    }
    was_shooting_ = shooting;

    if (waiting_for_speed_ && isAtSpeed()) {
      spin_up_seconds_ = spin_up_timer_.get();
      waiting_for_speed_ = false;
    }
    if (waiting_for_recovery_) {
      if (!isAtSpeed()) {
        dipped_after_launch_ = true;
      } else if (dipped_after_launch_) {
        recovery_seconds_ = recovery_timer_.get();
        waiting_for_recovery_ = false;
      }
    }
  }
}
