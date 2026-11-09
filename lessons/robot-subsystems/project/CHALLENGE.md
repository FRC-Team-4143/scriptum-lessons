# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Intake and safe feed

Build a new subsystem by yourself, then make two subsystems work together. Pushing a game piece into a slow flywheel makes a weak shot, so:

1. **`IntakeSubsystem`.** In a new `subsystems/intake/` folder, with its own `IntakeConstants`: one `RollerMech`
   on CAN id 22. Use `ShooterSubsystem` as your pattern. Log its duty cycle as
   `getSubsystemKey() + "RollerDuty"`, and register it in `RobotContainer`.
2. **The B button** (`L` on the keyboard). Add `OI.getIntakeButton()`. The intake runs at about `0.6` while it's held.
3. **Safe feed.** Add a constant `FEED_MIN_VELOCITY` of `250.0`. The shooter's roller now runs only while the
   index button is held **and** the flywheel is at least that fast.
4. **Take turns.** Add a `public boolean isFeeding()` to `ShooterSubsystem`. The intake stops while the
   shooter is feeding, even with B held.

**How it's checked:** with the simulator running, Verify looks for each rule working for a moment, and for
no mistakes since you pressed Start. To do it: hold **E** alone (the roller must stay still). Hold **L** alone (the
intake runs). Hold **U** until the flywheel is fast, then add **E** (it feeds), then add **L** (the intake
stops). `Check/Challenge/SafeFeedSeen` counts the rules seen (4 is all of them), and `SafeFeedMistakes` counts
mistakes: press Start to clear them. The **Roller runs** checkpoint now needs **U** held as well.
