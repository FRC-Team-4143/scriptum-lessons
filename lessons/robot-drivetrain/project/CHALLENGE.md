# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Driver-friendly sticks

Plain arcade drive has two problems a driver feels right away. Small stick movements are twitchy, and at
full forward the robot can't turn: `forward + turn` goes past `1.0`, the drivetrain cuts it off at `1.0`, and
the difference between the sides is lost. Fix both in `teleopPeriodic()`:

1. **Square each stick and keep its sign.** A stick at `0.5` gives `0.25`, and a stick at `-0.5` gives `-0.25`.
2. **Never lose the turn.** After squaring, add up the size of each stick, ignoring the signs (so
   `-0.5` counts as `0.5`). If that total is more than `1.0`, divide both `left_speed` and
   `right_speed` by it.

| Stick (forward, turn) | Left | Right |
| --- | --- | --- |
| 0.5, 0.0 | 0.25 | 0.25 |
| 1.0, 0.5 | 1.0 | 0.6 |
| 1.0, 1.0 | 1.0 | 0.0 |

Work out one row of the table on paper first.

### Two Java tools that help

Java has a built-in `Math` class full of ready-made methods. You call them like this, and each one hands
back a number. Two of them make this challenge much easier.

**`Math.abs(x)`** gives you the size of `x` without its sign (abs is short for *absolute value*):

```java
Math.abs(0.5)    // 0.5
Math.abs(-0.5)   // 0.5  (the minus sign is gone)
Math.abs(0.0)    // 0.0
```

That helps with both steps:

- **Step 1:** squaring a number with `x * x` always makes it positive (`-0.5 * -0.5` is `0.25`), so the robot
  could never drive backward. Multiply by the size instead: `x * Math.abs(x)` is `0.25` when `x` is `0.5`,
  and `-0.25` when `x` is `-0.5`. Try both on paper to see why.
- **Step 2:** `Math.abs(forward) + Math.abs(turn)` is the total size of the two sticks.

**`Math.max(a, b)`** gives you whichever of the two numbers is bigger:

```java
Math.max(1.0, 1.5)   // 1.5
Math.max(1.0, 0.8)   // 1.0
Math.max(1.0, 1.0)   // 1.0
```

That's how step 2 works without an `if`. Dividing by `1.0` changes nothing. So if you divide by the bigger
of `1.0` and your total, the speeds only shrink when the total is over `1.0`, and stay exactly the same
otherwise. Check it against the last row of the table: what's the total there, and what do you divide by?

**How it's checked:** Verify runs your `teleopPeriodic()` with exact stick positions (a keyboard only gives
full stick) and reads what you send to `setDutyCycles`. No simulator needed.
