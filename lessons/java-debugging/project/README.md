# Debugging

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

Code that compiles isn't the same as code that works. This program reads the log from a
practice match and prints a summary. It compiles, it runs, and it's wrong. Every method under
`src/` (apart from `MatchLog` and `Main`) has one bug, marked with a `BUG` comment that
names its checkpoint.

Reading code and guessing only gets you so far. A **debugger** lets you freeze the program on any
line and look inside: what's in every variable, which method called which, and what happens one
line at a time. In this lesson you'll use it to find all seven bugs.

## Run it first

Open `src/Main.java` and click the **Run** button, the little triangle (▷) at the top right of the file. You'll see:

```text
Average speed: 1.75 m/s
Shot accuracy: 0.0%
Auto distance: 0.0 m
Lowest battery: -0.001 V
Seconds intaking: 0
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: ...
```

When everything is fixed, it should print:

```text
Average speed: 1.8125 m/s
Shot accuracy: 87.5%
Auto distance: 7.0 m
Lowest battery: 10.79 V
Seconds intaking: 3
Points scored: 20
Good range readings: 4
```

## Debugger basics

- **Breakpoint:** click just left of a line number and a red dot appears. When the program
  reaches that line, it stops *before* running it.
- **Debug, not Run:** start the program with **Debug**, either the small **Debug** link just
  above `public static void main`, or the arrow next to ▷ at the top right, then **Debug Java**.
  Breakpoints only work when you start the program this way.
- **The toolbar** appears at the top while you're debugging:
  **Continue** runs to the next breakpoint, **Step Over** runs one line,
  **Step Into** goes inside the method on this line, **Step Out** finishes the method you're in,
  and **Stop** ends the program.
- **Variables** (in the **Run and Debug** panel on the left) shows every variable right now.
  You can also hover over a variable in the code to see its value.
- **Use the toolbar buttons, not the F-keys.** The keys you'd use in a desktop editor
  (F5, F10, F11) belong to your browser here: F5 reloads the page and F11 goes full screen.

## What you need to do

Fix each bug so the method does what its comment says. Don't change `MatchLog.java`. Fix the
bugs in order: the crash in step 6 and the stuck loop in step 7 stop `Main` before it reaches the end.

1. **Your first breakpoint: `SpeedMath.averageSpeed`.** The average is too low. Put a breakpoint
   on the `sum += speeds[i];` line and start **Debug**. Each time it stops, look at `i` in
   Variables and press **Continue**. This step has two checkpoints:
   - **Count the stops:** *before you fix anything,* write how many times it stopped in
     `Answers.LOOP_RUNS_BEFORE_FIX`.
   - **Fix the average:** then fix the loop so it adds every speed.
2. **Step into a method: `Accuracy.percent`.** It always says 0.0. Put a breakpoint on the
   `Shot accuracy` line in `Main`, debug, then press **Step Into** to go inside `percent`.
   What does `made / attempted` come out as?
3. **Step out again: `Odometry.totalDistance`.** It always says 0.0. Step into `addLeg`, watch
   `total` grow, then press **Step Out**. Look at `total` in `totalDistance`: what happened?
   (Hint: a method gets a *copy* of each number you pass it.)
4. **A conditional breakpoint: `Battery.lowestVolts`.** It reports a negative voltage, because one of the
   500 readings is `-1`, meaning the sensor missed that tick. Pressing **Continue** 500 times
   would take forever. Right-click your breakpoint, choose **Edit Breakpoint**, and give it the
   condition `millivolts[i] < 0`, so it stops *only* on the bad reading. Two checkpoints:
   - **Find the missing reading:** write that `i` in `Answers.MISSING_SAMPLE_INDEX`.
   - **Skip the missing readings:** make `lowestVolts` skip readings below 0.
5. **Watching a comparison: `StateLog.countState`.** The log has three `"INTAKE"`s, but it counts 0.
   Stop inside the loop. In the **Watch** section, add `states[i]`, `wanted` and
   `states[i] == wanted`. They look the same, so why is it `false`? (For strings, `==` asks
   "is this the *same object*?", and `.equals` asks "do they say the same thing?")
6. **Read the stack trace: `Scoring`.** The program crashes. Read the red text from the top: the
   exception, then each `at ...` line, from where it crashed back to `main`. Click a line to jump
   there. Fix the real cause, not the symptom: no `try`/`catch` that hides it.
7. **Pause a stuck program: `Cleaner.countValid`.** Now `Main` never finishes. Debug it, wait a
   second, and press **Pause**. The debugger shows exactly which line it's stuck on. Step a
   few times and watch `i`. Why does it never move?

Printing values with `System.out.println` is debugging too, and it's fine to use. But try the
debugger for each step. It's much faster once you're used to it.

### New in this lesson

- breakpoints, conditional breakpoints, and the Debug button
- Step Over, Step Into, Step Out, Continue, Pause, Stop
- the Variables, Watch and Call Stack panes
- reading a stack trace
- `.equals` for comparing `String`s

## Why this matters on the robot

Most robot bugs compile fine: the arm goes the wrong way, or a state machine never leaves
a state. A robot loop runs 50 times a second, so a `println` floods the console with
thousands of lines. With the robot simulator you can attach this same debugger, stop on the
exact loop where things go wrong, and look at every value. (Never pause a real robot while its
motors are running.)

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
