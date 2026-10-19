# Generics

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Generics](https://frc-team-4143.github.io/docs/software/java/advanced/generics).

In the Collections lesson you wrote `ArrayList<Integer>` and
`HashMap<Integer, Integer>`. The type in the angle brackets told the list
what it holds. That's called **generics**, and in this lesson you write
generic code of your own.

Here's the problem it solves. The robot wants to remember its last few gyro
headings, and the driver station wants to remember its last few alerts. Same
job, different types: `Double` and `String`. Without generics you'd write the
class twice, or once with `Object` and lose track of what's inside. With
generics you write it once as `RingBuffer<T>`, and `T` is a blank that each
use fills in: `RingBuffer<Double>`, `RingBuffer<String>`.

## What you need to do

Team standard: fields use `trailing_snake_case_`. `Main` won't compile until
all three classes are generic, so do the steps first, then run it.

1. **`Pair<A, B>`** (`src/Pair.java`): two values that belong together, like a
   team number and its nickname.
   - Declare it `public class Pair<A, B>`.
   - Fields `protected A first_` and `protected B second_`.
   - A constructor `Pair(A first, B second)` that stores both.
   - `getFirst()` returns `first_` (type `A`) and `getSecond()` returns
     `second_` (type `B`).
2. **`RingBuffer<T>`** (`src/RingBuffer.java`): keeps only the newest items,
   at most `capacity_` of them.
   - Declare it `public class RingBuffer<T>`, and add the field
     `protected ArrayList<T> items_ = new ArrayList<>();`
   - `add(T item)`: if it already holds `capacity_` items, remove the oldest
     (index 0) first. Then add `item` at the end.
   - `size()` returns how many items it holds now.
   - `get(int index)` returns the item at that index, type `T`. Index 0 is the
     oldest item still kept.
   - `latest()` returns the newest item, type `T`, or `null` when it's empty.
3. **A generic method**, `ListTools.lastOrDefault` (`src/ListTools.java`).
   Change the starter's `Object` version to
   `public static <T> T lastOrDefault(List<T> items, T fallback)`. It returns
   the last item in the list, or `fallback` if the list is empty.
4. **A bounded type**, `ListTools.max`. Change it to
   `public static <T extends Comparable<T>> T max(List<T> items)`. It returns
   the biggest item, using `compareTo` to compare them. If two tie, return the
   first one. Return `null` for an empty list.
5. **Run it.** `Main` keeps gyro headings and alerts in ring buffers, prints
   an alliance from a list of pairs, and uses your two methods on lists of
   text and of numbers.

### New Java in this lesson

- a **generic class**: `public class RingBuffer<T>`, then `T` used as a type
  inside it
- several **type parameters**: `public class Pair<A, B>`
- a **generic method**: `public static <T> T lastOrDefault(...)`. The `<T>`
  goes before the return type, and Java works out what `T` is from the
  arguments you pass.
- a **bounded type**: `<T extends Comparable<T>>` only allows types that can
  be compared, so you can call `compareTo` on them
- `a.compareTo(b)`: negative when `a` is smaller, `0` when they're equal,
  positive when `a` is bigger
- type parameters take classes, so write `Integer` and `Double`, not `int`
  and `double`

## Why this matters on the robot

Generics are everywhere in robot code. Filters that smooth a noisy sensor
keep a buffer of its last readings, just like your `RingBuffer`. WPILib's
`Pair` holds two related values, and its interpolating lookup tables (distance
→ shooter speed) are generic in both their key and value types. And every
`ArrayList` of subsystems or `HashMap` of AprilTag positions is a generic
class someone wrote once so that everyone could use it for anything.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
