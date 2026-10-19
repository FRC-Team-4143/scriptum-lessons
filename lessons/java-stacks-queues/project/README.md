# Stacks & Queues

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Stacks & Queues](https://frc-team-4143.github.io/docs/software/java/advanced/stacks-queues).

A list lets you reach any item you like. Sometimes that's more freedom than
you want. Two simpler shapes show up all over robot code, and the only
difference between them is **which item comes out next**:

| Shape | Comes out next | Think of | Robot example |
| --- | --- | --- | --- |
| **stack** | the *newest* item (last in, first out) | a pile of plates | Undo on the dashboard |
| **queue** | the *oldest* item (first in, first out) | the line at a food truck | auto steps, run in order |

In this lesson you use Java's `ArrayDeque` (say "array deck") as both, then
build a queue yourself out of a plain array, the way the robot's indexer
lines up game pieces.

## What you need to do

`src/Main.java` is already written. It tries all four classes and prints what
happens, so click **Run** any time. Team standard: fields use
`trailing_snake_case_`.

1. **Undo with a stack** (`src/SettingHistory.java`). The drive coach changes
   the driver's max speed on the dashboard, and wants an Undo button.
   - Add `import java.util.ArrayDeque;` at the top, and the field
     `protected ArrayDeque<Double> history_ = new ArrayDeque<>();`
   - `set(double value)` pushes the value the setting has *now* onto
     `history_`, then changes it to `value`.
   - `undo()` pops the newest saved value back into `current_`. With nothing
     to undo, it does nothing (popping an empty deque crashes).
   - `undoCount()` returns how many values are saved.
2. **Balanced brackets** (`src/AutoParser.java`). Autos can be typed as text,
   like `seq(drive[2.0], par{intake, shoot})`. Write
   `isBalanced(String text)`: `true` when every `(`, `[` and `{` is closed by
   its own kind of bracket, in the right order. Letters, numbers, commas and
   spaces don't matter. Use a stack: push each opening bracket, and when a
   closing one comes, pop the top and check they match. Loop over the
   characters with `for (char c : text.toCharArray())`.
3. **Auto steps in a queue** (`src/AutoQueue.java`).
   - Add the field `protected ArrayDeque<String> steps_ = new ArrayDeque<>();`
   - `addStep(String step)` puts the step at the back with `offer`.
   - `nextStep()` takes the front step off with `poll` and returns it, or
     returns `"done"` when there are none left.
   - `stepsLeft()` returns how many are waiting.
4. **A queue by hand** (`src/IndexerQueue.java`). The indexer has a fixed
   number of slots. Build it on the `slots_` array, with no `java.util` at
   all. `front_` is the slot that fires next and `count_` is how many pieces
   are loaded.
   - The constructor makes `slots_` an array with room for `capacity` pieces.
   - `isFull()` is `true` when every slot holds a piece.
   - `load(String piece)` returns `false` and changes nothing when full.
     Otherwise it puts the piece in the slot just behind the last piece and
     returns `true`.
   - `fire()` takes the piece at `front_` out and returns it, or returns
     `"empty"` when nothing is loaded.
   - When a position runs off the end of the array, it **wraps round** to
     slot `0`. The `%` (remainder) operator does that for you.
5. **Run it.** `Main` undoes two speed changes, checks two autos for typos,
   runs a four-step auto and cycles notes through a two-slot indexer.

### New Java in this lesson

- `ArrayDeque<Double>`: a collection you can add to and take from at either
  end. "Deque" is short for **d**ouble-**e**nded **que**ue.
- as a stack: `push` (add on top), `pop` (take the top off), `peek` (look at
  the top without taking it)
- as a queue: `offer` (add at the back), `poll` (take the front off)
- `isEmpty()` and `size()`, which work for both
- `char` and character literals in single quotes, like `'('`
- `text.toCharArray()`, to loop over a `String` one character at a time
- `%`, the remainder operator, used to wrap an index round: `(i + 1) % 3`
  goes 1, 2, 0, 1, 2, 0...

## Why this matters on the robot

A command-based robot is full of queues: a sequence of commands runs one
after another in the order you wrote them, and log messages go out in the
order they happened. Stacks are rarer, but they're how anything nested gets
checked or undone, from a parser reading an auto file to the Java computer
keeping track of which method called which (that's why a crash prints a
"stack trace"). And a fixed-size array queue like your indexer is what
robot code uses when it can't afford to make new objects every 20
milliseconds, such as a buffer holding the last few sensor readings.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
