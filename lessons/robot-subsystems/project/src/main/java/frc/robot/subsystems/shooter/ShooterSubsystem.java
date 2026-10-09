package frc.robot.subsystems.shooter;

import com.marswars.logging.MwLog;
import com.marswars.mechanisms.FlywheelMech;
import com.marswars.mechanisms.RollerMech;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
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

    // TODO: create the roller the same way with a RollerMech. Its arguments are:
    //   (getSubsystemKey(), "Roller", List.of(<motor config>), <gear ratio>, <inertia>)
    roller = null;
  }

  /** The mechanisms this subsystem owns. MWLib reads and writes them for us every loop. */
  @Override
  public List<SubsystemIoBase> getIos() {
    // TODO: return both mechanisms. Use List.of(flywheel, roller)
    return List.of();
  }

  @Override
  public void reset() {}

  /** Runs every 20 ms. */
  @Override
  public void updateLogic(double timestamp) {
    // TODO: write the shoot button logic. While OI.getShootButton() is true, run the flywheel at
    // CONSTANTS.SHOOT_DUTY_CYCLE with flywheel.setTargetDutyCycle(...). Otherwise (else), set its
    // duty cycle to 0.0 so it coasts to a stop.

    // The roller feeds game pieces into the flywheel while the index button is held.
    double rollerDuty = 0.0; // TODO: use CONSTANTS.INDEX_DUTY_CYCLE while OI.getIndexButton()
    roller.setTargetDutyCycle(rollerDuty);

    // Send numbers to AdvantageScope.
    MwLog.log(getSubsystemKey() + "FlywheelVelocity", flywheel.getCurrentVelocity());
    MwLog.log(getSubsystemKey() + "RollerDuty", rollerDuty);
  }
}
