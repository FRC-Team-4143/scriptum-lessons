# Conditions

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

All the code lives in `src/Main.java`. Fill in each method and click **Run** any time to see its output in the terminal.

More depth: the team docs page [Conditions](https://frc-team-4143.github.io/docs/software/java/conditions).

## What you need to do

Each method in `src/Main.java` makes a decision and returns the result. The
lesson is choosing the *right decision tool* for each one - the checkpoint
only sees the answers, but each step below names the tool to use.

1. **Ranges.** `String batteryStatus(double percent)` sorts a percentage
   into `"EMPTY"` at exactly 0, `"LOW"` below 50, `"MEDIUM"` below 90, and
   `"FULL"` at 90 and above. Use an `if` / `else if` / `else` chain. Order
   matters: check the most specific case first, and think about what
   happens exactly at the boundaries (0, 50, 90).
2. **One value, many cases.** `String driveModeName(int mode)` turns a
   drive mode number into its name: `1` is `"Tank"`, `2` is `"Arcade"`, `3`
   is `"Swerve"`, and `4` is `"Field-Oriented Swerve"`. This is what a
   `switch` statement is for; use its `default` case to return
   `"Unknown mode"` for anything else.
3. **Combining conditions.** Each of these is a single `return` of a
   boolean expression - no `if` required.
   - `boolean canEnable(boolean hasComms, boolean eStopped)` - `true` only
     when there's comms **and** the robot is not e-stopped. Use `&&` and `!`.
   - `boolean isWeekend(String day)` - `true` when `day` is `"Saturday"`
     **or** `"Sunday"`. Use `||`.
4. **Shorthand.** `String motorDirection(boolean isReversed)` returns
   `"REVERSED"` or `"FORWARD"`. Write it with the ternary operator
   (`condition ? ifTrue : ifFalse`) on one line, instead of an `if`/`else`.
5. **Run it** and confirm the sample outputs in `main` match what you
   expect, then try a few inputs of your own (especially edge cases) before
   verifying.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
