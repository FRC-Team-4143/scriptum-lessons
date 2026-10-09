package frc.robot.mechanisms;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.hardware.traits.CommonTalon;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.sim.ChassisReference;
import com.marswars.logging.MwLog;
import com.marswars.mechanisms.MechBase;
import com.marswars.mechanisms.MotorConfig;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import frc.robot.Constants;
import java.util.List;
import java.util.Random;
import org.littletonrobotics.junction.Logger;

/**
 * The robot's drivetrain. It has a left side and a right side, and each side can have several
 * motors. You tell it how hard each side should push and it takes care of the motors, the sensors,
 * and (in simulation) the physics.
 *
 * <p>MWLib calls {@link #readInputs}, {@link #writeOutputs} and {@link #logData} for you every loop
 * (see LessonLoop), so you never have to call them yourself.
 */
public class DifferentialDriveMech extends MechBase {
  private final CommonTalon[] leftMotors;
  private final CommonTalon[] rightMotors;
  private final BaseStatusSignal[] signals;
  private final boolean[] leftInverted;
  private final boolean[] rightInverted;

  private final DutyCycleOut leftRequest = new DutyCycleOut(0);
  private final DutyCycleOut rightRequest = new DutyCycleOut(0);
  private final DriveInputsAutoLogged inputs = new DriveInputsAutoLogged();

  private final DifferentialDrivetrainSim sim;

  private static final double DEADBAND = 0.05; // commands smaller than this are treated as 0.0

  // Simulated sensor imperfections (simulation only). The seed is fixed so every student sees the
  // same robot.
  private static final double LEFT_SCALE = 1.004; // left wheels read 0.4% too far
  private static final double RIGHT_SCALE = 0.997; // right wheels read 0.3% too short
  private static final double SLIP_NOISE = 0.02; // random slip, as a fraction of each movement
  private static final double JITTER_METERS = 0.0005; // encoder position jitter
  private static final double JITTER_METERS_PER_SECOND = 0.01; // encoder velocity jitter
  private final Random noise = new Random(4143);

  // Simulated friction (simulation only). See updateSimulation().
  private static final double STATIC_FRICTION_VOLTS = 0.25; // volts it takes to get a wheel moving
  private static final double SCRUB_VOLTS_PER_METER_PER_SECOND = 4.5; // how hard turning drags
  private static final double SUPPLY_VOLTS = 12.0; // battery voltage
  private static final double CURRENT_LIMIT_AMPS = 120.0; // per motor, like the TalonFX default
  private static final DCMotor DRIVE_MOTOR = DCMotor.getKrakenX60(1);
  private double lastTrueLeftMeters = 0.0;
  private double lastTrueRightMeters = 0.0;
  private double measuredLeftMeters = 0.0;
  private double measuredRightMeters = 0.0;

  public DifferentialDriveMech(List<MotorConfig> leftConfigs, List<MotorConfig> rightConfigs) {
    super("", "Drive");

    // MWLib builds the motors: the first one in each list is the leader, the rest follow it.
    ConstructedMotors left = configMotors(leftConfigs, Constants.GEAR_RATIO);
    ConstructedMotors right = configMotors(rightConfigs, Constants.GEAR_RATIO);
    leftMotors = left.motors;
    rightMotors = right.motors;

    signals = new BaseStatusSignal[left.signals.length + right.signals.length];
    System.arraycopy(left.signals, 0, signals, 0, left.signals.length);
    System.arraycopy(right.signals, 0, signals, left.signals.length, right.signals.length);

    leftInverted = invertedFlags(leftConfigs);
    rightInverted = invertedFlags(rightConfigs);

    // Physics model, used only in simulation.
    int motorsPerSide = Math.max(leftConfigs.size(), rightConfigs.size());
    sim =
        new DifferentialDrivetrainSim(
            DCMotor.getKrakenX60(motorsPerSide),
            Constants.GEAR_RATIO,
            7.5, // how hard the robot is to spin (kg*m^2)
            Constants.ROBOT_MASS_KG,
            Constants.WHEEL_RADIUS_METERS,
            Constants.TRACK_WIDTH_METERS,
            null);
  }

  /**
   * Sets how hard each side of the robot pushes.
   *
   * @param left -1.0 (full backward) to 1.0 (full forward)
   * @param right -1.0 (full backward) to 1.0 (full forward)
   */
  public void setDutyCycles(double left, double right) {
    // Real sticks never rest at exactly 0.0, so tiny commands are treated as 0.0. Otherwise the
    // robot would creep or spin on its own. (Later you will learn to do this yourself.)
    leftRequest.Output = MathUtil.applyDeadband(clamp(left), DEADBAND);
    rightRequest.Output = MathUtil.applyDeadband(clamp(right), DEADBAND);
  }

  @Override
  public void readInputs(double timestamp) {
    if (!MwLog.isReplay()) {
      BaseStatusSignal.refreshAll(signals);
      inputs.leftPositionRotations = leftMotors[0].getPosition().getValueAsDouble();
      inputs.rightPositionRotations = rightMotors[0].getPosition().getValueAsDouble();
      inputs.leftVelocityRps = leftMotors[0].getVelocity().getValueAsDouble();
      inputs.rightVelocityRps = rightMotors[0].getVelocity().getValueAsDouble();

      if (IS_SIM) {
        updateSimulation();
      }
    }
    Logger.processInputs(getLoggingKey() + "Inputs", inputs);
  }

  @Override
  public void writeOutputs(double timestamp) {
    leftMotors[0].setControl(leftRequest);
    rightMotors[0].setControl(rightRequest);
  }

  @Override
  public void logData() {
    MwLog.log(getLoggingKey() + "LeftOutput", leftRequest.Output);
    MwLog.log(getLoggingKey() + "RightOutput", rightRequest.Output);
    if (IS_SIM) {
      // Where the simulated robot REALLY is, to compare with the pose your code estimates.
      MwLog.log(getLoggingKey() + "TruePose", sim.getPose());
    }
  }

  private static double clamp(double value) {
    return Math.max(-1.0, Math.min(1.0, value));
  }

  // ---------------------------------------------------------------------------------------------
  // Simulation: feeds the motor voltages into a physics model, then tells the (simulated) motors
  // where the wheels ended up. You do not need to read this yet.
  // ---------------------------------------------------------------------------------------------

  private void updateSimulation() {
    for (int i = 0; i < leftMotors.length; i++) {
      prepareSimMotor((TalonFX) leftMotors[i], leftInverted[i]);
    }
    for (int i = 0; i < rightMotors.length; i++) {
      prepareSimMotor((TalonFX) rightMotors[i], rightInverted[i]);
    }

    // The motors are driven straight from the duty cycles rather than from the simulated TalonFX's
    // own voltage. The TalonFX only knows about its own motor, not the carpet dragging on the
    // wheels, so it believes a turning robot is stalled and cuts its output (to about 4 V). The
    // current limit is applied below instead, to the current the physics model really sees.
    double supplyVolts = DriverStation.isEnabled() ? SUPPLY_VOLTS : 0.0;
    double leftVolts = leftRequest.Output * supplyVolts;
    double rightVolts = rightRequest.Output * supplyVolts;

    // Friction, which the physics model leaves out. The first few volts on each side only break
    // the wheels loose, so a stick that rests a hair off center does not creep. And turning drags
    // the wheels sideways across the carpet ("scrub"), so the faster the robot spins the harder
    // the carpet pushes back. Without it the robot spins about 3 times faster than a real one.
    leftVolts = minusStaticFriction(leftVolts);
    rightVolts = minusStaticFriction(rightVolts);
    double spinMetersPerSecond =
        (sim.getRightVelocityMetersPerSecond() - sim.getLeftVelocityMetersPerSecond()) / 2.0;
    leftVolts += SCRUB_VOLTS_PER_METER_PER_SECOND * spinMetersPerSecond;
    rightVolts -= SCRUB_VOLTS_PER_METER_PER_SECOND * spinMetersPerSecond;
    leftVolts = limitCurrent(leftVolts, sim.getLeftVelocityMetersPerSecond());
    rightVolts = limitCurrent(rightVolts, sim.getRightVelocityMetersPerSecond());

    sim.setInputs(leftVolts, rightVolts);
    sim.update(0.020);

    // Real encoders are not perfect, so the simulated ones are not either. Each wheel reads a tiny
    // bit
    // too far or too short (wheels are never exactly the size we think), it slips a little as it
    // rolls, and the reading jitters. That is why the pose your code works out differs a little
    // from where the simulated robot really is (see Drive/TruePose).
    double leftDelta = sim.getLeftPositionMeters() - lastTrueLeftMeters;
    double rightDelta = sim.getRightPositionMeters() - lastTrueRightMeters;
    lastTrueLeftMeters = sim.getLeftPositionMeters();
    lastTrueRightMeters = sim.getRightPositionMeters();
    measuredLeftMeters += leftDelta * LEFT_SCALE * (1.0 + SLIP_NOISE * noise.nextGaussian());
    measuredRightMeters += rightDelta * RIGHT_SCALE * (1.0 + SLIP_NOISE * noise.nextGaussian());

    double leftMeters = measuredLeftMeters + JITTER_METERS * noise.nextGaussian();
    double rightMeters = measuredRightMeters + JITTER_METERS * noise.nextGaussian();
    double leftSpeed =
        sim.getLeftVelocityMetersPerSecond() * LEFT_SCALE
            + JITTER_METERS_PER_SECOND * noise.nextGaussian();
    double rightSpeed =
        sim.getRightVelocityMetersPerSecond() * RIGHT_SCALE
            + JITTER_METERS_PER_SECOND * noise.nextGaussian();

    double wheelCircumference = 2.0 * Math.PI * Constants.WHEEL_RADIUS_METERS;
    pushSimState(
        leftMotors,
        leftMeters / wheelCircumference * Constants.GEAR_RATIO,
        leftSpeed / wheelCircumference * Constants.GEAR_RATIO);
    pushSimState(
        rightMotors,
        rightMeters / wheelCircumference * Constants.GEAR_RATIO,
        rightSpeed / wheelCircumference * Constants.GEAR_RATIO);
  }

  /** Puts the simulated robot back at the origin with fresh (zeroed) encoders. */
  @SuppressWarnings("unused")
  private void resetSimulation() {
    sim.setPose(new Pose2d());
    lastTrueLeftMeters = 0.0;
    lastTrueRightMeters = 0.0;
    measuredLeftMeters = 0.0;
    measuredRightMeters = 0.0;
  }

  /** Keeps the current through one motor under the limit, like the TalonFX's stator limit. */
  private static double limitCurrent(double volts, double wheelMetersPerSecond) {
    double motorRadPerSec =
        wheelMetersPerSecond / Constants.WHEEL_RADIUS_METERS * Constants.GEAR_RATIO;
    double backEmf = motorRadPerSec / DRIVE_MOTOR.KvRadPerSecPerVolt;
    double window = CURRENT_LIMIT_AMPS * DRIVE_MOTOR.rOhms;
    return Math.max(backEmf - window, Math.min(backEmf + window, volts));
  }

  private static double minusStaticFriction(double volts) {
    return Math.copySign(Math.max(0.0, Math.abs(volts) - STATIC_FRICTION_VOLTS), volts);
  }

  private static void prepareSimMotor(TalonFX motor, boolean inverted) {
    var simState = motor.getSimState();
    simState.Orientation =
        inverted ? ChassisReference.Clockwise_Positive : ChassisReference.CounterClockwise_Positive;
    simState.setSupplyVoltage(12.0);
  }

  private static void pushSimState(
      CommonTalon[] motors, double motorRotations, double motorRotationsPerSecond) {
    for (CommonTalon motor : motors) {
      var simState = ((TalonFX) motor).getSimState();
      simState.setRawRotorPosition(motorRotations);
      simState.setRotorVelocity(motorRotationsPerSecond);
    }
  }

  private static boolean[] invertedFlags(List<MotorConfig> configs) {
    boolean[] flags = new boolean[configs.size()];
    for (int i = 0; i < flags.length; i++) {
      flags[i] =
          configs.get(i).getAsFXConfig().MotorOutput.Inverted == InvertedValue.Clockwise_Positive;
    }
    return flags;
  }
}
