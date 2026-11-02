package frc.robot.subsystems.intake;

import com.marswars.logging.MwLog;
import com.marswars.mechanisms.RollerMech;
import com.marswars.subsystem.MwSubsystem;
import com.marswars.subsystem.SubsystemIoBase;
import frc.robot.OI;
import frc.robot.subsystems.intake.IntakeConstants.IntakeStates;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import java.util.List;

/** The intake: one roller that pulls game pieces in. It waits while the shooter is feeding. */
public class IntakeSubsystem extends MwSubsystem<IntakeStates, IntakeConstants> {
  private static IntakeSubsystem instance_ = null;

  public static IntakeSubsystem getInstance() {
    if (instance_ == null) {
      instance_ = new IntakeSubsystem();
    }
    return instance_;
  }

  private final RollerMech roller_;

  private IntakeSubsystem() {
    super(IntakeStates.MANUAL, new IntakeConstants());
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
    return List.of(roller_);
  }

  @Override
  public void reset() {}

  /** Runs every 20 ms. */
  @Override
  public void updateLogic(double timestamp) {
    boolean run = OI.getIntakeButton() && !ShooterSubsystem.getInstance().isFeeding();
    double duty = run ? CONSTANTS.INTAKE_DUTY_CYCLE : 0.0;
    roller_.setTargetDutyCycle(duty);
    MwLog.log(getSubsystemKey() + "RollerDuty", duty);
  }
}
