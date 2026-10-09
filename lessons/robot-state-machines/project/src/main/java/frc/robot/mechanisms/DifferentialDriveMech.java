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
import edu.wpi.first.math.estimator.DifferentialDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import frc.robot.DriveMath;
import frc.robot.subsystems.drive.DrivetrainConstants;
import java.util.List;
import java.util.Random;
import org.littletonrobotics.junction.Logger;

/**
 * The robot's drivetrain. It has a left side and a right side, and each side can have several
 * motors. You tell it how hard each side should push and it takes care of the motors, the sensors,
 * and (in simulation) the physics.
 *
 * <p>MWLib calls {@link #readInputs}, {@link #writeOutputs} and {@link #logData} for you every loop
 * so you never have to call them yourself.
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
  private static final double SUPPLY_VOLTS = 12.0; // battery voltage
  private static final double CURRENT_LIMIT_AMPS = 120.0; // per motor, like the TalonFX default
  private static final DCMotor DRIVE_MOTOR = DCMotor.getKrakenX60(1);
  private double lastTrueLeftMeters = 0.0;
  private double lastTrueRightMeters = 0.0;
  private double measuredLeftMeters = 0.0;
  private double measuredRightMeters = 0.0;

  // Kinematics and the pose estimator live in the mech, just like on the competition robot.
  private final DifferentialDriveKinematics kinematics =
      new DifferentialDriveKinematics(DrivetrainConstants.TRACK_WIDTH_METERS);
  private final DifferentialDrivePoseEstimator poseEstimator =
      new DifferentialDrivePoseEstimator(kinematics, new Rotation2d(), 0.0, 0.0, new Pose2d());
  private Pose2d pose = new Pose2d();

  public DifferentialDriveMech(List<MotorConfig> leftConfigs, List<MotorConfig> rightConfigs) {
    super("", "Drive");

    // MWLib builds the motors: the first one in each list is the leader, the rest follow it.
    ConstructedMotors left = configMotors(leftConfigs, DrivetrainConstants.GEAR_RATIO);
    ConstructedMotors right = configMotors(rightConfigs, DrivetrainConstants.GEAR_RATIO);
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
            DrivetrainConstants.GEAR_RATIO,
            7.5, // how hard the robot is to spin (kg*m^2)
            DrivetrainConstants.ROBOT_MASS_KG,
            DrivetrainConstants.WHEEL_RADIUS_METERS,
            DrivetrainConstants.TRACK_WIDTH_METERS,
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
    return DriveMath.rotationsToMeters(
        inputs.leftPositionRotations, DrivetrainConstants.WHEEL_RADIUS_METERS);
  }

  /** How far the right side has driven, in meters. */
  public double getRightMeters() {
    return DriveMath.rotationsToMeters(
        inputs.rightPositionRotations, DrivetrainConstants.WHEEL_RADIUS_METERS);
  }

  /** How fast the left side is moving, in meters per second. */
  public double getLeftMetersPerSecond() {
    return DriveMath.rotationsToMeters(
        inputs.leftVelocityRps, DrivetrainConstants.WHEEL_RADIUS_METERS);
  }

  /** How fast the right side is moving, in meters per second. */
  public double getRightMetersPerSecond() {
    return DriveMath.rotationsToMeters(
        inputs.rightVelocityRps, DrivetrainConstants.WHEEL_RADIUS_METERS);
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
        getLeftMetersPerSecond(),
        getRightMetersPerSecond(),
        DrivetrainConstants.TRACK_WIDTH_METERS);
  }

  // ---------------------------------------------------------------------------------------------
  // Where is the robot? Kinematics turns wheel speeds into robot speeds, and the pose estimator
  // adds up small movements to track x, y and heading.
  // ---------------------------------------------------------------------------------------------

  /** The robot's speeds as a ChassisSpeeds: forward (vx) and turning (omega). */
  public ChassisSpeeds getChassisSpeeds() {
    return kinematics.toChassisSpeeds(
        new DifferentialDriveWheelSpeeds(getLeftMetersPerSecond(), getRightMetersPerSecond()));
  }

  /** Which way the robot faces, worked out from how far each side has driven. */
  public Rotation2d getYaw() {
    return new Rotation2d(
        (getRightMeters() - getLeftMeters()) / DrivetrainConstants.TRACK_WIDTH_METERS);
  }

  /** Feeds the latest wheel readings to the pose estimator. Runs every loop. */
  private void updatePose() {
    poseEstimator.update(getYaw(), getLeftMeters(), getRightMeters());
    pose = poseEstimator.getEstimatedPosition();
  }

  /** Where the robot thinks it is on the field. */
  public Pose2d getPose() {
    return pose;
  }

  /** The kinematics object for this chassis (it knows the track width). */
  public DifferentialDriveKinematics getKinematics() {
    return kinematics;
  }

  /** Puts the robot back at the origin, facing forward, with fresh encoders. */
  public void resetPose() {
    resetEncoders();
    poseEstimator.resetPosition(new Rotation2d(), 0.0, 0.0, new Pose2d());
    pose = new Pose2d();
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
    updatePose();
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
    MwLog.log(getLoggingKey() + "Pose", pose);
    MwLog.log(getLoggingKey() + "PoseX", pose.getX());
    MwLog.log(getLoggingKey() + "PoseY", pose.getY());
    MwLog.log(getLoggingKey() + "PoseYawDeg", pose.getRotation().getDegrees());
    ChassisSpeeds speeds = getChassisSpeeds();
    MwLog.log(getLoggingKey() + "ChassisSpeeds", speeds);
    MwLog.log(getLoggingKey() + "LinearSpeed", speeds.vxMetersPerSecond);
    MwLog.log(getLoggingKey() + "AngularSpeed", speeds.omegaRadiansPerSecond);
    // The same two numbers worked out by hand with your DriveMath methods, to compare.
    MwLog.log(getLoggingKey() + "LinearSpeedByHand", getLinearSpeed());
    MwLog.log(getLoggingKey() + "AngularSpeedByHand", getAngularSpeed());
    if (IS_SIM) {
      // Where the simulated robot REALLY is, to compare with the pose your code estimates.
      MwLog.log(getLoggingKey() + "TruePose", sim.getPose());
      // How far the simulated robot REALLY is from facing the goal (the aim lesson's checks use
      // this, so a drifting pose estimate cannot fool them). Positive: the goal is to the left.
      Pose2d truth = sim.getPose();
      Rotation2d toGoal =
          DrivetrainConstants.GOAL.getTranslation().minus(truth.getTranslation()).getAngle();
      MwLog.log(
          getLoggingKey() + "TrueAimErrorDegrees", toGoal.minus(truth.getRotation()).getDegrees());
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

    double wheelCircumference = 2.0 * Math.PI * DrivetrainConstants.WHEEL_RADIUS_METERS;
    pushSimState(
        leftMotors,
        leftMeters / wheelCircumference * DrivetrainConstants.GEAR_RATIO,
        leftSpeed / wheelCircumference * DrivetrainConstants.GEAR_RATIO);
    pushSimState(
        rightMotors,
        rightMeters / wheelCircumference * DrivetrainConstants.GEAR_RATIO,
        rightSpeed / wheelCircumference * DrivetrainConstants.GEAR_RATIO);
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

  /** Keeps the current through one motor under the limit, like the TalonFX's stator limit. */
  private static double limitCurrent(double volts, double wheelMetersPerSecond) {
    double motorRadPerSec =
        wheelMetersPerSecond
            / DrivetrainConstants.WHEEL_RADIUS_METERS
            * DrivetrainConstants.GEAR_RATIO;
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
