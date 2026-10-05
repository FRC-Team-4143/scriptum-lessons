# Variables

:::tip[Read this first: Variables and Datatypes]

The team docs page for this lesson is the best place to learn the ideas
before you write any code: [Variables and
Datatypes](https://frc-team-4143.github.io/docs/software/java/vars-dt) -
Declaring a Variable, Assigning a Variable, Datatypes, Enums, and Constants – every section maps to something you'll write below. Come back here when
you're ready to apply it. If a step below feels unfamiliar, that page is
where to look first.

:::

All the code lives in `src/Main.java`. Click **Run** any time to see your
output printed to the terminal.

## Datatypes you'll use here

- `int` — a whole number, like `4143`.
- `double` — a decimal number, like `3.14`.
- `boolean` — either `true` or `false`.
- `String` — text, wrapped in double quotes, like `"I am a string!"`.
- `enum` — a type you define yourself, with a fixed set of named values,
  like `RED` and `BLUE`.

## Declaring vs. assigning

`int team_number;` *declares* a variable without giving it a value yet.
`team_number = 4143;` *assigns* it one. You can — and usually should — do
both on one line:
```java
int team_number = 4143;
```
A variable that's declared but never assigned is dangerous: nothing stops
other code from reading it before it has a real value. The team standard is
to always declare and assign together, so that never happens.

## Why constants?

A constant is a variable that's never meant to change, like a robot's max
speed or a wheel diameter. Adding `final` to the declaration locks it — the
compiler will stop you if you (or a teammate) accidentally try to reassign
it later. Constants are also named differently from regular variables, in
`SCREAMING_SNAKE_CASE`, so they're easy to spot at a glance:
```java
public static final double MAX_SPEED = 5.0;
```

## What you need to do

1. **Store four facts in local variables.** Inside `main`, declare one
   variable for each row below, choosing the datatype shown, and declare and
   assign on the same line. Name them whatever you like - the checkpoint
   reads what gets printed, not your variable names.

   | Datatype | Value | Printed key |
   | --- | --- | --- |
   | `int` | `4143` | `Team number` |
   | `double` | `3.14` | `Pi` |
   | `boolean` | `true` | `Robot is on` |
   | `String` | `"I am a string!"` | `Message` |

2. **Print each one** with `System.out.println`, as the key, a colon, one
   space, then the value - e.g.
   `System.out.println("Team number: " + yourVariableName);`.

   :::warning[Match the printed format exactly]

   The checkpoint looks for the exact text `key: value`. `Team number:4143`
   (missing the space) or `team number: 4143` (wrong capitalization) won't
   match, even though they print basically the same information.

   :::

3. **Add four named values to the `Main` class itself**, outside of `main`.
   The checkpoints look these up by name via reflection, so use exactly
   these names. Team standard: constants and enums are `final` /
   `SCREAMING_SNAKE_CASE`.
   - `String TEAM_NAME` - a `public static final` constant equal to
     `"Team 4143"`.
   - `double MAX_SPEED` - a `public static final` constant equal to `5.0`.
   - An `enum` named `ALLIANCE` with values `RED` and `BLUE`.
   - An `enum` named `MATCH_PERIOD` with values `AUTONOMOUS`, `TELEOP`, and
     `ENDGAME`.

   Define both enums as nested types inside `Main`, not as their own
   top-level types - they're only used by `Main`, so they belong there.
4. **Run it** and compare your output against the table, then verify.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
