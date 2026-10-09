package frc.robot.subsystems.drive;

import choreo.trajectory.DifferentialSample;
import choreo.trajectory.Trajectory;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.LTVUnicycleController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import java.util.List;

/**
 * Turns one sample of a Choreo path into left and right motor duty cycles. Provided for you and
 * already tuned: read it to see how a path becomes motor power, you do not need to change it.
 *
 * <p>MWLib has a follower for swerve robots, but none for a differential drive, so the lesson
 * brings its own. It works in three steps, every loop:
 *
 * <ol>
 *   <li><b>Where should we be, and how fast?</b> The path sample says: this pose, this left wheel
 *       speed, this right wheel speed.
 *   <li><b>Feedback.</b> The robot is never exactly where the path says. A {@link
 *       LTVUnicycleController} (a path-following controller built for differential drives) compares
 *       the robot's pose with the sample's pose and nudges the speeds so the robot drifts back onto
 *       the path.
 *   <li><b>Feedforward.</b> Wheel speeds (meters per second) are turned into motor power (duty
 *       cycle): a little to break the wheels loose (kS), plus power in proportion to speed (kV).
 * </ol>
 */
public class DifferentialPathFollower {
  private final DrivetrainConstants constants;
  private final DifferentialDriveKinematics kinematics;
  private final LTVUnicycleController controller;

  private static final double LOOP_SECONDS = 0.020; // the robot code runs every 20 ms

  public DifferentialPathFollower(
      DrivetrainConstants constants, DifferentialDriveKinematics kinematics) {
    this.constants = constants;
    this.kinematics = kinematics;
    this.controller =
        new LTVUnicycleController(LOOP_SECONDS, DrivetrainConstants.FREE_SPEED_METERS_PER_SECOND);
  }

  /**
   * Finds the part of the path for a moment in time, between the two saved samples around it.
   *
   * <p>Choreo's own {@code sampleAt} would do this too, but for a differential drive it blends the
   * samples using their saved accelerations, and those are not reliable in the paths Choreo
   * generates (the turning acceleration, alpha, can be off by a factor of 100). So this takes the
   * plain average of the two neighbouring samples instead, which is accurate enough for samples
   * that are only 0.05 seconds apart.
   *
   * @param seconds time since the path started; before the start or after the end gives the first
   *     or last sample
   */
  public static DifferentialSample sampleAt(Trajectory<DifferentialSample> path, double seconds) {
    List<DifferentialSample> samples = path.samples();
    DifferentialSample first = samples.get(0);
    DifferentialSample last = samples.get(samples.size() - 1);
    if (seconds <= first.t) {
      return first;
    }
    if (seconds >= last.t) {
      return last;
    }
    int i = 1;
    while (samples.get(i).t < seconds) {
      i++;
    }
    DifferentialSample a = samples.get(i - 1);
    DifferentialSample b = samples.get(i);
    double fraction = (seconds - a.t) / (b.t - a.t);
    return new DifferentialSample(
        seconds,
        MathUtil.interpolate(a.x, b.x, fraction),
        MathUtil.interpolate(a.y, b.y, fraction),
        a.heading + MathUtil.angleModulus(b.heading - a.heading) * fraction,
        MathUtil.interpolate(a.vl, b.vl, fraction),
        MathUtil.interpolate(a.vr, b.vr, fraction),
        MathUtil.interpolate(a.omega, b.omega, fraction),
        0.0,
        0.0,
        0.0,
        0.0,
        0.0);
  }

  /**
   * Works out the motor power for one path sample.
   *
   * @param robot where the robot thinks it is
   * @param sample where the path says it should be right now, and how fast its wheels should turn
   * @return {left duty cycle, right duty cycle}, each from -1.0 to 1.0
   */
  public double[] calculate(Pose2d robot, DifferentialSample sample) {
    // The speeds the path wants: forward speed is the average of the two wheels, and the turning
    // speed is how much faster the right wheel is than the left, spread across the track width.
    double pathForward = (sample.vl + sample.vr) / 2.0;
    double pathTurn = sample.omega;

    // Feedback: ask the controller for the speeds that follow the path AND pull back onto it.
    ChassisSpeeds corrected = controller.calculate(robot, sample.getPose(), pathForward, pathTurn);
    return toDutyCycles(corrected);
  }

  /**
   * Works out the motor power for the last bit of a path: the path's clock has run out, but the
   * robot may still be a little short of, or past, the end point. A differential drive cannot slide
   * sideways, so the path controller cannot fix that (it needs the path to be moving). Instead:
   * turn to face the end point, then drive at it (backwards, if that is the shorter turn).
   *
   * @param robot where the robot thinks it is
   * @param goal the end point of the path
   * @return {left duty cycle, right duty cycle}, each from -1.0 to 1.0
   */
  public double[] settle(Pose2d robot, Pose2d goal) {
    Translation2d toGoal = goal.getTranslation().minus(robot.getTranslation());
    double distance = toGoal.getNorm();
    if (distance < constants.CHOREO_SETTLE_DEADBAND_METERS) {
      return new double[] {0.0, 0.0};
    }
    // Angle between where the robot faces and where the goal is, folded into -90..90 degrees (if
    // the goal is behind the robot it simply drives backwards instead of spinning around).
    double bearing = toGoal.getAngle().minus(robot.getRotation()).getRadians();
    boolean backwards = Math.abs(bearing) > Math.PI / 2.0;
    if (backwards) {
      bearing = MathUtil.angleModulus(bearing + Math.PI);
    }
    double speed =
        Math.min(constants.CHOREO_SETTLE_MAX_SPEED, constants.CHOREO_SETTLE_KP_DISTANCE * distance);
    // Drive more slowly the less the robot faces the goal, and not at all if it is sideways.
    speed *= Math.max(0.0, Math.cos(bearing));
    double forward = backwards ? -speed : speed;
    double turn = constants.CHOREO_SETTLE_KP_HEADING * bearing;
    return toDutyCycles(new ChassisSpeeds(forward, 0.0, turn));
  }

  /** Turns the speeds the robot should have into motor power. */
  private double[] toDutyCycles(ChassisSpeeds speeds) {
    DifferentialDriveWheelSpeeds wheels = kinematics.toWheelSpeeds(speeds);
    // Feedforward: wheel speed -> duty cycle. Turning drags the wheels sideways, which takes extra
    // power on top of rolling. "spin" is how much faster than the middle of the robot the right
    // side is going (negative when the left side is).
    double spin = (wheels.rightMetersPerSecond - wheels.leftMetersPerSecond) / 2.0;
    double extra = constants.CHOREO_KV_TURN * spin;
    return new double[] {
      clamp(toDutyCycle(wheels.leftMetersPerSecond) - extra),
      clamp(toDutyCycle(wheels.rightMetersPerSecond) + extra)
    };
  }

  /**
   * The power it takes to hold a wheel at a speed: kS in the direction of travel plus kV * speed.
   */
  private double toDutyCycle(double wheelMetersPerSecond) {
    double kS = Math.abs(wheelMetersPerSecond) > 1e-3 ? constants.CHOREO_KS : 0.0;
    return Math.signum(wheelMetersPerSecond) * kS + constants.CHOREO_KV * wheelMetersPerSecond;
  }

  private static double clamp(double duty) {
    return Math.max(-1.0, Math.min(1.0, duty));
  }
}
