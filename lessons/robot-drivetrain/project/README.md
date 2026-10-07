# Motors and Drivetrains

Make a robot drive. You will read the controller sticks and turn them into motor commands for a
**differential drive** (tank-style) robot, all running in simulation. This is also your first look at
**Java**: you will write real code, and each new idea is explained right where you first use it.

Companion docs page: [Motors and Drivetrains](https://docs.marswars.org/docs/software/training/motors-drivetrains)

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

## How this project is set up

Your robot code uses **MWLib**, the MARS/WARS robot library. It already knows how to talk to the
brushless motors, read their sensors and simulate the robot, so you never program a motor
directly. Instead you tell the drivetrain what you want.

- `Robot.java` is **your code**. Everything you write goes in `teleopPeriodic()`.
- `Constants.java` lists the robot's motors (one on each side) and measurements.
- The drivetrain is the `drive` object in `Robot.java`. The one thing you need from it is
  `drive.setDutyCycles(left, right)`.
- The controller is the `controller` object. It can tell you how far each stick is pushed.

## Running it

Click **Start** in the Scriptum Driver Station, choose **Teleop** and click **Enable**. Drive with
a gamepad, or with the keyboard: **W / S** is the left stick up and down, **Arrow Up / Arrow Down**
is the right stick up and down, and **Arrow Left / Arrow Right** is the right stick left and right.
Open AdvantageScope to watch `Drive/LeftOutput` and `Drive/RightOutput`.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Tour the Driver Station and AdvantageScope, run the starter, read the project layout |
| 15 | Read "Java you will learn" and try the examples out loud |
| 35 | Step 1: tank drive |
| 40 | Step 2: arcade drive |
| 25 | Look at the AdvantageScope graphs while you drive: what do the left and right outputs do when you turn? |
| 15 | Step 3: Verify |
| 30 | Bonus challenges (do these after Verify) |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

Everything here is code **you write** in `teleopPeriodic()`. Run it after each step.

1. **Tank drive.** Declare `double leftSpeed` from the left stick (`controller.getLeftY()`) and
   `double rightSpeed` from the right stick (`controller.getRightY()`), each with a minus sign in
   front. Finish with `drive.setDutyCycles(leftSpeed, rightSpeed);`.
   Run it: **W / S** drives the left side, **Arrow Up / Down** the right side.
2. **Arcade drive.** Replace your code so that you:
   - declare `forward` from the **left stick Y** (still with the minus sign),
   - declare `turn` from the **right stick X** (`controller.getRightX()`),
   - set `leftSpeed = forward + turn` and `rightSpeed = forward - turn`,
   - call `drive.setDutyCycles(leftSpeed, rightSpeed);`.

   Run it: **W / S** drive, **Arrow Left / Right** turn. If it turns the wrong way, check your signs.
3. **Click Verify** to check your work.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.

## Bonus challenges

- **Speed limit.** Multiply `forward` by `0.5` so the robot never goes faster than half speed. Which
  operator do you use?
- **Backwards robot.** Remove the minus sign from `forward`. What changes? Put it back.
- **Squared sticks.** Make small stick movements gentler by multiplying `forward` by its own
  absolute value: `forward = forward * Math.abs(forward);`. (`Math.abs(x)` is a method that gives the
  size of `x` without its sign.)

