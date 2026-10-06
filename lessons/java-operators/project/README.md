# Operators

:::tip[Read this first: Operators]

The team docs page for this lesson is the best place to learn the ideas
before you write any code:
[Operators](https://frc-team-4143.github.io/docs/software/java/operators) -
the Arithmetic and Assignment sections cover everything in this lesson,
including `%` and `+=`/`-=`/`*=`/`/=`. Come back here when you're ready to
apply it. If a step below feels unfamiliar, that page is where to look
first.

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
