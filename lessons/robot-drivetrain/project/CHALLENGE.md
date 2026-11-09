# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Driver-friendly sticks

Plain arcade drive has two problems a driver feels right away. Small stick movements are twitchy, and at
full forward the robot can't turn: `forward + turn` goes past `1.0`, the drivetrain cuts it off at `1.0`, and
the difference between the sides is lost. Fix both in `teleopPeriodic()`:

1. **Square each stick and keep its sign.** A stick at `0.5` gives `0.25`, and a stick at `-0.5` gives `-0.25`.
2. **Never lose the turn.** After squaring, if `|forward| + |turn|` is more than `1.0`, divide both
   `left_speed` and `right_speed` by it.

| Stick (forward, turn) | Left | Right |
| --- | --- | --- |
| 0.5, 0.0 | 0.25 | 0.25 |
| 1.0, 0.5 | 1.0 | 0.6 |
| 1.0, 1.0 | 1.0 | 0.0 |

You can do it without an `if`, using two methods from Java's `Math` class: `Math.abs(x)` and
`Math.max(a, b)`. Work out one row of the table on paper first.

**How it's checked:** Verify runs your `teleopPeriodic()` with exact stick positions (a keyboard only gives
full stick) and reads what you send to `setDutyCycles`. No simulator needed.
