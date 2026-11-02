package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.mechanisms.DifferentialDriveMech;
import lesson.LessonLoop;
import org.littletonrobotics.junction.LoggedRobot;

public class Robot extends LoggedRobot {
  private final XboxController controller_ = new XboxController(0);
  private final DifferentialDriveMech drive_ =
      new DifferentialDriveMech(Constants.LEFT_MOTORS, Constants.RIGHT_MOTORS);
  private final LessonLoop loop_ = new LessonLoop(drive_);

  @Override
  public void robotPeriodic() {
    loop_.doControlLoop();
  }

  @Override
  public void disabledPeriodic() {
    drive_.setDutyCycles(0.0, 0.0);
  }

  @Override
  public void teleopPeriodic() {
    double forward = -controller_.getLeftY();
    double turn = controller_.getLeftX();

    // Gentle near the middle of the stick: square it, keeping its sign.
    forward = forward * Math.abs(forward);
    turn = turn * Math.abs(turn);

    double left_speed = forward + turn;
    double right_speed = forward - turn;

    // Past full power, shrink both sides together so the difference (the turn) survives.
    double biggest = Math.max(1.0, Math.abs(forward) + Math.abs(turn));
    drive_.setDutyCycles(left_speed / biggest, right_speed / biggest);
  }
}
