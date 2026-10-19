# Interfaces & Polymorphism

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Interfaces & Polymorphism](https://frc-team-4143.github.io/docs/software/java/advanced/interfaces).

A robot runs everything off one 12-volt battery. Drive motors, LEDs, the
intake: each one pulls current (amps), and if they pull too much at once the
voltage sags and the robot browns out. In this lesson you write the code that
adds up the robot's battery draw.

The catch: a motor, an LED strip and an intake are very different classes.
How can one method add them all up? The answer is an **interface**: a promise
that says "every class that signs up has these methods". Code that only needs
those methods can then work with any of them. That is called
**polymorphism** ("many shapes").

## What you need to do

Team standard: fields use `trailing_snake_case_`.

1. **Write the `PowerUser` interface** in `src/PowerUser.java`. It declares two
   methods, with no bodies: `String getName()` and `double getAmps()`.
2. **Make `Motor` and `LedStrip` implement it.** Add `implements PowerUser` to
   each class, then write the two methods (put `@Override` above each one).
   - `Motor` returns the name and amps it was built with.
   - `LedStrip` is named `"LED strip"`, and draws `AMPS_PER_LED` amps for every LED.
3. **Write the abstract class `Mechanism`** in `src/Mechanism.java`. Every
   mechanism has a name, so the name code lives here once instead of in every
   mechanism:
   - `public abstract class Mechanism implements PowerUser`
   - a `protected String name_` field and a constructor `Mechanism(String name)`
   - `getName()` returns `name_`
   - `boolean isOverLimit(double limit_amps)` returns `true` when `getAmps()`
     is more than `limit_amps`
   - **no** `getAmps()`: `Mechanism` doesn't know how much a mechanism draws.
     Leaving it out is allowed *because* the class is `abstract`, and every
     subclass has to write its own.
4. **Make `Intake` extend `Mechanism`.** Its constructor `Intake(String name)`
   calls `super(name)`. Its `getAmps()` returns `RUNNING_AMPS` while it is
   running and `0.0` when it isn't. Don't write `getName()` in `Intake`: it
   already has the one from `Mechanism`.
5. **Write `PowerBudget.totalAmps(PowerUser[] users)`**: the sum of
   `getAmps()` over the whole array (`0.0` for an empty one).
6. **Write `PowerBudget.biggestUser(PowerUser[] users)`**: the name of the part
   that draws the most amps. If two tie, return the first one. Return `"none"`
   for an empty array.
7. **Run it.** `Main` builds a small robot and prints its power budget.

Neither `PowerBudget` method may check what kind of part it has (no
`instanceof`, no `if` on the name). Call `getAmps()` and let each class answer
for itself. That's polymorphism.

### New Java in this lesson

- `interface` and `implements`
- `abstract class` and methods a subclass must write
- `@Override` on a method that comes from an interface
- a **for-each loop**, `for (PowerUser user : users)`, which visits each item
  of an array in turn without an index

## Why this matters on the robot

Robot code is full of interfaces. Every subsystem promises the same methods
so the robot can update all of them in one loop, and WPILib's motor
controllers share an interface so the same drive code works with different
brands of motor.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
