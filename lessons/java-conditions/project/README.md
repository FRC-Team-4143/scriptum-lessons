# Conditions

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

All the code lives in `src/Main.java`. Fill in each method and click **Run** any time to see its output in the terminal.

More depth: the team docs page [Conditions](https://docs.marswars.org/docs/software/java/basics/conditions).

## What you need to do

Each method in `src/Main.java` makes a decision and returns the result. The
lesson is choosing the *right decision tool* for each one - the checkpoint
only sees the answers, but each step below names the tool to use.

1. **Ranges.** `String batteryStatus(double percent)` sorts a percentage
   into `"EMPTY"` at exactly 0, `"LOW"` below 50, `"MEDIUM"` below 90, and
   `"FULL"` at 90 and above. Use an `if` / `else if` / `else` chain. Order
   matters: check the most specific case first, and think about what
   happens exactly at the boundaries (0, 50, 90).
2. **Name the choices.** Drive modes could be numbers (1, 2, 3...), but
   then nobody can tell what `mode = 3` means. An **enum** gives each one a
   name instead. Fill in the `DriveMode` enum near the top of `Main` with
   the values `TANK`, `ARCADE`, `SWERVE`, and `FIELD_ORIENTED_SWERVE`
   (team standard: enum type names are `UpperCamelCase` like classes, and
   the values are `SCREAMING_SNAKE_CASE`). Then delete the `//` in front of
   the `Drive mode SWERVE` line in `main`.
3. **One value, many cases.** `String driveModeName(DriveMode mode)` turns
   a drive mode into its name: `TANK` is `"Tank"`, `ARCADE` is `"Arcade"`,
   `SWERVE` is `"Swerve"`, and `FIELD_ORIENTED_SWERVE` is
   `"Field-Oriented Swerve"`. This is what a `switch` statement is for.
   Write each case with the bare value (`case TANK:`, not
   `case DriveMode.TANK:`), and use the `default` case to return
   `"Unknown mode"`.
4. **Combining conditions.** Each of these is a single `return` of a
   boolean expression - no `if` required.
   - `boolean canEnable(boolean has_comms, boolean e_stopped)` - `true` only
     when there's comms **and** the robot is not e-stopped. Use `&&` and `!`.
   - `boolean isWeekend(String day)` - `true` when `day` is `"Saturday"`
     **or** `"Sunday"`. Use `||`.
5. **Shorthand.** `String motorDirection(boolean is_reversed)` returns
   `"REVERSED"` or `"FORWARD"`. Write it with the ternary operator
   (`condition ? ifTrue : ifFalse`) on one line, instead of an `if`/`else`.
6. **Run it** and confirm the sample outputs in `main` match what you
   expect, then try a few inputs of your own (especially edge cases) before
   verifying.

## Checking your work

Open the lesson chip at the top, choose **Checkpoints** and run **Verify** whenever you want.
