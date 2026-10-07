# Variables

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

All the code lives in `src/Main.java`. Click **Run** any time to see your output in the terminal.

More depth: the team docs page [Variables](https://frc-team-4143.github.io/docs/software/java/vars-dt).

## What you need to do

1. **Store four facts in local variables.** Inside `main`, declare one
   variable for each row below, choosing the datatype shown, and declare and
   assign on the same line. Name them whatever you like - the checkpoint
   reads what gets printed, not your variable names.

   | Datatype | Value | Printed key |
   | --- | --- | --- |
   | `int` | `4143` | `Team number` |
   | `double` | `3.14` | `Pi` |
   | `boolean` | `true` | `Robot is on` |
   | `String` | `"I am a string!"` | `Message` |

2. **Print each one** with `System.out.println`, as the key, a colon, one
   space, then the value - e.g.
   `System.out.println("Team number: " + yourVariableName);`.

   :::warning[Match the printed format exactly]

   The checkpoint looks for the exact text `key: value`. `Team number:4143`
   (missing the space) or `team number: 4143` (wrong capitalization) won't
   match, even though they print basically the same information.

   :::

3. **Add four named values to the `Main` class itself**, outside of `main`.
   The checkpoints look these up by name via reflection, so use exactly
   these names. Team standard: constants and enums are `final` /
   `SCREAMING_SNAKE_CASE`.
   - `String TEAM_NAME` - a `public static final` constant equal to
     `"Team 4143"`.
   - `double MAX_SPEED` - a `public static final` constant equal to `5.0`.
   - An `enum` named `ALLIANCE` with values `RED` and `BLUE`.
   - An `enum` named `MATCH_PERIOD` with values `AUTONOMOUS`, `TELEOP`, and
     `ENDGAME`.

   Define both enums as nested types inside `Main`, not as their own
   top-level types - they're only used by `Main`, so they belong there.
4. **Run it** and compare your output against the table, then verify.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
