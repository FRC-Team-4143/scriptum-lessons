# Big-O & Efficiency

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Big-O & Efficiency](https://frc-team-4143.github.io/docs/software/java/advanced/big-o).

Your scouting app works fine on the five teams you tested it with. Then you
load a whole district, 3000 teams, and the "did a scout type a team twice?"
check takes forever. Nothing is broken. The code just does far more work than
it needs to, and the extra work grows much faster than the list does.

This lesson is about **counting steps**: how many times your code looks at
an item, and how that number grows as the list (called **n**) gets bigger.
Programmers write that growth with **Big-O**:

| Big-O | Name | What it means | n = 1000 |
| --- | --- | --- | --- |
| O(1) | constant | the same steps, however big n is | 1 |
| O(log n) | logarithmic | each step throws away half of what's left | about 10 |
| O(n) | linear | one step per item | 1000 |
| O(n²) | quadratic | a step for every pair of items | 1,000,000 |

Big-O ignores small details. A loop that takes 2 steps per item, or two loops
one after the other, is still O(n): it grows in a straight line. What matters
is the *shape* of the growth.

## What you need to do

Team standard: fields use `trailing_snake_case_`.

1. **Classify the snippets** in `src/Answers.java`. Each one is a short piece
   of code in a comment. Replace each `NOT_SURE` with `CONSTANT`, `LOG_N`,
   `LINEAR` or `N_SQUARED` (the choices are in `BigO.java`). For each one,
   ask: "if n doubles, what happens to the number of steps?"
2. **`ScoutStats.latest(scores)`** in `src/ScoutStats.java`: the score from
   the last match in the list, or `0` for an empty list. It must take one look
   at the list, however long it is. That's O(1).
3. **`ScoutStats.best(scores)`**: the highest score, or `0` for an empty list.
   Look at each score once. That's O(n).
4. **`Duplicates.hasDuplicate(teams)`** in `src/Duplicates.java`: `true` if
   any team number appears twice. `hasDuplicateSlow` already does this, but it
   compares every team with every other team: O(n²). Yours must look at each
   team only once. Keep a `HashSet` of the teams you've already seen.
5. **`EventOverlap.teamsInBoth(event1, event2)`** in `src/EventOverlap.java`:
   how many *different* teams appear in both lists. A team written twice at
   one event still counts once. Look at each list's items about once: O(n + m).
6. **Run it.** `Main` runs both duplicate checks on 2000 teams and prints how
   many looks each one took.

The checker counts how many times your code looks at an item in a list: with
`get(i)`, a for-each loop, `contains()` and so on. It uses lists much bigger
than the ones in `Main`, so a slow method fails even when its answer is right.

### New Java in this lesson

- **Big-O** notation: O(1), O(log n), O(n), O(n²)
- an `enum`, a type with a fixed list of values (`BigO.java`): you pick one
  with `BigO.LINEAR`
- why `HashSet.contains` is about one step but `ArrayList.contains` is a whole
  loop
- comparing `Integer` objects with `.equals`, not `==`

## Why this matters on the robot

The robot runs its code every 20 milliseconds. If a loop takes longer than
that, the robot misses its deadline and you see "loop overrun" warnings in
the Driver Station. Vision code that checks every camera target against every
other one, or a path planner that tries every pair of points, can blow that
budget as soon as there are a lot of them. Knowing the Big-O of your code
tells you *before* the match whether it will keep up.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
