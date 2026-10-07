# Operators

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

All the code lives in `src/Main.java`. Fill in each method and click **Run** any time to see its output in the terminal.

More depth: the team docs page [Operators](https://frc-team-4143.github.io/docs/software/java/operators).

## What you need to do

Every method in `src/Main.java` starts as a stub that returns a placeholder
(`0`, `0.0`, or `false`). Replace each placeholder with the real expression -
these are all one-liners, except the last.

1. **Basic arithmetic.** Combine the two parameters with the right operator.
   - `int addInts(int a, int b)` - return `a + b`.
   - `int subtractInts(int a, int b)` - return `a - b`.
2. **Unit conversions** (1 inch = 0.0254 meters). One is a multiply and the
   other a divide by the same factor, so they should undo each other.
   - `double inchesToMeters(double inches)`
   - `double metersToInches(double meters)`
3. **Even or odd.** Ask whether dividing by 2 leaves a remainder of zero,
   which is exactly what `%` reports.
   - `boolean isEven(int value)`
4. **Assignment operators.** Keep one variable and update it with each
   operator in turn, rather than computing the answer in one formula - the
   point is to see what each operator does.
   - `double applyAssignments(double start)` - starting from `start`, apply
     each of these in order and return the final value:
     `x = start;` -> `x += 1;` -> `x -= 0.14;` -> `x *= 6;` -> `x /= 3;`
5. **Run it.** `main` already calls every method with sample inputs, so
   compare the printed results to what you'd work out by hand, then verify.

:::note[Just enough about comparisons for now]

`isEven` needs one more thing you haven't formally learned yet: `==`
compares two values and evaluates directly to a `boolean` - `true` or
`false` - the same way `%` evaluates to a number. You don't need an `if`
statement at all:

```java
public static boolean isPositive(int n) {
    return n > 0;
}
```

`value % 2 == 0` works the same way - one full expression, not two separate
steps. Comparisons (`==`, `!=`, `<`, `>`, and friends) get their own lesson
soon, in **Conditions**.

:::

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
