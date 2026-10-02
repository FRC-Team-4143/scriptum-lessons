# AdvantageScope

Background: [AdvantageScope](https://docs.advantagescope.org/) is the tool
teams use to visualize robot telemetry - line graphs, 3D/2D fields, tables,
and more.

The robot code is already written and already publishing `FlywheelRPM` (an
oscillating value), `GamePieceLoaded` (a toggling boolean), a robot pose
driving a figure-8 around the field (`RobotPose2d`/`RobotPose3d`), swerve
drive data for the same robot (`SwerveModuleStates`, `ChassisSpeeds`,
`ChassisRotation`), and one *writable* value, `/Tuning/FlywheelTargetRPM` -
this lesson is about the tool, not the code. Click **Start** in the Driver
Station, choose a mode, and click **Enable**. Then open the **Scope** pane
on the right - AdvantageScope reads *live* data here, the same way Elastic
does, and connects automatically. No log file to open.

## Using AdvantageScope

With the robot running:

1. Drag **FlywheelRPM** onto a new **Line Graph** tab's left axis.
2. Right-click the left axis and lock its range to roughly 0-3000.
3. Drag **FlywheelRPM** again, this time onto the graph's right axis, then
   right-click the right axis and set its **Filter** to **Differentiate**.
   The right axis's own scale is left auto - no need to lock it.
4. Drag **GamePieceLoaded** onto the graph's discrete field (below the
   left/right axes) - it renders as colored bands instead of a numeric line.
5. Add a **2D Field** tab, drag **RobotPose2d** onto it, and set its field
   to **Evergreen** - you'll see the figure-8 path traced from directly
   above.
6. Add a **3D Field** tab, drag **RobotPose3d** onto it, and set its field
   to **Evergreen** too - the same path, now with a 3D robot model driving
   it. Field2d wants a `Pose2d` topic; Field3d wants a `Pose3d` one, which
   is why there are two separate topics publishing the same path.
7. On the 3D Field, set the camera to **Orbit Robot** - it'll follow the
   robot around the figure-8 instead of staying fixed on the whole field.
8. Add a **Swerve** tab and drag on three sources: **SwerveModuleStates**
   (each of the four wheels' speed and angle), **ChassisSpeeds** (the
   robot's overall forward/sideways/angular velocity), and
   **ChassisRotation** (the robot's heading - the same one driving the
   Field widgets above).
9. Click the slider icon next to the search bar - it turns purple when
   **Tuning Mode** is on. Find **FlywheelTargetRPM** in the sidebar (under
   the **Tuning** table, not AdvantageKit) and type in a new value. Only
   values published under `/Tuning` are editable this way - everything
   else on this page is read-only, which is why `FlywheelTargetRPM` lives
   there and the rest doesn't.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
Most checkpoints here look at whatever you've configured in AdvantageScope;
the tuning one needs the robot running and a value actually written through
Tuning Mode.
