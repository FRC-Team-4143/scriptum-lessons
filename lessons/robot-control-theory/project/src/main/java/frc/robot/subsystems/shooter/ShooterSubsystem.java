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
  private static ShooterSubsystem instance = null;

  public static ShooterSubsystem getInstance() {
    if (instance == null) {
      instance = new ShooterSubsystem();
    }
    return instance;
  }

  private final FlywheelMech flywheel;
  private final RollerMech roller;

  // Bookkeeping for the lesson's checks and the launch button. You do not need to change it.
  private final Timer spinUpTimer = new Timer();
  private final Timer recoveryTimer = new Timer();
  private boolean wasShooting = false;
  private boolean waitingForSpeed = false;
  private boolean waitingForRecovery = false;
  private boolean dippedAfterLaunch = false;
  private boolean launchWasPressed = false;
  private double spinUpSeconds = 0.0;
  private double recoverySeconds = 0.0;

  private ShooterSubsystem() {
    super(ShooterStates.MANUAL, new ShooterConstants());

    // CONSTANTS is this subsystem's ShooterConstants. getSubsystemKey() gives the mechanism a name
    // to log under.
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

  /** The mechanisms this subsystem owns. MWLib reads and writes them for us every loop. */
  @Override
  public List<SubsystemIoBase> getIos() {
    return List.of(flywheel, roller);
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
      flywheel.setTargetDutyCycle(0.0);
    }

    // The roller feeds game pieces into the flywheel while the index button is held.
    double rollerDuty = OI.getIndexButton() ? CONSTANTS.INDEX_DUTY_CYCLE : 0.0;
    roller.setTargetDutyCycle(rollerDuty);

    // Tap the launch button to pretend a game piece was just fired (once per tap).
    boolean launchPressed = OI.getLaunchButton();
    if (launchPressed && !launchWasPressed) {
      simulateBallLaunch();
    }
    launchWasPressed = launchPressed;

    trackTiming();

    // Send numbers to AdvantageScope.
    MwLog.log(getSubsystemKey() + "FlywheelVelocity", flywheel.getCurrentVelocity());
    MwLog.log(getSubsystemKey() + "FlywheelTarget", CONSTANTS.SHOOT_VELOCITY);
    MwLog.log(
        getSubsystemKey() + "FlywheelError",
        CONSTANTS.SHOOT_VELOCITY - flywheel.getCurrentVelocity());
    MwLog.log(getSubsystemKey() + "FlywheelStyle", CONSTANTS.FLYWHEEL_CONTROL);
    MwLog.log(getSubsystemKey() + "RollerDuty", rollerDuty);
    MwLog.log(getSubsystemKey() + "SpinUpSeconds", spinUpSeconds);
    MwLog.log(getSubsystemKey() + "RecoverySeconds", recoverySeconds);
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
        flywheel.setTargetVelocity(CONSTANTS.SHOOT_VELOCITY);
        break;
    }
  }

  /**
   * Bang-bang control: full power while the flywheel is too slow, no power once it is fast enough.
   *
   * <p>TODO: if flywheel.getCurrentVelocity() is below CONSTANTS.SHOOT_VELOCITY, call
   * flywheel.setTargetDutyCycle(1.0). Otherwise call flywheel.setTargetDutyCycle(0.0).
   */
  private void bangBang() {
    flywheel.setTargetDutyCycle(0.0);
  }

  /**
   * Feedforward control: no measuring at all. Work out how much power the target speed needs and
   * apply it.
   *
   * <p>TODO: the voltage needed is FLYWHEEL_KV times the target speed in rotations per second.
   * (SHOOT_VELOCITY is in radians per second, so divide it by 2 * Math.PI.) A duty cycle is a
   * fraction of 12 volts, so divide the voltage by 12.0 and pass it to
   * flywheel.setTargetDutyCycle(...).
   */
  private void feedforward() {
    flywheel.setTargetDutyCycle(0.0);
  }

  /** True when the flywheel is close enough to the target speed. */
  public boolean isAtSpeed() {
    return flywheel.getCurrentVelocity()
        >= CONSTANTS.SHOOT_VELOCITY * (1.0 - CONSTANTS.AT_SPEED_TOLERANCE);
  }

  /** Pretends a game piece was just launched, which pushes back on the flywheel. */
  public void simulateBallLaunch() {
    flywheel.applyLoadTorque(CONSTANTS.BALL_LOAD_TORQUE);
    if (OI.getShootButton()) {
      recoveryTimer.restart();
      waitingForRecovery = true;
      dippedAfterLaunch = false;
    }
  }

  /** Times each spin-up and each recovery after a launch. Used by the lesson checks. */
  private void trackTiming() {
    boolean shooting = OI.getShootButton();
    if (shooting && !wasShooting) {
      spinUpTimer.restart();
      waitingForSpeed = true;
    }
    if (!shooting) {
      waitingForSpeed = false;
      waitingForRecovery = false;
    }
    wasShooting = shooting;

    if (waitingForSpeed && isAtSpeed()) {
      spinUpSeconds = spinUpTimer.get();
      waitingForSpeed = false;
    }
    if (waitingForRecovery) {
      if (!isAtSpeed()) {
        dippedAfterLaunch = true;
      } else if (dippedAfterLaunch) {
        recoverySeconds = recoveryTimer.get();
        waitingForRecovery = false;
      }
    }
  }
}
