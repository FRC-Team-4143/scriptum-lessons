# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Two-cycle auto

One game piece is a good auto. Two is a great one. Make a new tree that scores, goes back for a second game piece, and scores again.

1. **A third path, `BackToPickup`.** A pose waypoint on ScoreSpot with heading **-2.761 rad** (that's the way the
   robot faces after aiming at the goal, not the 1.5708 it arrived with), a translation waypoint at (6.0, 3.8), and
   a pose waypoint on Pickup with heading 0 rad. Add a Max Velocity of 1.8 m/s and Generate.
2. **A new tree, `TwoCycle`**, saved as `deploy/autos/trees/TwoCycle.xml`. Leave `PickupAndScore` as it is: its checkpoint still reads it. You've used every step you need, and `ToScore` can be driven twice.
3. **Pick it** in the **Auto Chooser**, then run it on a **Blue** alliance.

**The clock.** A real match gives 15 seconds, but this practice robot settles fully at the end of each path, about 2
seconds a time, so the limit here is **25 seconds** to the start of the second shot. Think about what's worth waiting for:
every `Sleep` costs you, but a `SHOOT` needs the flywheel at speed before it counts as a shot.

**How it's checked:** run `TwoCycle` with the simulator running. Using the robot's true position, the
check wants, in order: Pickup, ScoreSpot, an aimed shot, Pickup, ScoreSpot, an aimed shot (each within 0.4 meters
and 0.087 rad). `Check/Challenge/TwoCycleStep` says how far you got (6 is all of them) and `TwoCycleSeconds` how long it took. If the second path goes wrong, watch the tree in Arbor and look for the red step.
