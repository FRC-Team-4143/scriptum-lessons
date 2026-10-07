# Recursion

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

All the code lives in `src/Main.java`. No method bodies are given: write each full declaration yourself, and call your methods from `main` to try them.

## What you need to do

All the code lives in `src/Main.java`. No method bodies are given - same as
the Methods lesson, you write the full declaration yourself, and `main` is
empty for you to call your methods from. You've already solved some of
these with loops; this time, solve them again *without any loops* - each
method must call itself on a smaller input.

For every method, follow the same two-step recipe:

- **Decide the base case** - the smallest input you can answer directly
  without recursing. Write that `if` first and `return` the answer.
- **Decide the recursive step** - express the answer for `n` in terms of
  the same method on something smaller (`n - 1`, `n / 10`, everything but
  the last character), combined with whatever small piece is left over.

Then write:

1. `int factorial(int n)` - `n!`. Base case: `0!` and `1!` are both `1`.
2. `int fibonacci(int n)` - the `n`th Fibonacci number, 0-indexed
   (`fibonacci(0)` is `0`, `fibonacci(1)` is `1`). Base case: `n` less
   than 2. The recursive step adds the two numbers before it.
3. `int sumDigits(int n)` - the sum of `n`'s digits, for `n` 0 or greater.
   Base case: a single digit. Otherwise split off the last digit with `%`
   and recurse on what's left with `/`.
4. `String reverseString(String s)` - `s` reversed. Base case: a `String` of
   length 0 or 1 is already its own reverse. Otherwise it's the last
   character followed by the reverse of everything else.
5. **Call each from `main`** with a few inputs (including the base cases).
   If you see a `StackOverflowError`, your recursion never reaches its base
   case. When all four work, compare the recursive `factorial` and
   `reverseString` with your loop versions from the Methods lesson - which
   is easier to read?

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
