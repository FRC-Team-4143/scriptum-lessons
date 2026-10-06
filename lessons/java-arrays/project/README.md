# Arrays

:::tip[Read this first: Arrays and ArrayLists]

The team docs page for this lesson is the best place to learn the ideas
before you write any code: [Arrays and
ArrayLists](https://frc-team-4143.github.io/docs/software/java/arrays) - the
Arrays and For Each sections are what you need here. (ArrayLists are covered
too, but this lesson sticks to plain arrays.) Come back here when you're
ready to apply it. If a step below feels unfamiliar, that page is where to
look first.

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
