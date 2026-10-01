# Robot Starter

A minimal WPILib command-based robot wired up with [AdvantageKit](https://docs.advantagekit.org)
logging. This is your starting point for writing robot code that runs in
simulation.

## Running it

Click the **Run** button in the Scriptum Driver Station to build and start the
robot in simulation. Telemetry shows up in AdvantageScope and the Driver Station
panel as the robot runs.

## Where the code lives

The robot's behavior is defined in `src/main/java/frc/robot/`:

- `RobotContainer.java` — **write your code here.** This is where you wire up
  subsystems, commands, and controller bindings. Its `robotPeriodic()` method
  runs every loop.
- `Robot.java` — sets up AdvantageKit logging and calls
  `RobotContainer.robotPeriodic()`. You shouldn't need to edit it.
- `Constants.java` — selects the AdvantageKit runtime mode (sim vs. replay).

The starter logs a counter that increments every loop and a robot pose that
drives in a circle, both through AdvantageKit's `Logger.recordOutput(...)`, so
you can confirm that edit → run → telemetry is working end to end. Edit
`RobotContainer.java`, click Run, and watch your changes appear in AdvantageScope.

## What you need to do

There's no checkpoint here - this lesson is about getting comfortable with
the edit, run, observe loop you'll use for all robot code.

1. **Run the unmodified starter.** Click **Run** in the Driver Station,
   enable the robot, and find the counter and the circling robot pose in
   AdvantageScope. That's your proof the toolchain works.
2. **Read `RobotContainer.java`.** Find where the counter and pose are
   computed and logged each loop.
3. **Change something and watch it land.** Make the robot drive a bigger or
   faster circle, or log a new value of your own with
   `Logger.recordOutput("MyValue", ...)`. Click Run again and confirm the
   change in AdvantageScope.
4. **Break it on purpose** (a typo, say) to see what a build error looks
   like, then fix it. Better to meet that here than during build season.

