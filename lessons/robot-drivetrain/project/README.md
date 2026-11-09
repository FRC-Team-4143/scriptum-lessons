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
- `Constants.java` lists the robot's motors (one on each side) and measurements. You change one
  thing in it in Step 2.
- The drivetrain is the `drive_` object in `Robot.java`. The one thing you need from it is
  `drive_.setDutyCycles(left, right)`.
- The controller is the `controller_` object. It can tell you how far each stick is pushed.

## Running it

Click **Start** in the Scriptum Driver Station, choose **Teleop** and click **Enable**. Drive with
a gamepad, or with the keyboard: **W / S** is the left stick up and down, **A / D** is the left stick left and right, and
**Arrow Up / Arrow Down** is the right stick up and down.
Open AdvantageScope to watch `Drive/LeftOutput` and `Drive/RightOutput`. To see the robot itself, open
the **2D Field** tab and drag `Drive/TruePose` onto the field.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Tour the Driver Station and AdvantageScope, run the starter, read the project layout |
| 15 | Read "Java you will learn" and try the examples out loud |
| 35 | Step 1: tank drive |
| 15 | Step 2: fix the right side |
| 35 | Step 3: arcade drive |
| 25 | Look at the AdvantageScope graphs while you drive: what do the left and right outputs do when you turn? |
| 15 | Step 4: Verify |
| 20 | Catch up on any step that took longer, or help a classmate |

Go slower on anything that is new.

## What you need to do

You write your code in `teleopPeriodic()`, and Step 2 changes one setting in `Constants.java`. Run it
after each step.

1. **Tank drive.** Declare `double left_speed` from the left stick (`controller_.getLeftY()`) and
   `double right_speed` from the right stick (`controller_.getRightY()`), each with a minus sign in
   front. Finish with `drive_.setDutyCycles(left_speed, right_speed);`.
   Run it: **W / S** drives the left side, **Arrow Up / Down** the right side. Now push both forward
   together (**W** and **Arrow Up**). The robot should drive straight, but it spins! Your code is not
   the problem: `Drive/LeftOutput` and `Drive/RightOutput` are both positive.
2. **Fix the right side.** The two sides of the robot are mirror images, so the right motor is mounted
   facing the other way. A motor that faces the other way needs to be **inverted**, so that positive
   still means forward. In `Constants.java`, each motor is `motor(can id, inverted)`, where `inverted`
   is `true` or `false`. Find the motor that needs inverting, change it, and run it again. Both sticks
   forward should now drive straight. `Drive/TrueSpeed` shows how fast the robot really moves forward.
3. **Arcade drive.** Replace your code so that you:
   - declare `forward` from the **left stick Y** (still with the minus sign),
   - declare `turn` from the **left stick X** (`controller_.getLeftX()`),
   - set `left_speed = forward + turn` and `right_speed = forward - turn`,
   - call `drive_.setDutyCycles(left_speed, right_speed);`.

   Run it: **W / S** drive, **A / D** turn (all on the left stick). If it turns the wrong way, check your signs.
4. **Click Verify** to check your work.

Stuck on a step? Open **Dozer**: he can explain the idea or walk you through it.
