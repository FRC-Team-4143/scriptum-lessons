# Recursion

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Recursion](https://docs.marswars.org/docs/software/java/basics/recursion).

All the code lives in `src/Main.java`. No method bodies are given: write each full declaration yourself, and call your methods from `main` to try them.

## What you need to do

All the code lives in `src/Main.java`. No method bodies are given - same as
the Methods lesson, you write the full declaration yourself, and `main` is
empty for you to call your methods from. You've already solved some of
these with loops; this time, solve them again *without any loops* - each
method must call itself on a smaller input.

### How to think about it

Recursion feels strange at first, and that's normal. The trick is to
**not** follow every call in your head. Instead, write the line that calls
the method on something smaller, and *pretend it already works*.

Take counting the dots in a triangle of dots, `triangle(rows)`. A triangle
with 4 rows is the bottom row of 4 dots sitting under a triangle with 3
rows. If `triangle(3)` hands you the right answer (6), then `triangle(4)`
is just `4 + 6`. You never have to work out *how* `triangle(3)` got 6. You
only have to finish the job.

So for each method, answer three questions, and each answer is a piece of
your code:

1. **What's the smallest input, and what's its answer?** That's the `if`
   at the top, and what it returns.
2. **What's a smaller version of the problem?** That's what you pass to
   the method when it calls itself.
3. **If that smaller call gives you the right answer, how do you finish?**
   That's the last `return` line.

Still stuck? Press **Dozer**, then **I'm so lost → Teach me, step by
step**. He has a guide for each method.

### The recipe

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

Open the lesson chip at the top, choose **Checkpoints** and run **Verify** whenever you want.
