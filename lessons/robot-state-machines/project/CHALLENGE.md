# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Clear a jam

Game pieces jam. Add a sequence the driver starts with **one tap**, that finishes by itself. It's made of
states, so it's written once and an auto could use it too.

1. **Shooter states.** Add `SPIN_DOWN` (flywheel off, roller still) and `UNJAM` (flywheel off, roller
   backward at about `-0.5`). Asking the shooter for `UNJAM` always goes through `SPIN_DOWN`. It stays there until
   the flywheel is under `100` rad/s, because a roller running backward into a fast wheel is how parts break. It stays in
   `UNJAM` for `0.5` seconds (a `Timer`), then goes to `IDLE`.
2. **A robot state.** Add `UNJAM` to `RobotStates`. Asking for it must not be cancelled partway: once it starts, it
   finishes, even if the driver lets go of the right bumper (which asks for `DRIVE`). Then the robot goes
   back to `DRIVE` on its own. You will need to override `handleStateTransition` in `RobotContainer`, and give the shooter a way to say it's done.
3. **The button.** **Y** (`I` on the keyboard), with `onTrue`.

Follow the team's rule: every condition goes in `handleStateTransition`, and `updateLogic` is a plain `switch`
that says what each state does. The state names must be exactly `SPIN_DOWN` and `UNJAM`.

**How it's checked:** with the simulator running, hold **U** until the flywheel is at speed, tap **I**, and let
go of everything. Watch `Robot/State` and `Subsystem/Shooter/State`. `Check/Challenge/UnjamStep` says how far you got
(5 is all the way), `UnjamSeconds` how long the roller reversed, and `UnjamMistakes` how often the roller ran
backward with the flywheel still fast. Press Start to clear a mistake.
