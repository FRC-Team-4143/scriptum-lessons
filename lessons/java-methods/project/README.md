# Methods

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

All the code lives in `src/Main.java`. Unlike earlier lessons, no method
bodies are given to fill in - you write the full declaration yourself:
modifiers, return type, name, and parameter list. `main` is empty too;
call your methods from there to try them out, the same way you tested code
directly in `main` in previous lessons.

More depth: the team docs page [Methods](https://frc-team-4143.github.io/docs/software/java/methods).

## What you need to do

There's no scaffolding this time: `src/Main.java` has only comments
describing six methods, and you write the full declaration yourself -
modifiers, return type, name, and parameter list. The names, parameter
types, and return types must match what's below exactly, or the checkpoint
won't be able to call your code. The first two methods show *why* methods
exist; the rest are classic practice problems.

1. **Write `clamp`** - the reusable building block. Everything that
   controls a motor needs to keep a value inside a range, so write that
   check once.
   - `double clamp(double value, double min, double max)` - returns `min`
     if `value` is below `min`, `max` if it's above `max`, and `value`
     unchanged otherwise.
2. **Write `scaleJoystick` by calling `clamp`.** The checkpoint can't see
   whether you reused `clamp`, but that's the lesson: don't repeat the
   range check, reuse the method you already wrote.
   - `double scaleJoystick(double rawInput, double sensitivity)` -
     `rawInput * sensitivity`, clamped to `[-1.0, 1.0]`.
3. **Write three classics** using the loop skills from earlier lessons.
   Think about edge cases: what's the answer for `0`, `1`, negatives, or an
   empty string?
   - `boolean isPrime(int n)` - whether `n` is 2 or greater with no
     divisors other than 1 and itself.
   - `int factorial(int n)` - `n * (n-1) * ... * 1`; `0!` is `1`.
   - `String reverseString(String s)` - a **new** `String` with `s`'s
     characters in reverse order.
4. **Write `isPalindrome` by calling `reverseString`** - the same
   build-on-what-you-have idea as step 2.
   - `boolean isPalindrome(String s)` - whether `s` reads the same forwards
     and backwards.
5. **Call your methods from `main`** (it starts empty) with a few different
   inputs, including edge cases, run, and check the output yourself before
   you verify.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
