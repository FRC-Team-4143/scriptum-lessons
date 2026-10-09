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
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import frc.robot.DriveMath;
import frc.robot.FieldTargets;
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
  private final CommonTalon[] left_motors_;
  private final CommonTalon[] right_motors_;
  private final BaseStatusSignal[] signals_;
  private final boolean[] left_inverted_;
  private final boolean[] right_inverted_;

  private final DutyCycleOut left_request_ = new DutyCycleOut(0);
  private final DutyCycleOut right_request_ = new DutyCycleOut(0);
  private final DriveInputsAutoLogged inputs_ = new DriveInputsAutoLogged();

  private final DifferentialDrivetrainSim sim_;

  // Simulated sensor imperfections (simulation only). The seed is fixed so every student sees the
  // same robot.
  private static final double LEFT_SCALE = 1.004; // left wheels read 0.4% too far
  private static final double RIGHT_SCALE = 0.997; // right wheels read 0.3% too short
  private static final double SLIP_NOISE = 0.02; // random slip, as a fraction of each movement
  private static final double JITTER_METERS = 0.0005; // encoder position jitter
  private static final double JITTER_METERS_PER_SECOND = 0.01; // encoder velocity jitter
  private final Random noise_ = new Random(4143);

  // Simulated friction (simulation only). See updateSimulation().
  private static final double STATIC_FRICTION_VOLTS = 0.25; // volts it takes to get a wheel moving
  private static final double SCRUB_VOLTS_PER_METER_PER_SECOND = 4.5; // how hard turning drags
  private static final double SUPPLY_VOLTS = 12.0; // battery voltage
  private static final double CURRENT_LIMIT_AMPS = 120.0; // per motor, like the TalonFX default
  private static final DCMotor DRIVE_MOTOR = DCMotor.getKrakenX60(1);
  private double last_true_left_meters_ = 0.0;
  private double last_true_right_meters_ = 0.0;
  private double measured_left_meters_ = 0.0;
  private double measured_right_meters_ = 0.0;
  // True for one loop after a reset. The motors can still report their OLD position on the very
  // next read, which would make the robot look like it jumped; that one reading is ignored.
  private boolean just_reset_ = false;

  // Kinematics turns wheel speeds into robot speeds. The pose estimator that adds up the movements
  // lives in the LocalizationSubsystem, which asks this mech for the wheel readings.
  private final DifferentialDriveKinematics kinematics_ =
      new DifferentialDriveKinematics(DrivetrainConstants.TRACK_WIDTH_METERS);

  public DifferentialDriveMech(List<MotorConfig> left_configs, List<MotorConfig> right_configs) {
    super("", "Drive");

    // MWLib builds the motors: the first one in each list is the leader, the rest follow it.
    ConstructedMotors left = configMotors(left_configs, DrivetrainConstants.GEAR_RATIO);
    ConstructedMotors right = configMotors(right_configs, DrivetrainConstants.GEAR_RATIO);
    left_motors_ = left.motors;
    right_motors_ = right.motors;

    signals_ = new BaseStatusSignal[left.signals.length + right.signals.length];
    System.arraycopy(left.signals, 0, signals_, 0, left.signals.length);
    System.arraycopy(right.signals, 0, signals_, left.signals.length, right.signals.length);

    left_inverted_ = invertedFlags(left_configs);
    right_inverted_ = invertedFlags(right_configs);

    // Physics model, used only in simulation.
    int motors_per_side = Math.max(left_configs.size(), right_configs.size());
    sim_ =
        new DifferentialDrivetrainSim(
            DCMotor.getKrakenX60(motors_per_side),
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
    left_request_.Output = clamp(left);
    right_request_.Output = clamp(right);
  }

  /** How far the left wheels have turned, in wheel rotations. Forward is positive. */
  public double getLeftPositionRotations() {
    return inputs_.leftPositionRotations;
  }

  /** How far the right wheels have turned, in wheel rotations. Forward is positive. */
  public double getRightPositionRotations() {
    return inputs_.rightPositionRotations;
  }

  /** How fast the left wheels are turning, in wheel rotations per second. */
  public double getLeftVelocityRps() {
    return inputs_.leftVelocityRps;
  }

  /** How fast the right wheels are turning, in wheel rotations per second. */
  public double getRightVelocityRps() {
    return inputs_.rightVelocityRps;
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
      left_motors_[0].setPosition(0.0);
      right_motors_[0].setPosition(0.0);
    }
    // Let the very next reading say zero too, instead of the old number from the last loop.
    inputs_.leftPositionRotations = 0.0;
    inputs_.rightPositionRotations = 0.0;
    inputs_.leftVelocityRps = 0.0;
    inputs_.rightVelocityRps = 0.0;
    just_reset_ = true;
  }

  // ---------------------------------------------------------------------------------------------
  // Units the rest of the robot code actually wants: meters, and meters per second. They use the
  // DriveMath methods you wrote.
  // ---------------------------------------------------------------------------------------------

  /** How far the left side has driven, in meters. */
  public double getLeftMeters() {
    return DriveMath.rotationsToMeters(
        inputs_.leftPositionRotations, DrivetrainConstants.WHEEL_RADIUS_METERS);
  }

  /** How far the right side has driven, in meters. */
  public double getRightMeters() {
    return DriveMath.rotationsToMeters(
        inputs_.rightPositionRotations, DrivetrainConstants.WHEEL_RADIUS_METERS);
  }

  /** How fast the left side is moving, in meters per second. */
  public double getLeftMetersPerSecond() {
    return DriveMath.rotationsToMeters(
        inputs_.leftVelocityRps, DrivetrainConstants.WHEEL_RADIUS_METERS);
  }

  /** How fast the right side is moving, in meters per second. */
  public double getRightMetersPerSecond() {
    return DriveMath.rotationsToMeters(
        inputs_.rightVelocityRps, DrivetrainConstants.WHEEL_RADIUS_METERS);
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
  // Wheel readings for localization. Kinematics turns wheel speeds into robot speeds; the
  // LocalizationSubsystem adds up the small movements to track x, y and heading.
  // ---------------------------------------------------------------------------------------------

  /** The robot's speeds as a ChassisSpeeds: forward (vx) and turning (omega). */
  public ChassisSpeeds getChassisSpeeds() {
    return kinematics_.toChassisSpeeds(
        new DifferentialDriveWheelSpeeds(getLeftMetersPerSecond(), getRightMetersPerSecond()));
  }

  /** Which way the robot faces, worked out from how far each side has driven. */
  public Rotation2d getYaw() {
    return new Rotation2d(
        (getRightMeters() - getLeftMeters()) / DrivetrainConstants.TRACK_WIDTH_METERS);
  }

  /** The kinematics object for this chassis (it knows the track width). */
  public DifferentialDriveKinematics getKinematics() {
    return kinematics_;
  }

  /**
   * Puts the robot at a pose on the field with fresh encoders. In simulation the simulated robot is
   * moved there and the wheel readings start counting from zero. The pose ESTIMATE is not kept
   * here: the DrivetrainSubsystem tells the LocalizationSubsystem to restart from the same pose.
   *
   * @param new_pose where the robot is now, in field coordinates
   */
  public void resetPose(Pose2d new_pose) {
    if (IS_SIM) {
      // The simulation owns the wheel positions (see resetEncoders), so it is the one that moves.
      resetSimulation(new_pose);
    } else {
      left_motors_[0].setPosition(0.0);
      right_motors_[0].setPosition(0.0);
    }
    inputs_.leftPositionRotations = 0.0;
    inputs_.rightPositionRotations = 0.0;
    inputs_.leftVelocityRps = 0.0;
    inputs_.rightVelocityRps = 0.0;
    just_reset_ = true;
  }

  /**
   * Where the robot REALLY is. Only the simulation knows this; on a real robot nothing does, so it
   * returns the origin. Used by the simulated cameras and by the lesson's checks.
   */
  public Pose2d getTruePose() {
    return IS_SIM ? sim_.getPose() : new Pose2d();
  }

  /** Sets how hard ONLY the left side pushes, from -1.0 to 1.0. */
  public void setLeftDutyCycle(double left) {
    left_request_.Output = clamp(left);
  }

  /** Sets how hard ONLY the right side pushes, from -1.0 to 1.0. */
  public void setRightDutyCycle(double right) {
    right_request_.Output = clamp(right);
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
      BaseStatusSignal.refreshAll(signals_);
      if (just_reset_) {
        just_reset_ = false;
      } else {
        inputs_.leftPositionRotations = left_motors_[0].getPosition().getValueAsDouble();
        inputs_.rightPositionRotations = right_motors_[0].getPosition().getValueAsDouble();
        inputs_.leftVelocityRps = left_motors_[0].getVelocity().getValueAsDouble();
        inputs_.rightVelocityRps = right_motors_[0].getVelocity().getValueAsDouble();
      }

      if (IS_SIM) {
        updateSimulation();
      }
    }
    Logger.processInputs(getLoggingKey() + "Inputs", inputs_);
  }

  @Override
  public void writeOutputs(double timestamp) {
    left_motors_[0].setControl(left_request_);
    right_motors_[0].setControl(right_request_);
  }

  @Override
  public void logData() {
    MwLog.log(getLoggingKey() + "LeftOutput", left_request_.Output);
    MwLog.log(getLoggingKey() + "RightOutput", right_request_.Output);
    ChassisSpeeds speeds = getChassisSpeeds();
    MwLog.log(getLoggingKey() + "ChassisSpeeds", speeds);
    MwLog.log(getLoggingKey() + "LinearSpeed", speeds.vxMetersPerSecond);
    MwLog.log(getLoggingKey() + "AngularSpeed", speeds.omegaRadiansPerSecond);
    // The same two numbers worked out by hand with your DriveMath methods, to compare.
    MwLog.log(getLoggingKey() + "LinearSpeedByHand", getLinearSpeed());
    MwLog.log(getLoggingKey() + "AngularSpeedByHand", getAngularSpeed());
    if (IS_SIM) {
      // Where the simulated robot REALLY is, to compare with the pose your code estimates.
      MwLog.log(getLoggingKey() + "TruePose", sim_.getPose());
      // How far the robot REALLY is from the two field targets. The lesson's checks read these.
      MwLog.log(
          getLoggingKey() + "DistanceToPickup",
          sim_.getPose().getTranslation().getDistance(FieldTargets.PICKUP.getTranslation()));
      MwLog.log(
          getLoggingKey() + "DistanceToScoreSpot",
          sim_.getPose().getTranslation().getDistance(FieldTargets.SCORE_SPOT.getTranslation()));
      // How far the simulated robot REALLY is from facing the goal (the aim checks use this, so a
      // drifting pose estimate cannot fool them). Positive: the goal is to the left.
      Pose2d truth = sim_.getPose();
      Rotation2d to_goal =
          FieldTargets.GOAL.getTranslation().minus(truth.getTranslation()).getAngle();
      MwLog.log(
          getLoggingKey() + "TrueAimErrorDegrees", to_goal.minus(truth.getRotation()).getDegrees());
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
    for (int i = 0; i < left_motors_.length; i++) {
      prepareSimMotor((TalonFX) left_motors_[i], left_inverted_[i]);
    }
    for (int i = 0; i < right_motors_.length; i++) {
      prepareSimMotor((TalonFX) right_motors_[i], right_inverted_[i]);
    }

    // The motors are driven straight from the duty cycles rather than from the simulated TalonFX's
    // own voltage. The TalonFX only knows about its own motor, not the carpet dragging on the
    // wheels, so it believes a turning robot is stalled and cuts its output (to about 4 V). The
    // current limit is applied below instead, to the current the physics model really sees.
    double supply_volts = DriverStation.isEnabled() ? SUPPLY_VOLTS : 0.0;
    double left_volts = left_request_.Output * supply_volts;
    double right_volts = right_request_.Output * supply_volts;

    // Friction, which the physics model leaves out. The first few volts on each side only break
    // the wheels loose, so a stick that rests a hair off center does not creep. And turning drags
    // the wheels sideways across the carpet ("scrub"), so the faster the robot spins the harder
    // the carpet pushes back. Without it the robot spins about 3 times faster than a real one.
    left_volts = minusStaticFriction(left_volts);
    right_volts = minusStaticFriction(right_volts);
    double spin_meters_per_second =
        (sim_.getRightVelocityMetersPerSecond() - sim_.getLeftVelocityMetersPerSecond()) / 2.0;
    left_volts += SCRUB_VOLTS_PER_METER_PER_SECOND * spin_meters_per_second;
    right_volts -= SCRUB_VOLTS_PER_METER_PER_SECOND * spin_meters_per_second;
    left_volts = limitCurrent(left_volts, sim_.getLeftVelocityMetersPerSecond());
    right_volts = limitCurrent(right_volts, sim_.getRightVelocityMetersPerSecond());

    sim_.setInputs(left_volts, right_volts);
    sim_.update(0.020);

    // Real encoders are not perfect, so the simulated ones are not either. Each wheel reads a tiny
    // bit
    // too far or too short (wheels are never exactly the size we think), it slips a little as it
    // rolls, and the reading jitters. That is why the pose your code works out differs a little
    // from where the simulated robot really is (see Drive/TruePose).
    double left_delta = sim_.getLeftPositionMeters() - last_true_left_meters_;
    double right_delta = sim_.getRightPositionMeters() - last_true_right_meters_;
    last_true_left_meters_ = sim_.getLeftPositionMeters();
    last_true_right_meters_ = sim_.getRightPositionMeters();
    measured_left_meters_ += left_delta * LEFT_SCALE * (1.0 + SLIP_NOISE * noise_.nextGaussian());
    measured_right_meters_ +=
        right_delta * RIGHT_SCALE * (1.0 + SLIP_NOISE * noise_.nextGaussian());

    double left_meters = measured_left_meters_ + JITTER_METERS * noise_.nextGaussian();
    double right_meters = measured_right_meters_ + JITTER_METERS * noise_.nextGaussian();
    double left_speed =
        sim_.getLeftVelocityMetersPerSecond() * LEFT_SCALE
            + JITTER_METERS_PER_SECOND * noise_.nextGaussian();
    double right_speed =
        sim_.getRightVelocityMetersPerSecond() * RIGHT_SCALE
            + JITTER_METERS_PER_SECOND * noise_.nextGaussian();

    double wheel_circumference = 2.0 * Math.PI * DrivetrainConstants.WHEEL_RADIUS_METERS;
    pushSimState(
        left_motors_,
        left_meters / wheel_circumference * DrivetrainConstants.GEAR_RATIO,
        left_speed / wheel_circumference * DrivetrainConstants.GEAR_RATIO);
    pushSimState(
        right_motors_,
        right_meters / wheel_circumference * DrivetrainConstants.GEAR_RATIO,
        right_speed / wheel_circumference * DrivetrainConstants.GEAR_RATIO);
  }

  /** Puts the simulated robot back at the origin with fresh (zeroed) encoders. */
  private void resetSimulation() {
    resetSimulation(new Pose2d());
  }

  /** Puts the simulated robot at a pose, standing still, with fresh (zeroed) encoders. */
  private void resetSimulation(Pose2d where) {
    sim_.setPose(where);
    sim_.setInputs(0.0, 0.0);
    // Start counting from wherever the simulation says the wheels are now (zero in most versions of
    // WPILib, but this does not depend on it).
    last_true_left_meters_ = sim_.getLeftPositionMeters();
    last_true_right_meters_ = sim_.getRightPositionMeters();
    measured_left_meters_ = 0.0;
    measured_right_meters_ = 0.0;
    // Tell the simulated motors right away that the wheels are back at zero. Their position comes
    // from what the simulation pushes, so this is what actually resets the reading.
    pushSimState(left_motors_, 0.0, 0.0);
    pushSimState(right_motors_, 0.0, 0.0);
  }

  /** Keeps the current through one motor under the limit, like the TalonFX's stator limit. */
  private static double limitCurrent(double volts, double wheel_meters_per_second) {
    double motor_rad_per_sec =
        wheel_meters_per_second
            / DrivetrainConstants.WHEEL_RADIUS_METERS
            * DrivetrainConstants.GEAR_RATIO;
    double back_emf = motor_rad_per_sec / DRIVE_MOTOR.KvRadPerSecPerVolt;
    double window = CURRENT_LIMIT_AMPS * DRIVE_MOTOR.rOhms;
    return Math.max(back_emf - window, Math.min(back_emf + window, volts));
  }

  private static double minusStaticFriction(double volts) {
    return Math.copySign(Math.max(0.0, Math.abs(volts) - STATIC_FRICTION_VOLTS), volts);
  }

  private static void prepareSimMotor(TalonFX motor, boolean inverted) {
    var sim_state = motor.getSimState();
    sim_state.Orientation =
        inverted ? ChassisReference.Clockwise_Positive : ChassisReference.CounterClockwise_Positive;
    sim_state.setSupplyVoltage(12.0);
  }

  private static void pushSimState(
      CommonTalon[] motors, double motor_rotations, double motor_rotations_per_second) {
    for (CommonTalon motor : motors) {
      var sim_state = ((TalonFX) motor).getSimState();
      sim_state.setRawRotorPosition(motor_rotations);
      sim_state.setRotorVelocity(motor_rotations_per_second);
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
