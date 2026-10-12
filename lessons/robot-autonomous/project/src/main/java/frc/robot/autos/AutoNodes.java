package frc.robot.autos;

import com.marswars.auto.BehaviorTreeAuto;
import com.marswars.auto.ChoreoTrajectory;
import com.marswars.bt.RobotStateNodes;
import com.marswars.bt.core.BehaviorTreeFactory;
import com.marswars.bt.core.NodeKind;
import com.marswars.bt.core.NodeStatus;
import com.marswars.bt.core.PortInfo;
import com.marswars.bt.core.StatefulActionNode;
import frc.robot.RobotConstants.RobotStates;
import frc.robot.RobotContainer;
import java.util.List;

/**
 * The robot's own behavior-tree nodes: the actions your trees in {@code deploy/autos/trees} can
 * use, on top of the nodes every tree has (Sequence, Fallback, Timeout, Sleep and the rest).
 * Provided for you; deploy/autos/nodes.xml lists the same nodes for Arbor.
 */
public final class AutoNodes {
  /** How long a path may run past its own length before FollowPath gives up waiting. */
  private static final double PATH_EXTRA_SECONDS = 3.0;

  private AutoNodes() {}

  /** A factory that knows every node an autonomous tree can use. */
  public static BehaviorTreeFactory createFactory() {
    BehaviorTreeFactory factory = new BehaviorTreeFactory();

    // SetWantedState: ask the robot for a state (the same states the buttons ask for). Instant,
    // like a button press: it succeeds at once. IsSystemState: a condition, true while the robot is
    // in a state. Retry it (RetryUntilSuccessful) to wait until the robot gets there.
    RobotStateNodes.register(factory, RobotContainer::getInstance, RobotStates.class);

    // FollowPath: drive one of your Choreo paths, and succeed once the robot has arrived.
    factory.registerNodeType(
        "FollowPath",
        NodeKind.ACTION,
        "Drive a Choreo path until the end of it is reached",
        List.of(
            PortInfo.trajectory("trajectory", "Name of a Choreo path (deploy/choreo/<name>.traj)")),
        (name, config) ->
            new StatefulActionNode(name, config) {
              private double give_up_time_;

              @Override
              protected NodeStatus onStart() {
                // The running autonomous loaded every path its tree names before it started.
                ChoreoTrajectory path =
                    BehaviorTreeAuto.trajectories(this).apply(getString("trajectory"));
                give_up_time_ =
                    now() + path.getDifferentialTrajectory().getTotalTime() + PATH_EXTRA_SECONDS;
                RobotContainer.getInstance().followPath(path);
                return NodeStatus.RUNNING;
              }

              @Override
              protected NodeStatus onRunning() {
                RobotContainer robot = RobotContainer.getInstance();
                // Arrived, or stuck for too long: either way the next step can start.
                if (robot.isPathDone() || now() > give_up_time_) {
                  robot.endPath();
                  return NodeStatus.SUCCESS;
                }
                return NodeStatus.RUNNING;
              }

              @Override
              protected void onHalted() {
                RobotContainer.getInstance().endPath();
              }
            });

    return factory;
  }
}
