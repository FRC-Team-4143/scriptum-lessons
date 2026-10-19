# Lambdas & Functional Interfaces

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Lambdas & Functional Interfaces](https://frc-team-4143.github.io/docs/software/java/advanced/lambdas).

So far, a variable has held a *value*: a number, a string, an object. In this
lesson a variable holds *behavior*: a little piece of code that you can pass
around and run later. That piece of code is called a **lambda**.

```java
SpeedFilter half = speed -> speed * 0.5;   // nothing runs yet
half.apply(0.8);                           // now it runs: 0.4
```

A lambda needs a type, and that type is a **functional interface**: an
interface with exactly **one** method. You already know interfaces from the
last lesson. A lambda is just a very short way to write a class that
implements one, without writing the class.

You'll use this to wire up a pretend robot: the driver's stick, three
buttons, an intake and a drivetrain.

## What you need to do

Team standard: fields use `trailing_snake_case_`.

1. **Write the `SpeedFilter` interface** in `src/SpeedFilter.java`. Put
   `@FunctionalInterface` on the line above it and declare its one method:
   `double apply(double speed);`
2. **Write three filters in `src/Filters.java`.** Each method `return`s a lambda.
   - `half()`: halves the speed.
   - `invert()`: flips the sign (`0.3` becomes `-0.3`).
   - `deadband(double threshold)`: `0.0` when `Math.abs(speed) < threshold`,
     otherwise the speed unchanged. This one needs an `if`, so give the lambda
     a body in `{ }` with `return` lines.
3. **Write `Filters.chain(SpeedFilter first, SpeedFilter second)`.** It returns a
   lambda that runs `first`, then runs `second` on the result. Behavior built
   out of behavior: `chain(deadband(0.1), half())` is a gentle stick.
4. **Finish `src/ButtonBoard.java`.** `Binding` is given: it holds a condition, an
   action and `was_true_`.
   - `onTrue(BooleanSupplier condition, Runnable action)` adds a new `Binding`
     to `bindings_`. It does **not** run the action.
   - `poll()` runs once every robot loop. For each binding, read the condition
     now. If it is `true` now but was `false` last time, the button was just
     pressed: run the action. Then remember the value for next time. Holding a
     button down runs its action once, not 50 times a second.
5. **Wire up the robot in `src/RobotBindings.java`.**
   - `configureBindings()`: A runs the intake, B stops it, X ejects at
     `EJECT_POWER`. Use **method references** (`controller_::a`,
     `intake_::run`) where you can.
   - `driveSpeed()` returns a `DoubleSupplier` that reads the stick *every time
     it's asked*: flip the sign of `controller_.getLeftY()` (forward is
     negative on a real controller) and pass it through `stick_filter_`.
6. **Run it.** `Main` tries your filters, then plays a few loops of a pretend
   match and prints what the robot does.

### New Java in this lesson

- `@FunctionalInterface`: an interface with exactly one method
- **lambdas**: `speed -> speed * 0.5`, `() -> intake_.stop()`, and bigger ones
  with a `{ ... return ...; }` body
- Java's built-in functional interfaces in `java.util.function`:
  - `Runnable`: nothing in, nothing out (`run()`)
  - `BooleanSupplier`, `DoubleSupplier`, `Supplier<T>`: nothing in, a value out
    (`getAsBoolean()`, `getAsDouble()`, `get()`)
  - `Consumer<T>`: a value in, nothing out (`accept(x)`)
  - `Function<T, R>` and `DoubleUnaryOperator`: a value in, a value out
    (`apply(x)`, `applyAsDouble(x)`)
- **method references**: `intake_::run` means `() -> intake_.run()`, and
  `Math::abs` means `x -> Math.abs(x)`
- `ArrayList`, used a little: an array that grows with `add()`

## Why this matters on the robot

This is exactly how real robot code binds buttons and reads joysticks. In
the team's code you'll see lines like these:

```java
driver_controller_.a().onTrue(Commands.runOnce(() -> robot.setWantedState(RobotStates.INTAKE)));

return MathUtil.applyDeadband(-driver_controller_.getLeftY(), 0.1);
```

The first one doesn't set the state *now*. It hands the A button a piece of
behavior to run *later*, whenever A is pressed. The second is a deadband on
the stick, just like your `Filters.deadband`, and the drive code asks for it
through a supplier so it gets a fresh reading every loop instead of the one
value the stick had when the robot turned on. After this lesson you've built
the machinery that runs these.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
