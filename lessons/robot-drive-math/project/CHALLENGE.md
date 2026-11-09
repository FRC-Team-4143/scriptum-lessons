# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Smooth launch

Slam a stick forward and the real robot jerks: its wheels slip, and the battery voltage dips. Teams limit how fast a command can change. Add this method to `DriveMath`:

```java
public static double[] smoothStick(double[] targets, double speedUpStep, double slowDownStep)
```

`targets` is the stick value for each loop. Return a **new** array of the same length with the smoothed
value for each loop, starting from `0.0`:

1. Moving **away from zero** changes by at most `speedUpStep` per loop.
2. Moving **toward zero** changes by at most `slowDownStep` per loop.
3. Going from forward to backward (or the other way), **stop on exactly zero first**, even if the
   step could have carried past it.
4. Land exactly on a target that is closer than one step.

| `targets` | `speedUpStep` | `slowDownStep` | Result |
| --- | --- | --- | --- |
| `{1, 1, 1}` | 0.25 | 0.5 | `{0.25, 0.5, 0.75}` |
| `{1, 0, 0}` | 1.0 | 0.25 | `{1, 0.75, 0.5}` |
| `{0.5, -1, -1, -1, -1}` | 0.5 | 0.3 | `{0.5, 0.2, 0, -0.5, -1}` |

Write out the third row by hand before you code. Then, if you like, use the same idea on the real robot:
keep the last value in a field and smooth `forward` in `teleopPeriodic()`.

**How it's checked:** Verify calls `smoothStick` with inputs you haven't seen, including negatives, an empty array and exact boundaries. No simulator needed.
