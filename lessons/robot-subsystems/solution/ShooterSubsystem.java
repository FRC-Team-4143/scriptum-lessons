package frc.robot.subsystems.shooter;

import com.marswars.logging.MwLog;
import com.marswars.mechanisms.FlywheelMech;
import com.marswars.mechanisms.RollerMech;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import frc.robot.OI;
import frc.robot.subsystems.shooter.ShooterConstants.ShooterStates;
import java.util.List;

/**
 * The shooter: a flywheel that launches game pieces and a roller that feeds them in. Each is a
 * "mechanism" (a reusable piece of MWLib that owns its motors). The subsystem decides when each
 * mechanism does something.
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
    // Shoot button: spin the flywheel up to speed. Otherwise let it coast to a stop.
    if (OI.getShootButton()) {
      flywheel.setTargetVelocity(CONSTANTS.SHOOT_VELOCITY);
    } else {
      flywheel.setTargetDutyCycle(0.0);
    }

    // The roller feeds game pieces into the flywheel while the index button is held.
    double rollerDuty = OI.getIndexButton() ? CONSTANTS.INDEX_DUTY_CYCLE : 0.0;
    roller.setTargetDutyCycle(rollerDuty);

    // Send numbers to AdvantageScope.
    MwLog.log(getSubsystemKey() + "FlywheelVelocity", flywheel.getCurrentVelocity());
    MwLog.log(getSubsystemKey() + "FlywheelTarget", CONSTANTS.SHOOT_VELOCITY);
    MwLog.log(getSubsystemKey() + "RollerDuty", rollerDuty);
  }
}
