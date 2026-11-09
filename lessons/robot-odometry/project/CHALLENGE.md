# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Waypoint tour

Your Auto can only drive straight along x. Real autos go places. Make Auto drive to **(2.0, 1.0)**, then to
**(3.5, -1.0)**, and stop there, in under 10 seconds. (Stopping there also still passes the **Pose in auto**
checkpoint.)

Keep the waypoints in an **array of `Translation2d` objects**, and a field that says which one is next. Each loop:

- Subtract your position from the waypoint: `Translation2d` has `minus`, `getNorm()` (how far) and `getAngle()` (which way).
- Compare that direction with the way you're facing. `Rotation2d` has `minus` and `getRadians()`, and it handles the wraparound at pi for you.
- Turn toward it, and drive forward only as much as you're facing it. `Math.cos(angle)` is one neat way. Watch the sign: `arcadeDrive`'s turn is positive to the **right**, but angles are positive to the **left**.
- Slow down near the last waypoint, and remember to start over in `autonomousInit()`.

**How it's checked:** run Auto with the simulator running. The check uses where the robot **really** is
(`Drive/TruePose`): it must come within 0.3 meters of each waypoint in order, then stop within 0.3 meters of
the last one. `Check/Challenge/WaypointsReached` says how far you got. Your own `Drive/Pose` may disagree a little: that's the drift you saw in step 6.
