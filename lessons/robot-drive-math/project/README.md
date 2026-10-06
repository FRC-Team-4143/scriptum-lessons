# Drive Math and Methods

Last lesson you wrote the same math inside one long `robotPeriodic()`. In this lesson you will
move that math into **methods**: small named pieces of code that do one job and can be reused. When
you are done, `Robot.java` reads almost like a sentence.

Companion docs page: [Drive Math and Methods](https://docs.marswars.org/docs/software/training/drive-math-methods)

## Java you will learn in this lesson

**Methods: giving a job a name.** You have been *calling* methods (`controller.getLeftY()`). Now you will
*write* them. A method is a named block of code that takes some information in and (usually) gives an
answer back:

```java
public static double average(double a, double b) {
  return (a + b) / 2.0;
}
```

Read it from left to right: `public static` (for now, always write these two words), `double` is the
**type of the answer** (the return type), `average` is the name, and `double a, double b` are the
**parameters**: the information the caller must provide. Inside the braces, `return` hands back the
answer. You call it like this:

```java
double middle = average(2.0, 4.0);   // middle is now 3.0
```

A method that gives nothing back is `void`, like `drive.setDutyCycles(0.0, 0.0)`. Why bother? A method
lets you name a job, use it from many places, and test it on its own. You can even build one method
out of another one instead of copying and pasting.

**Arrays: a numbered list of values.** A method can only return one thing, so to return *two* numbers
(a left speed and a right speed) we return an **array**:

```java
double[] speeds = new double[2];   // room for two doubles
speeds[0] = 0.5;                   // the first slot is number 0, not 1
speeds[1] = -0.5;
double first = speeds[0];
int howMany = speeds.length;       // 2
double[] sameThing = {0.5, -0.5};  // a shortcut for making and filling one
```

Remember: counting starts at **0**.

**Loops: doing something once for every item.** A `for` loop repeats code. Its three parts say where to
start, when to keep going, and how to count:

```java
double total = 0.0;
for (int i = 0; i < values.length; i++) {
  total += values[i];     // add the item in slot i to the total
}
```

This runs once with `i = 0`, then `i = 1`, and so on, until `i` reaches `values.length`. `i++` means
"add one to `i`" and `total += x` means "add `x` to `total`". You use a loop instead of writing the same
line over and over. The `%` operator gives the remainder after dividing, so `7 % 5` is `2`. It is how
code "wraps around" an array.

## Where the code lives

- `DriveMath.java` is **pure math**. Numbers go in, a number comes out. It never touches a motor
  or a sensor, so it is easy to test. You write five methods here.
- `mechanisms/DifferentialDriveMech.java` is the drivetrain. You write three more methods in it:
  `setLeftDutyCycle`, `setRightDutyCycle` and `arcadeDrive`. It also now has readings in meters and
  meters per second (`getLeftMeters()`, `getLinearSpeed()` and so on). Those are already written, and
  they call your `DriveMath` methods.
- `Robot.java` is already finished. It **calls** your methods, so read it to see how they are used.
  Until you write the methods, the robot sits still and logs zeros.

## Session plan (about 3 hours)

A suggested pace that adds up to the 3 hour session. Take short breaks whenever the class needs one.

| Minutes | What to do |
| --- | --- |
| 20 | Read about methods, arrays and loops |
| 30 | DriveMath: rotationsToMeters, linearSpeed, angularSpeed |
| 30 | DriveMath.arcadeToWheelSpeeds |
| 25 | DriveMath.average |
| 30 | The mech: setLeft, setRight, arcadeDrive |
| 15 | Read "How the loop works" and test-drive |
| 15 | Verify |
| 15 | Bonus challenges |

Go slower on anything that is new. Finishing every bonus is not expected.

## What you need to do

Work through the methods one at a time. After each, click **Verify** to test it.

1. **`DriveMath.rotationsToMeters`.** Wheel rotations times the wheel circumference
   (`2 * Math.PI * radius`).
2. **`DriveMath.linearSpeed`.** The average of the left and right speeds.
3. **`DriveMath.angularSpeed`.** `(right - left) / trackWidth`.
4. **`DriveMath.arcadeToWheelSpeeds`.** Return `{left, right}` where `left = forward + turn` and
   `right = forward - turn`. If either number ends up above `1.0` or below `-1.0`, divide both by
   whichever is bigger, so the motors do not clip. (Why divide *both*? Think about what would
   happen to the turn if you only fixed one side.)
5. **`DriveMath.average`.** Takes an array of numbers and returns their average. Add them up with a `for`
   loop (see above), then divide by how many there are. `Robot.java` already keeps the last 5 speeds in an
   array and logs `Robot/SmoothedSpeed`, which uses your `average`. Compare it with `Robot/LinearSpeed` in
   AdvantageScope. Which one is calmer?
6. **`setLeftDutyCycle` and `setRightDutyCycle`** in the drivetrain. Look at `setDutyCycles` right
   above them: it already shows how to store a clamped value in `leftRequest.Output`.
7. **`arcadeDrive`** in the drivetrain. Call your `DriveMath.arcadeToWheelSpeeds` method and give
   the result to `setDutyCycles`.

Now **Start** the robot, enable Teleop, and drive: the left stick goes forward and back and the right
stick turns. Run Auto to see the robot drive 2 meters, built from your methods.

## How the loop works (read, don't write)

You never call `readInputs` or `writeOutputs` on the drivetrain. MWLib does it for you, every 20 ms:

1. **Read inputs:** the drivetrain asks its motors for position and velocity.
2. **Your code runs:** `robotPeriodic()`, `teleopPeriodic()` and so on say what you WANT.
3. **Write outputs:** the drivetrain sends your request to the motors.

Open `mechanisms/DifferentialDriveMech.java` and find these three steps (`readInputs`,
`writeOutputs`, `logData`). This is called an **IO model**: inputs in, outputs out,
and your logic in the middle.

## Bonus challenges

- Add `metersToRotations(meters, radius)` to `DriveMath` (the opposite of step 1).
- Add a **slow mode** to `teleopPeriodic()`: multiply forward by `0.5` while a bumper is held.
- Make the arcade turn gentler near the middle of the stick by **squaring** the stick value but
  keeping its sign. Hint: `Math.copySign(x * x, x)`.

## Words to know

- **Method:** a named block of code. It can take **parameters** and **return** a value.
- **Pure function:** a method whose answer depends only on its inputs.
- **Reuse:** write it once, call it from everywhere.
