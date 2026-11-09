# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Stop on the mark

Your bang-bang robot coasts about 70 centimeters past 2 meters. Slowing down doesn't fix it, because a
robot that crawls gives away the match. Make Auto **stop within 3 centimeters of 2.0 meters, in under
2.5 seconds**. (This also still passes the **Drive 2 meters** checkpoint.)

Things to think about:

- Let the power depend on how far is left: big when far, small when close. Plot `Robot/Distance` and see what happens.
- That alone overshoots, because a 50 kg robot carries its speed. What else do you know in
  `robotPeriodic()` that says it's about to overshoot?
- Power can be negative. Pushing backward is the only real brake a robot has.
- Keep the numbers as constants so you can tune them, and change one at a time.
- Keep an `if` / `else` on `distance_meters_` in `autonomousPeriodic()`: "close enough to 2.0, so stop" is a
  natural one, and the **Deadband and if statements** checkpoint still looks for it.

**How it's checked:** run Auto with the simulator running. The check uses where the robot **really** is
(`Drive/TruePose`), and judges each run once the robot has stopped for half a second. Plot
`Check/Challenge/StopErrorMeters` and `Check/Challenge/StopSeconds` to see how close you got.
