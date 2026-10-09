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
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import frc.robot.Constants;
import frc.robot.DriveMath;
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
    leftRequest.Output = clamp(left);
    rightRequest.Output = clamp(right);
  }

  /** How far the left wheels have turned, in wheel rotations. Forward is positive. */
  public double getLeftPositionRotations() {
    return inputs.leftPositionRotations;
  }

  /** How far the right wheels have turned, in wheel rotations. Forward is positive. */
  public double getRightPositionRotations() {
    return inputs.rightPositionRotations;
  }

  /** How fast the left wheels are turning, in wheel rotations per second. */
  public double getLeftVelocityRps() {
    return inputs.leftVelocityRps;
  }

  /** How fast the right wheels are turning, in wheel rotations per second. */
  public double getRightVelocityRps() {
    return inputs.rightVelocityRps;
  }

  /**
   * Sets both wheel positions back to zero, so "how far have I driven" starts counting again from
   * here. (In simulation it also puts the simulated robot back at the start.)
   */
  public void resetEncoders() {
    if (IS_SIM) {
      // The simulation feeds the motors their position every loop, so it has to be the one that
      // starts over. Telling the motors "you are at zero" with setPosition() would be undone on the
      // next loop, when the simulation tells them where the wheels really are.
      resetSimulation();
    } else {
      leftMotors[0].setPosition(0.0);
      rightMotors[0].setPosition(0.0);
    }
    // Let the very next reading say zero too, instead of the old number from the last loop.
    inputs.leftPositionRotations = 0.0;
    inputs.rightPositionRotations = 0.0;
    inputs.leftVelocityRps = 0.0;
    inputs.rightVelocityRps = 0.0;
  }

  // ---------------------------------------------------------------------------------------------
  // Units the rest of the robot code actually wants: meters, and meters per second. They use the
  // DriveMath methods you wrote.
  // ---------------------------------------------------------------------------------------------

  /** How far the left side has driven, in meters. */
  public double getLeftMeters() {
    return DriveMath.rotationsToMeters(inputs.leftPositionRotations, Constants.WHEEL_RADIUS_METERS);
  }

  /** How far the right side has driven, in meters. */
  public double getRightMeters() {
    return DriveMath.rotationsToMeters(
        inputs.rightPositionRotations, Constants.WHEEL_RADIUS_METERS);
  }

  /** How fast the left side is moving, in meters per second. */
  public double getLeftMetersPerSecond() {
    return DriveMath.rotationsToMeters(inputs.leftVelocityRps, Constants.WHEEL_RADIUS_METERS);
  }

  /** How fast the right side is moving, in meters per second. */
  public double getRightMetersPerSecond() {
    return DriveMath.rotationsToMeters(inputs.rightVelocityRps, Constants.WHEEL_RADIUS_METERS);
  }

  /** How far the whole robot has driven, in meters: the average of the two sides. */
  public double getDistanceMeters() {
    return (getLeftMeters() + getRightMeters()) / 2.0;
  }

  /** The robot's forward speed, in meters per second. */
  public double getLinearSpeed() {
    return DriveMath.linearSpeed(getLeftMetersPerSecond(), getRightMetersPerSecond());
  }

  /** The robot's turning speed, in radians per second. Positive is turning left. */
  public double getAngularSpeed() {
    return DriveMath.angularSpeed(
        getLeftMetersPerSecond(), getRightMetersPerSecond(), Constants.TRACK_WIDTH_METERS);
  }

  /** Sets how hard ONLY the left side pushes, from -1.0 to 1.0. */
  public void setLeftDutyCycle(double left) {
    leftRequest.Output = clamp(left);
  }

  /** Sets how hard ONLY the right side pushes, from -1.0 to 1.0. */
  public void setRightDutyCycle(double right) {
    rightRequest.Output = clamp(right);
  }

  /**
   * Arcade drive: one number for forward/backward and one for turning.
   *
   * @param forward -1.0 (backward) to 1.0 (forward)
   * @param turn -1.0 (turn left) to 1.0 (turn right)
   */
  public void arcadeDrive(double forward, double turn) {
    double[] speeds = DriveMath.arcadeToWheelSpeeds(forward, turn);
    setDutyCycles(speeds[0], speeds[1]);
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

    double leftVolts = ((TalonFX) leftMotors[0]).getSimState().getMotorVoltage();
    double rightVolts = ((TalonFX) rightMotors[0]).getSimState().getMotorVoltage();

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
  private void resetSimulation() {
    sim.setPose(new Pose2d());
    sim.setInputs(0.0, 0.0);
    // Start counting from wherever the simulation says the wheels are now (zero in most versions of
    // WPILib, but this does not depend on it).
    lastTrueLeftMeters = sim.getLeftPositionMeters();
    lastTrueRightMeters = sim.getRightPositionMeters();
    measuredLeftMeters = 0.0;
    measuredRightMeters = 0.0;
    // Tell the simulated motors right away that the wheels are back at zero. Their position comes
    // from what the simulation pushes, so this is what actually resets the reading.
    pushSimState(leftMotors, 0.0, 0.0);
    pushSimState(rightMotors, 0.0, 0.0);
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
