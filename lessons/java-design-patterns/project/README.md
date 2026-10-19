# Design Patterns

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Design Patterns](https://frc-team-4143.github.io/docs/software/java/advanced/design-patterns).

Programmers keep running into the same problems: "I want to swap how this
works without rewriting it", "lots of things need to know when this
changes", "I need to build the right object from a name". Over the years
they found good answers and gave them names. Those answers are called
**design patterns**. A pattern isn't a library you download. It's a shape
your classes can take, built from the interfaces and lambdas you already
know.

You'll use three of them on a pretend robot:

- **Strategy**: the drivetrain can drive in tank mode or arcade mode, and
  the driver can switch mid-match.
- **Observer**: when the intake's sensor sees a game piece, the LEDs and the
  intake both hear about it.
- **Factory**: the drive team picks an auto by name on the dashboard, and the
  code builds the right one.

## What you need to do

Team standard: fields use `trailing_snake_case_`.

`Gamepad`, `WheelSpeeds`, the `DriveMode`, `PieceListener` and `Auto`
interfaces, and the four auto classes are written for you. Read them first.

1. **Write two drive modes (Strategy).** Make `TankDrive` and `ArcadeDrive`
   `implements DriveMode`, and write `getName()` and `calculate(Gamepad pad)`
   in each:
   - `TankDrive` is named `"Tank"`. The left wheels get the left stick's Y,
     and the right wheels get the right stick's Y.
   - `ArcadeDrive` is named `"Arcade"`. `forward` is the left stick's Y and
     `turn` is the right stick's X. The left wheels get `forward + turn`, the
     right wheels get `forward - turn`, and each one is **clamped** to stay
     between `-1.0` and `1.0` (a motor can't go faster than full speed).
2. **Finish `Drivetrain` (Strategy).** It keeps one `DriveMode` in `mode_`.
   - The constructor remembers the starting mode.
   - `setMode(mode)` switches to a new mode. `setMode(null)` is ignored.
   - `getModeName()` returns the current mode's name.
   - `drive(pad)` asks the current mode to `calculate` the wheel speeds, and
     returns them. It must not check *which* mode it has: no `instanceof`,
     no `if` on the name.
3. **Finish `BeamBreak` (Observer).** It keeps a list of `PieceListener`s.
   - `addListener` adds one to `listeners_`, and `removeListener` takes one
     out.
   - `update(beam_blocked)` runs every robot loop. When `beam_blocked` is
     different from `has_piece_`, set `has_piece_` to the new value **first**,
     then call `onChange(has_piece_)` on every listener, in the order they
     were added. When nothing changed, tell nobody: a sensor read 50 times a
     second shouldn't shout 50 times a second.
4. **Write `AutoFactory.create(name)` (Factory).** It builds a **new** auto
   for the name the dashboard sends:
   - `"leave"` gives `new LeaveAuto()`, `"one piece"` gives
     `new OnePieceAuto()`, and `"two piece left"` / `"two piece right"` give
     `new TwoPieceAuto("left")` / `new TwoPieceAuto("right")`.
   - Ignore upper and lower case, and spaces at either end: `"  LEAVE "`
     means `"leave"`.
   - Any other name, including `null`, gives `new DoNothingAuto()`. Never
     return `null`: a robot with no auto would crash at the start of the
     match.
5. **Run it.** `Main` drives in both modes, plays a few loops of the beam
   break, and builds three autos.

### New Java in this lesson

- **design patterns**: Strategy, Observer and Factory
- holding an object by its interface type (`DriveMode mode_`), so any class
  that implements it can be swapped in
- a `List` of listeners, and passing a **lambda** as a listener:
  `beam.addListener(has_piece -> leds.setGreen(has_piece));`
- `Math.max` and `Math.min` to clamp a number between two limits
- `name.trim()` (drop spaces at the ends) and `name.toLowerCase()`
- a `switch` on a `String`, with `case "leave":` and `default:`

### A pattern to use sparingly: Singleton

You'll also see the **Singleton** pattern in robot code: a class that only
ever has *one* object, which everything reaches through a static method like
`Field.getInstance()`. It's handy for things there really is one of, but
it's a global variable in disguise: any code anywhere can change it, and
tests can't swap it for a fake. Prefer passing objects in through
constructors, like `Drivetrain` takes its `DriveMode`. Reach for a singleton
only when there truly can only be one.

## Why this matters on the robot

All three are in real robot code. Many teams let the driver pick a drive
mode from the dashboard, which is the Strategy pattern. WPILib's buttons and
triggers are the Observer pattern: you hand them a piece of code, and they
run it when something changes. And an auto chooser on the dashboard hands
the robot a name, which a factory turns into the right autonomous routine.
Once you know the names, you'll spot these shapes all over the team's code.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
