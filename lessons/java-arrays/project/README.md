# Arrays

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

All the code lives in `src/Main.java`. Fill in each method and click **Run** any time to see its output in the terminal.

More depth: the team docs page [Arrays](https://frc-team-4143.github.io/docs/software/java/arrays).

## What you need to do

Each method in `src/Main.java` takes an `int[]` and either summarizes it or
builds a new one. You'll use a loop over the array's elements in nearly
every one.

1. **Summarize an array.**
   - `int max(int[] values)` - the largest value. Track the largest seen so
     far as you loop.
   - `double average(int[] values)` - the average of every value, as a
     `double`. Total the values, then divide; watch out for integer
     division.
   - `boolean contains(int[] values, int target)` - whether `target`
     appears anywhere. You can stop as soon as you find it.
2. **Build a new array from an old one.** Both of these must return a
   **new** array and leave the caller's array exactly as they found it.
   Fill a new array of the same length, or copy first.
   - `int[] doubleAll(int[] values)` - every value doubled.
   - `int[] sort(int[] values)` - every value in ascending order.
     `java.util.Arrays` has a helper, or you can write your own sorting
     loop - either is fine.
3. **Build an array from nothing.** `int[] fibonacci(int n)` returns the
   first `n` Fibonacci numbers (`0, 1, 1, 2, 3, 5, 8, ...`) as a **new**
   array. Create an array of length `n` and fill it in with a loop, where
   each slot depends on the two before it. Handle small `n` (0, 1, 2)
   without crashing.
4. **Run it**; `main` prints the results for a sample array. Verify when
   they look right.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
