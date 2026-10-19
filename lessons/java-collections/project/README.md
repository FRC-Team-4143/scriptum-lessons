# Collections

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Collections](https://frc-team-4143.github.io/docs/software/java/advanced/collections).

At a competition, our scouts write down every match: which team played and how
many points it scored. By the end of the day that is hundreds of numbers. In
this lesson you write the code that keeps track of them.

An array has a fixed size, chosen when you make it. Scouting data keeps
growing, so you need something that grows too. Java's **collections** do that.
This lesson uses the three you will see most:

| Collection | What it holds | Use it when |
| --- | --- | --- |
| `ArrayList` | items in order, repeats allowed | the order matters, like scores match by match |
| `HashSet` | items with no repeats, no order | you only care *whether* something is there |
| `HashMap` | pairs: a **key** and its **value** | you look things up by a key, like team number → points |

## What you need to do

`src/Main.java` is already written. It feeds nine scouted matches into your
three classes and prints what they found, so click **Run** any time. Team
standard: fields use `trailing_snake_case_`.

1. **`ScoreList`** (`src/ScoreList.java`): one team's scores, match by match.
   - Add `import java.util.ArrayList;` at the top of the file.
   - Add the field `protected ArrayList<Integer> scores_ = new ArrayList<>();`
   - `addScore(int score)` adds the score to the end of the list.
   - `count()` returns how many scores there are.
   - `average()` returns the average score, or `0.0` when there are none.
     Careful: an `int` divided by an `int` drops the decimals, so make one of
     them a `double`.
   - `best()` returns the highest score, or `0` when there are none.
2. **`TeamsSeen`** (`src/TeamsSeen.java`): every team the scouts saw, once each.
   - Import `java.util.HashSet` and add the field
     `protected HashSet<Integer> teams_ = new HashSet<>();`
   - `addTeam(int team)` adds the team. Adding it again changes nothing.
   - `hasSeen(int team)` returns `true` if that team was added.
   - `countTeams()` returns how many *different* teams were added.
3. **`TeamTotals`** (`src/TeamTotals.java`): total points for each team.
   - Import `java.util.HashMap` and add the field
     `protected HashMap<Integer, Integer> totals_ = new HashMap<>();`
     The key is the team number and the value is that team's total.
   - `addMatch(int team, int points)` adds `points` to the team's total. The
     first time a team shows up, its total starts at `0`.
   - `totalFor(int team)` returns the team's total, or `0` for a team with no
     matches yet.
4. **`TeamTotals.topTeam()`**: the team with the highest total. If two teams
   tie, return the lower team number. Return `-1` if no matches were added.
   Loop over the map's keys with `for (int team : totals_.keySet())`. A
   `HashMap` doesn't keep things in any order, so the tie rule is how you get
   the same answer every time.
5. **Run it.** `Main` prints our team's average and best score, how many
   teams were scouted, and the top team.

### New Java in this lesson

- `import java.util.ArrayList;` brings in a class from Java's library. You can
  bring in the whole package at once with `import java.util.*;`
- `ArrayList<Integer>`: the type in the angle brackets says what the list
  holds. Collections hold objects, not plain `int`s, so you write `Integer`,
  the object version of `int`. Java turns an `int` into an `Integer` and back
  for you (this is called **boxing**).
- `new ArrayList<>()`: the empty `<>` means "the same type as the variable".
- list methods: `add`, `get`, `size`, `isEmpty`, `contains`, `remove`
- set methods: `add`, `contains`, `size`
- map methods: `put`, `get`, `getOrDefault`, `containsKey`, `keySet`
- a **for-each loop** over a collection: `for (int score : scores_)`

## Why this matters on the robot

Robot code uses all three. A list holds the subsystems the robot updates
every 20 milliseconds, in order. A set holds the AprilTag ids the camera saw
this frame, since a tag seen twice is still one tag. A map is a shooter's
lookup table: distance to the goal → flywheel speed.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
