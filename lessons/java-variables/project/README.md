# Variables

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

All the code lives in `src/Main.java`. Click **Run** any time to see your output in the terminal.

More depth: the team docs page [Variables](https://frc-team-4143.github.io/docs/software/java/basics/vars-dt).

## Which names matter

- **Variables inside `main`:** only the printed line is checked, so name them
  how you like (team standard: `snake_case`, lowercase words joined by
  underscores, like `team_number`).
- **Constants and enums:** the checkpoints look them up by name, so use exactly
  the names below.

## What you need to do

1. **Store four facts in local variables.** Inside `main`, declare one
   variable for each row below, choosing the datatype shown, and declare and
   assign on the same line.

   | Datatype | Value | Printed key |
   | --- | --- | --- |
   | `int` | `4143` | `Team number` |
   | `double` | `3.14` | `Pi` |
   | `boolean` | `true` | `Robot is on` |
   | `String` | `"I am a string!"` | `Message` |

2. **Print each one** with `System.out.println`, as the key, a colon, one
   space, then the value - e.g.
   `System.out.println("Team number: " + your_variable_name);`.

   :::warning[Match the printed format exactly]

   The checkpoint looks for the exact text `key: value`. `Team number:4143`
   (missing the space) or `team number: 4143` (wrong capitalization) won't
   match, even though they print basically the same information.

   :::

3. **Add four named values to the `Main` class itself**, outside of `main`.
   Use exactly these names. Team standard: constants are `final` /
   `SCREAMING_SNAKE_CASE`, enum type names are `UpperCamelCase` like classes (the enum's values are `SCREAMING_SNAKE_CASE`).
   - `String TEAM_NAME` - a `public static final` constant equal to
     `"Team 4143"`.
   - `double MAX_SPEED` - a `public static final` constant equal to `5.0`.
   - An `enum` named `Alliance` with values `RED` and `BLUE`.
   - An `enum` named `MatchPeriod` with values `AUTONOMOUS`, `TELEOP`, and
     `ENDGAME`.

   Define both enums as nested types inside `Main`, not as their own
   top-level types - they're only used by `Main`, so they belong there.
4. **Run it** and compare your output with this, then verify. The starter
   code already prints the last two lines, to show your constants. They look
   wrong until you fill in the constants in step 3.

   ```text
   Team number: 4143
   Pi: 3.14
   Robot is on: true
   Message: I am a string!
   Team name: Team 4143
   Max speed: 5.0
   ```

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
