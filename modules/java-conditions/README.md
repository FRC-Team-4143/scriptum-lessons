# Conditions

:::tip[Read this first: Conditions]

The team docs page for this lesson is the best place to learn the ideas
before you write any code:
[Conditions](https://frc-team-4143.github.io/docs/software/java/conditions) – Comparison Operators, Logical Operators (`&&`, `||`, `!`), Condition Logic
(`if`/`else if`/`else`), the Ternary Operator, and the Switch Statement -
one for each method below. Come back here when you're ready to apply it. If
a step below feels unfamiliar, that page is where to look first.

:::

All the code lives in `src/Main.java`. Click **Run** any time to see your
methods' output printed to the terminal.

:::note[Just enough about methods for now]

You haven't learned methods yet - that's its own lesson later. For now, all
you need is this: a method is a small box with a name. The words in its
parentheses are **parameters** - values handed to you, which you can use
like any other variable. `return` is how the method sends its answer back
out. For example:

```java
public static int square(int n) {
    return n * n;
}
```

Calling `square(5)` runs that code with `n` set to `5`, and hands back
`25`. That's it - fill in the methods below the same way. You'll get the
full picture in the **Methods** lesson later.

:::

## What you need to do

Each method in `src/Main.java` makes a decision and returns the result. The
lesson is choosing the *right decision tool* for each one - the checkpoint
only sees the answers, but each step below names the tool to use.

1. **Ranges.** `String batteryStatus(double percent)` sorts a percentage
   into `"EMPTY"` at exactly 0, `"LOW"` below 50, `"MEDIUM"` below 90, and
   `"FULL"` at 90 and above. Use an `if` / `else if` / `else` chain. Order
   matters: check the most specific case first, and think about what
   happens exactly at the boundaries (0, 50, 90).
2. **One value, many cases.** `String dayName(int day)` returns `"Monday"`
   through `"Sunday"` for `day` 1-7. This is what a `switch` statement is
   for; use its `default` case to return `"Invalid day"` for anything else.
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
