# Exceptions

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Exceptions](https://frc-team-4143.github.io/docs/software/java/advanced/exceptions).

At a competition our scouts type a line for every team in every match, like
`4143,12,38`: team 4143 scored 38 points in match 12. They type fast, so some
lines come out wrong: `1678,one,30`, `118,2`, or a team that scored `-5`. If
one bad line crashes the program, nobody gets any data. In this lesson you
write code that refuses bad data, says exactly what was wrong, and keeps going.

Java's tool for this is the **exception**: an object that means "something
went wrong here". Code **throws** an exception to stop and report a problem,
and other code **catches** it to deal with the problem instead of crashing.

## What you need to do

Team standard: fields use `trailing_snake_case_`. `ScoutReport`,
`Drivetrain` and `AutoStep` are already finished; read them, but you don't
need to change them.

1. **Refuse bad numbers** in the `ScoutEntry` constructor
   (`src/ScoutEntry.java`). Before storing anything, throw an
   `IllegalArgumentException` when:
   - `team` is 0 or less,
   - `match` is 0 or less, or
   - `points` is less than 0. (0 points is allowed.)

   Give each one a message that says what was wrong, like
   `"team must be positive, got 0"`.
2. **Write your own exception**, `BadLineException`
   (`src/BadLineException.java`).
   - Make it a **checked** exception: `extends Exception`, not
     `RuntimeException`.
   - Add a `protected int line_number_` field.
   - The constructor `BadLineException(String message, int line_number)`
     calls `super(message)` and stores the line number.
   - `getLineNumber()` returns it.
3. **Read one line** in `ScoutParser.parseLine(String line, int line_number)`
   (`src/ScoutParser.java`). It returns a `ScoutEntry`.
   - Split the line with `line.split(",")`. It must give exactly 3 pieces.
   - `trim()` each piece (that removes spaces at its ends), then turn it into
     a number with `Integer.parseInt`.
   - For anything wrong, throw a `BadLineException` carrying `line_number`.
     That includes the `NumberFormatException` that `Integer.parseInt` throws
     for text like `"one"`, and the `IllegalArgumentException` your
     `ScoutEntry` throws. Catch those and throw a `BadLineException` instead.
4. **Skip and count** in `ScoutParser.parseAll(String[] lines)`. It returns a
   `ScoutReport` and never crashes.
   - Lines are numbered from 1, so `lines[0]` is line 1.
   - Skip a blank line (empty, or only spaces). It isn't bad, just empty.
   - Add each good entry with `report.addEntry(entry)`.
   - When `parseLine` throws a `BadLineException`, catch it, record the line
     with `report.addBadLine(number)`, and carry on with the next line.
5. **Always stop** in `SafeAuto.runStep(AutoStep step, Drivetrain drive)`
   (`src/SafeAuto.java`). An `AutoStep` whose gyro is unplugged starts the
   wheels and then throws.
   - Call `step.run(drive)` in a `try` block and return `true` if it finishes.
   - Catch a `RuntimeException` and return `false`.
   - In a `finally` block, call `drive.stop()`, so the robot stops whatever
     happens.
6. **Run it.** `Main` reads some scouting text with typos in it, then runs a
   two-step auto where the second step fails. You can run it at any time to
   see how far you've got.

### New Java in this lesson

- `throw new IllegalArgumentException("message")` stops a method and reports
  a problem
- `try { ... } catch (SomeException e) { ... }` handles an exception instead
  of crashing. `e.getMessage()` is its message.
- `finally { ... }` runs whether the try block finished, threw or returned
- your own exception class: `extends Exception` and `super(message)`
- **checked** exceptions (extend `Exception`: callers must catch them or
  write `throws`) and **unchecked** ones (extend `RuntimeException`: usually a
  bug)
- `throws BadLineException` after a method's brackets warns its callers
- `Integer.parseInt(text)`, and the `NumberFormatException` it throws
- `text.split(",")` and `text.trim()`

There's one more form you may meet later: **try-with-resources**,
`try (FileReader reader = new FileReader("matches.csv")) { ... }`. It closes
the file for you when the block ends, like a built-in `finally`. This lesson
doesn't need it.

## Why this matters on the robot

Robot code runs 50 times a second for a whole match, so it must never crash:
a crashed robot just sits there. Code that talks to a camera, a sensor or a
file on the roboRIO catches the exceptions they throw and carries on with a
safe choice. And when something does go wrong in auto, the motors must stop.
That's exactly what `finally` guarantees.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
