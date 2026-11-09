# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Write your own PID

The motor controller has been running PID for you. Write one yourself, in a new file,
`subsystems/shooter/FlywheelPid.java`, in plain Java:

```java
public class FlywheelPid {
  public FlywheelPid(double kV, double kP, double kI, double kD) { ... }
  /** Returns the volts for the motor, from -12 to 12. */
  public double calculate(double target, double measured, double dtSeconds) { ... }
  /** Forgets the added-up error and the last speed. */
  public void reset() { ... }
}
```

```text
volts = kV x target  +  kP x error  +  kI x (error added up over time)  -  kD x (how fast measured is changing)
```

Three things separate a real controller from the formula:

1. **Clamp.** The battery only has 12 volts.
2. **Anti-windup.** During a long spin-up the error is huge, so the added-up error grows and grows, and later
   overshoots the target. Only add up the error while it's small (within 30 rad/s of the target) and the
   output isn't stuck at the limit.
3. **No derivative kick.** The `kD` part uses the change in `measured`, not in the error, so changing the target doesn't slam the motor. The first call after a reset has no change to look at, and a `dtSeconds` of 0 must not divide by zero.

Then use it. In `ShooterSubsystem`, make one from your gains, and in the `PID` case of `runFlywheel()` send
`calculate(CONSTANTS.SHOOT_VELOCITY, flywheel_.getCurrentVelocity(), 0.02) / 12.0` to `setTargetDutyCycle`, instead
of `setTargetVelocity`. Call `reset()` whenever the shoot button is up. A `kI` of about `0.4` works well. Leave
`FLYWHEEL_CONTROL` on `PID`, and check your numbers still pass the other checkpoints.

**How it's checked:** Verify tests your class on exact numbers, then flies it against a model of the real
flywheel: a long spin-up, a `kV` that's wrong on purpose, and a launch. No simulator needed, but do run it on the robot: that's the fun part.
