# Loops

:::tip[Read this first: Loops]

The team docs page for this lesson is the best place to learn the ideas
before you write any code:
[Loops](https://frc-team-4143.github.io/docs/software/java/loops) - focus on
For Loops and Increment Operators. The page also covers While Loops - read
why we avoid them before you're tempted to use one. Come back here when
you're ready to apply it. If a step below feels unfamiliar, that page is
where to look first.

:::

All the code lives in `src/Main.java`. Click **Run** any time to see your
methods' output printed to the terminal.

Team standard: avoid `while` loops (they're dangerous in robot code — one
missed exit condition and the robot stops responding). Use `for` loops
instead, which force you to think about the exit condition up front.

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

Each method in `src/Main.java` counts from 1 up to `n`, doing something
different with each number. All three use the same skeleton - a `for` loop
from 1 to `n` inclusive - so get that right first and adapt it.

1. **Accumulate.** `int sumTo(int n)` returns the sum of every integer from
   1 to `n`, computed with a `for` loop (not the shortcut formula). Declare
   the total *before* the loop so it survives between iterations.
2. **Count matches.** `int countDivisibleByThree(int n)` returns how many
   integers from 1 to `n` are evenly divisible by 3. Same shape as
   `sumTo`, but it only increments a counter when the current number passes
   a `%` test.
3. **Build a string.** `String fizzBuzz(int n)` is the classic exercise: one
   comma-separated string covering 1 to `n`, with `"FizzBuzz"` for numbers
   divisible by 3 and 5, `"Fizz"` for just 3, `"Buzz"` for just 5, and the
   number itself otherwise (e.g. `fizzBuzz(5)` -> `"1, 2, Fizz, 4, Buzz"`).
   Check the "divisible by both" case first, or `FizzBuzz` will never
   happen. The `", "` goes *between* items, with nothing trailing at the
   end.
4. **Run it**, compare against the examples above, and verify.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
