# Loops

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

All the code lives in `src/Main.java`. Fill in each method and click **Run** any time to see its output in the terminal. Use `for` loops (team standard: no `while`).

More depth: the team docs page [Loops](https://frc-team-4143.github.io/docs/software/java/loops).

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
