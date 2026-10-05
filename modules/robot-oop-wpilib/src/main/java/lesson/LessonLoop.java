package lesson;

import com.marswars.subsystem.MwSubsystemBase;
import com.marswars.subsystem.SubsystemIoBase;
import com.marswars.subsystem.SubsystemManager;
import frc.robot.BuildConstants;
import java.util.List;

/**
 * Provided for you, no need to edit. This is MWLib's loop. Every 20 ms it asks the drivetrain to
 * read its sensors, then write its motor outputs, then log its data. That is why your code only has
 * to say what you WANT the robot to do.
 */
public class LessonLoop extends SubsystemManager {
  private final LessonChecks checks =
      new LessonChecks(
          "Drive/LeftOutput",
          "Drive/RightOutput",
          "Drive/LinearSpeed",
          "Drive/AngularSpeed",
          "Drive/PoseX",
          "Drive/PoseYawDeg");

  public LessonLoop(SubsystemIoBase mech) {
    super(BuildConstants.class);
    registerSubsystem(
        new MwSubsystemBase() {
          @Override
          public List<SubsystemIoBase> getIos() {
            return List.of(mech);
          }

          @Override
          public String getName() {
            return "Drive";
          }

          @Override
          public String getSubsystemKey() {
            return "Subsystem/Drive/";
          }

          @Override
          public void update(double timestamp) {}

          @Override
          public void reset() {}
        });
  }

  @Override
  public void doControlLoop() {
    super.doControlLoop();
    checks.update();
  }
}
