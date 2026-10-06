# Motors and Drivetrains

Make a robot drive. You will read the controller sticks and turn them into motor commands for a
**differential drive** (tank-style) robot, all running in simulation. This is also your first look at
**Java**: you will write real code, and each new idea is explained right where you first use it.

Companion docs page: [Motors and Drivetrains](https://docs.marswars.org/docs/software/training/motors-drivetrains)

## How this project is set up

Your robot code uses **MWLib**, the MARS/WARS robot library. It already knows how to talk to the
brushless motors, read their sensors and simulate the robot, so you never program a motor
directly. Instead you tell the drivetrain what you want.

- `Robot.java` is **your code**. Everything you write goes in `teleopPeriodic()`.
- `Constants.java` lists the robot's motors (one on each side) and measurements.
- The drivetrain is the `drive` object in `Robot.java`. The one thing you need from it is
  `drive.setDutyCycles(left, right)`.
- The controller is the `controller` object. It can tell you how far each stick is pushed.

## Java you will learn in this lesson

**1. Methods are things you can ask an object to do.** You *call* a method by writing the object's
name, a dot, the method's name, and parentheses:

```java
controller.getLeftY()
```

asks the controller "how far is the left stick pushed up or down?" The answer is a number between
`-1.0` and `1.0`. Some methods need information, which goes inside the parentheses:

```java
drive.setDutyCycles(0.5, 0.5)
```

tells the drive to run both sides at half power. Every statement ends with a semicolon (`;`).
`//` starts a comment: a note for people that Java ignores.

**2. Variables are named boxes that hold a value.** To make one, write its *type*, a name, `=`, and a
value:

```java
double speed = 0.5;
```

`double` is the type for numbers with a decimal point. After that, you can use `speed` anywhere you
could use `0.5`. You can store the answer from a method call in a variable too:

```java
double stickUp = controller.getLeftY();
```

**3. Operators do math.** `+`, `-`, `*` and `/` add, subtract, multiply and divide. A minus sign in
front of a value flips its sign: `-speed` is the opposite of `speed`.

`teleopPeriodic()` runs about 50 times a second while the robot is enabled in teleop. Each time, it
runs your lines from top to bottom.

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

1. **Tank drive.** Declare two `double` variables, `leftSpeed` and `rightSpeed`. Set `leftSpeed` from
   the left stick (`controller.getLeftY()`) and `rightSpeed` from the right stick
   (`controller.getRightY()`). Pushing a stick forward gives a **negative** number, so put a minus sign
   in front to flip it. Finish with `drive.setDutyCycles(leftSpeed, rightSpeed);`.
   Run it: **W / S** moves the left side and **Arrow Up / Arrow Down** moves the right side.
   Push one side forward and one back to spin in place.
2. **Switch to arcade drive.** Tank drive uses two sticks. **Arcade drive** uses one stick to go forward
   and another to turn, which is how most drivers prefer it. Replace your code so that you:
   - declare `forward` and set it from the **left stick Y** (still with the minus sign),
   - declare `turn` and set it from the **right stick X** (`controller.getRightX()`),
   - declare `leftSpeed` and `rightSpeed` using arithmetic: `leftSpeed = forward + turn` and
     `rightSpeed = forward - turn`,
   - call `drive.setDutyCycles(leftSpeed, rightSpeed);`.

   Run it. **W** drives forward, **S** drives backward, and **Arrow Left / Right** spin the robot in
   place. If it turns the wrong way, check your signs. (The motors cap at `-1.0` to `1.0`, so very big
   sticks will clip.)
3. **Click Verify** to check your work.

## Bonus challenges

- **Speed limit.** Multiply `forward` by `0.5` so the robot never goes faster than half speed. Which
  operator do you use?
- **Backwards robot.** Remove the minus sign from `forward`. What changes? Put it back.
- **Squared sticks.** Make small stick movements gentler by multiplying `forward` by its own
  absolute value: `forward = forward * Math.abs(forward);`. (`Math.abs(x)` is a method that gives the
  size of `x` without its sign.)

## Words to know

- **Differential drive:** a drivetrain with a left side and a right side, steered by running the
  sides at different speeds.
- **Duty cycle:** how much of the battery voltage the motor gets, from `-1.0` to `1.0`.
- **Method / variable / operator:** see the Java section above.
