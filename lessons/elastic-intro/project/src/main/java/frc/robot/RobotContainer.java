package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;
import org.littletonrobotics.junction.networktables.LoggedNetworkBoolean;
import org.littletonrobotics.junction.networktables.LoggedNetworkNumber;

/**
 * The robot's behavior lives here. {@link Robot} handles the AdvantageKit logging setup and calls
 * {@link #robotPeriodic()} every loop, so you can focus on the tool, not the code.
 *
 * <p>This starter logs one value for every Elastic single-topic widget type (see the README's
 * table), plus a robot pose that drives in a circle for the Field widget, all through
 * AdvantageKit's {@link Logger} - open Elastic (the "Elastic" tab above) to confirm live telemetry
 * works end to end, then configure Elastic to display them.
 *
 * <p>{@link #autoRoutine}, {@link #autoDelaySeconds}, {@link #shooterAngleDegrees}, {@link
 * #brakeModeEnabledButton}, and {@link #brakeModeEnabledSwitch} run the other direction: they're
 * written *from* Elastic, and the robot only reads them. Unlike AdvantageScope's Tuning Mode,
 * Elastic has no separate "armed" toggle to flip first - any widget bound to a plain writable topic
 * accepts input as soon as it's on the grid.
 */
public class RobotContainer {
  private final Timer timer = new Timer();
  private long counter = 0;

  // An operator input, chosen from Elastic's ComboBox Chooser or Split Button Chooser - the same
  // chooser, just displayed with two different widgets.
  private final LoggedDashboardChooser<String> autoRoutine =
      new LoggedDashboardChooser<>("AutoRoutine");

  // An operator input, written from Elastic's Text Display (turn on its Show Submit Button
  // setting so it publishes once on submit, not on every keystroke).
  private final LoggedNetworkNumber autoDelaySeconds =
      new LoggedNetworkNumber("/AutoDelaySeconds", 0.0);

  // An operator input, dragged in from Elastic's Number Slider - unlike every other angle/speed
  // value in this file, this one is not computed here.
  private final LoggedNetworkNumber shooterAngleDegrees =
      new LoggedNetworkNumber("/ShooterAngleDegrees", 0.0);

  // Two separate operator inputs, each written by its own widget - unlike every other boolean in
  // this file, neither is computed here. Separate keys (rather than one topic shared by both
  // widgets) so a checkpoint can tell the two widgets apart live over NT4.
  private final LoggedNetworkBoolean brakeModeEnabledButton =
      new LoggedNetworkBoolean("/BrakeModeEnabledButton", false);
  private final LoggedNetworkBoolean brakeModeEnabledSwitch =
      new LoggedNetworkBoolean("/BrakeModeEnabledSwitch", false);

  // Between 0.0 and 1.0, changing over time.
  public static double climberSpeed(double t) {
    return 0.5 + 0.5 * Math.sin(t);
  }

  // Toggles back and forth as t changes.
  public static boolean gamePieceLoaded(double t) {
    return ((int) t) % 2 == 0;
  }

  // Around 12 volts, drifting slightly like a real battery under load.
  public static double batteryVoltage(double t) {
    return 12.0 + 0.5 * Math.sin(t);
  }

  // 0-360 degrees, spinning continuously like a gyro heading.
  public static double gyroHeadingDegrees(double t) {
    return (t * 45.0) % 360.0;
  }

  // Between 0 and 40 amps, changing over time.
  public static double intakeCurrentAmps(double t) {
    return 20.0 + 20.0 * Math.sin(t * 2.0);
  }

  // Counts down from 150 seconds to 0, then repeats.
  public static double matchTimeRemaining(double t) {
    return 150.0 - (t % 150.0);
  }

  // A short status string that tracks gamePieceLoaded.
  public static String statusMessage(double t) {
    return gamePieceLoaded(t) ? "Game piece loaded" : "Game piece empty";
  }

  // A hex color that tracks gamePieceLoaded - green when loaded, red when not.
  // Single Color View expects a "#RRGGBB" string, not a Color object.
  public static String statusColorHex(double t) {
    return gamePieceLoaded(t) ? "#ebe712" : "#000000";
  }

  public RobotContainer() {
    autoRoutine.addDefaultOption("Do nothing", "Do nothing");
    autoRoutine.addOption("Leave only", "Leave only");
    autoRoutine.addOption("Score preload, then leave", "Score preload, then leave");
    timer.start();
  }

  /** Called every loop while the robot is running. Add your own logic here. */
  public void robotPeriodic() {
    // Run the command scheduler so subsystems and commands you add keep working.
    CommandScheduler.getInstance().run();

    double seconds = timer.get();

    // A counter that ticks up once per loop.
    counter++;
    Logger.recordOutput("Counter", counter);

    Logger.recordOutput("ClimberSpeed", climberSpeed(seconds));
    Logger.recordOutput("GamePieceLoaded", gamePieceLoaded(seconds));
    Logger.recordOutput("BatteryVoltage", batteryVoltage(seconds));
    Logger.recordOutput("GyroHeadingDegrees", gyroHeadingDegrees(seconds));
    Logger.recordOutput("IntakeCurrentAmps", intakeCurrentAmps(seconds));
    Logger.recordOutput("MatchTimeRemaining", matchTimeRemaining(seconds));
    Logger.recordOutput("StatusMessage", statusMessage(seconds));
    Logger.recordOutput("StatusColorHex", statusColorHex(seconds));

    // Multi Color View expects an array of "#RRGGBB" strings - a static LED
    // strip pattern here, but it could just as easily change over time.
    Logger.recordOutput("LedStripColorsHex", new String[] {"#FF0000", "#FFFFFF", "#0000FF"});

    // A pose that drives in a circle around the middle of the field. Named
    // "Field2d/Robot" - Elastic's Field widget looks for the robot pose at
    // "<topic>/Robot" under whatever root you point it at.
    double radius = 2.0;
    double omega = 1.0;
    double x = 4.0 + radius * Math.cos(omega * seconds);
    double y = 4.0 + radius * Math.sin(omega * seconds);
    Rotation2d heading = new Rotation2d(omega * seconds + Math.PI / 2);
    Logger.recordOutput("Field2d/Robot", new Pose2d(x, y, heading));

    // autoRoutine, autoDelaySeconds, shooterAngleDegrees,
    // brakeModeEnabledButton, and brakeModeEnabledSwitch need nothing here -
    // they're LoggedDashboardChooser/LoggedNetworkNumber/LoggedNetworkBoolean,
    // which read their live NT value automatically every loop.
  }
}
