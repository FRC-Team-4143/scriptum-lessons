package lesson;

import com.marswars.logging.MwLog;
import com.marswars.proxy_server.Packet.Timestamp;
import com.marswars.proxy_server.TagSolutionPacket.TagSolutionData;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.subsystems.drive.DrivetrainSubsystem;
import frc.robot.subsystems.localization.LocalizationSubsystem;
import frc.robot.vision.VisionConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * Provided for you, no need to edit. It runs the "Knocked off course" challenge. One tap of Y
 * (keyboard I) with the robot enabled starts a test of about 15 seconds:
 *
 * <ol>
 *   <li>For 5 seconds a false camera sighting, far from the robot, turns up now and then. A good
 *       robot ignores it, so the estimate must stay within 0.3 meters of the truth.
 *   <li>Then the estimate is knocked 1.5 meters off, like a robot shoved by another robot. The
 *       false sightings keep coming. The estimate must get back within 0.2 meters in 5 seconds.
 *   <li>It must stay within 0.3 meters for 5 more seconds.
 * </ol>
 *
 * <p>It publishes under "Check/Challenge/": KnockedOff (1 once a whole test has passed), and
 * KnockedOffPhase (0 not started, 1 false sightings, 2 knocked off, 3 staying close, 4 passed, -1
 * failed; tap Y to try again).
 */
public final class KnockedOffCheck {
  private static final double LOOP_SECONDS = 0.02;
  private static final double WATCH_SECONDS = 5.0;
  private static final double RECOVER_SECONDS = 5.0;
  private static final double STAY_SECONDS = 5.0;
  private static final double KNOCK_METERS = 1.5;
  private static final double RECOVERED_METERS = 0.2;
  private static final double STAY_WITHIN_METERS = 0.3;
  private static final double FALSE_SIGHTING_METERS = 2.5;
  private static final double FALSE_SIGHTING_EVERY_SECONDS = 0.5;

  private static boolean false_sightings_on_ = false;
  private static double next_false_sighting_ = 0.0;
  private static double false_sighting_angle_ = 0.0;

  private final NetworkTableEntry error =
      NetworkTableInstance.getDefault()
          .getEntry("/AdvantageKit/RealOutputs/Subsystem/Localization/PositionErrorMeters");

  private boolean y_was_held_ = false;
  private int phase_ = 0;
  private double phase_seconds_ = 0.0;
  private boolean passed_ = false;

  /**
   * Adds this loop's false sighting, if one is due, to what the cameras really saw. It looks like
   * any other solution: a nearby AprilTag, the time now and a camera of its own.
   */
  public static List<TagSolutionData> withFalseSightings(List<TagSolutionData> real) {
    double now = Timer.getFPGATimestamp();
    if (!false_sightings_on_ || now < next_false_sighting_) {
      return real;
    }
    next_false_sighting_ = now + FALSE_SIGHTING_EVERY_SECONDS;
    false_sighting_angle_ += 2.4;
    Pose2d truth = DrivetrainSubsystem.getInstance().getTruePose();
    Translation2d offset =
        new Translation2d(FALSE_SIGHTING_METERS, new Rotation2d(false_sighting_angle_));
    Translation2d spot = truth.getTranslation().plus(offset);
    if (!isOnField(spot)) {
      spot = truth.getTranslation().minus(offset);
    }

    int nearest_id = 1;
    double nearest = Double.MAX_VALUE;
    for (AprilTag tag : VisionConstants.FIELD_LAYOUT.getTags()) {
      double distance = tag.pose.toPose2d().getTranslation().getDistance(spot);
      if (distance < nearest) {
        nearest = distance;
        nearest_id = tag.ID;
      }
    }
    ArrayList<Integer> ids = new ArrayList<>(List.of(nearest_id));
    int seconds = (int) now;
    Timestamp stamp = new Timestamp(seconds, (int) ((now - seconds) * 1e9));

    List<TagSolutionData> all = new ArrayList<>(real);
    all.add(new TagSolutionData(new Pose2d(spot, truth.getRotation()), ids, stamp, "false-sighting"));
    return all;
  }

  private static boolean isOnField(Translation2d spot) {
    return spot.getX() >= 0.0
        && spot.getX() <= VisionConstants.FIELD_LAYOUT.getFieldLength()
        && spot.getY() >= 0.0
        && spot.getY() <= VisionConstants.FIELD_LAYOUT.getFieldWidth();
  }

  private void enter(int phase) {
    phase_ = phase;
    phase_seconds_ = 0.0;
    false_sightings_on_ = phase == 1 || phase == 2 || phase == 3;
  }

  /** Call once per loop, after your code has logged its values. */
  public void update() {
    boolean y_held = DriverStation.getStickButton(0, XboxController.Button.kY.value);
    if (y_held && !y_was_held_ && DriverStation.isEnabled() && phase_ != 1 && phase_ != 2 && phase_ != 3) {
      enter(1);
    }
    y_was_held_ = y_held;

    double meters = error.getDouble(0.0);
    phase_seconds_ += LOOP_SECONDS;
    if (phase_ == 1) {
      if (meters > STAY_WITHIN_METERS) {
        enter(-1); // a false sighting moved the estimate
      } else if (phase_seconds_ >= WATCH_SECONDS) {
        LocalizationSubsystem.getInstance().knockEstimate(KNOCK_METERS);
        enter(2);
      }
    } else if (phase_ == 2) {
      // The first few loops still show the error from before the knock.
      if (phase_seconds_ > 0.1 && meters <= RECOVERED_METERS) {
        enter(3);
      } else if (phase_seconds_ >= RECOVER_SECONDS) {
        enter(-1);
      }
    } else if (phase_ == 3) {
      if (meters > STAY_WITHIN_METERS) {
        enter(-1);
      } else if (phase_seconds_ >= STAY_SECONDS) {
        passed_ = true;
        enter(4);
      }
    }
    MwLog.log("Check/Challenge/KnockedOff", passed_ ? 1.0 : 0.0);
    MwLog.log("Check/Challenge/KnockedOffPhase", phase_);
  }
}
