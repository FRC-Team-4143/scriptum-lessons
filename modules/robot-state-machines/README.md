# State Machines and Commands

The shooter works, but it is clumsy: the flywheel and roller start at the same time, so game
pieces get fed into a wheel that is not up to speed yet. In this lesson you will give the shooter a
**state machine** and control it with **commands**.

Companion docs pages: [State Machines and Commands](https://docs.marswars.org/docs/software/training/state-machines-commands),
[State Machines](https://docs.marswars.org/docs/software/controls/state-machines),
[Commands](https://docs.marswars.org/docs/software/robot_dev/commands)

## The big idea

A **state machine** says: the shooter is always in exactly one **state**, and there are rules for
moving between states.

```text
        driver wants SHOOT                 flywheel at speed
  IDLE ---------------------> SPIN_UP ------------------------> SHOOT
   ^                            ^                                 |
   |       driver lets go       |      flywheel slows down        |
   +----------(any state)-------+---------------------------------+
```

The driver can only say what they **want** (`IDLE` or `SHOOT`). The subsystem decides what state it
is actually in. You cannot jump from `IDLE` straight to `SHOOT`: the flywheel has to spin up first.

A **command** is a small action. The driver's button starts a command, and the command tells the
subsystem what state is wanted.

## Java you will learn in this lesson

**Combining conditions.** `&&` means **and**, `||` means **or**, and `!` means **not**:

```java
if (velocity > low && velocity < high) { ... }   // both must be true
```

**A method that answers yes or no.** A method that returns a `boolean` (true or false) turns a messy
condition into a readable name: `if (isAtSpeed())` reads like English. `isAtSpeed()` and `hasDipped()` are
already written for you in `ShooterSubsystem.java`. Read them, then use them in your state transitions.

**Lambdas: a tiny method you hand to another method.** A command needs to be told *what to do*. The arrow
syntax writes a method with no name:

```java
() -> shooter.setWantedState(ShooterStates.SHOOT)
```

Read it as "a method that takes nothing (`()`) and does this." You are not running it yet, only handing it
over so the command can run it later.

You also use `enum` and `switch` again, this time for the shooter's states.

## Where the code lives

Everything is under `src/main/java/frc/robot/`:

- `subsystems/shooter/ShooterSubsystem.java`: the state machine.
- `subsystems/shooter/ShooterCommands.java`: the commands.
- `subsystems/shooter/ShooterConstants.java`: the states and thresholds.
- `OI.java`: connects buttons to commands.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 25 | Read about the state diagram, boolean logic and lambdas |
| 35 | Part 1: transitions |
| 30 | Part 1: what each state does |
| 25 | Part 2: commands |
| 20 | Simulate a launch |
| 20 | Bind the buttons |
| 20 | Try it and watch the states in AdvantageScope |
| 5 | Verify |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

**Part 1: the state machine**

1. **Write the transitions.** In `ShooterSubsystem.java`, fill in `handleStateTransition()`. MWLib
   calls it every loop with the state the driver wants. Set `system_state_` to the state the shooter
   is *allowed* to be in. The rules are written in the comment above the method.
2. **Write what each state does.** In `updateLogic()`, write a `switch` with a case for each state:
   `IDLE` stops everything, `SPIN_UP` runs only the flywheel, and `SHOOT` runs the flywheel and the
   roller.

**Part 2: commands**

3. **Write the shoot command.** In `ShooterCommands.java`, `shoot()` returns a command that sets
   the wanted state to `SHOOT` when it starts and back to `IDLE` when it ends.
4. **Write the launch simulator.** In `ShooterSubsystem.java`, fill in `simulateBallLaunch()`. In
   the real world a launched game piece slows the flywheel down. In simulation the flywheel only
   knows that if you tell it. Also write the matching instant command in `ShooterCommands.java`.
5. **Bind them to buttons.** In `OI.java`, hold the **right bumper** to shoot
   (`whileTrue(...)`) and press **A** to simulate a launch (`onTrue(...)`).
6. **Try it.** Start the robot, enable Teleop, and hold the right bumper (**U** on the keyboard). Watch the `State` in
   AdvantageScope go `SPIN_UP` and then `SHOOT`. Tap **A** (**K**): the flywheel slows, the state falls
   back to `SPIN_UP`, then returns to `SHOOT` once it recovers.
7. **Click Verify.**

Next lesson you will learn how the flywheel holds its speed, and tune it yourself.

## Bonus challenges

- Add a `READY` light: log a boolean `Shooter/ReadyToShoot` that is true while the state is `SHOOT`.
- Make the driver's trigger spin the flywheel to a *different* speed for a close shot.

## Words to know

- **State machine:** a system that is always in one state, with rules for changing states.
- **Command:** a small, reusable action, started by a button or by other commands.
