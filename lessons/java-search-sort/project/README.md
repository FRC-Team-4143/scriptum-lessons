# Searching & Sorting

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Searching & Sorting](https://frc-team-4143.github.io/docs/software/java/advanced/search-sort).

Two jobs come up all the time in a scouting app: **finding** a team in a list
("where is 4143 in the pit map?") and **sorting** a list ("put the teams in
order, best first, for alliance selection").

Searching an unsorted list means checking items one by one: O(n), as you saw
in the Big-O lesson. But once a list is **sorted**, you can do far better.
Look at the middle item. If it's too small, the team you want must be in the
right half, so throw the left half away. Every look halves what's left, so a
list of 1000 teams takes about 10 looks. That's **binary search**, and it is
O(log n).

Then you'll sort teams by their scouting score: first with a sort you write
yourself, so you see how it works, and then the way real code does it, with
Java's `List.sort` and a **`Comparator`**.

## What you need to do

Team standard: fields use `trailing_snake_case_`.

1. **Linear search: `Search.indexOf(teams, team)`** in `src/Search.java`.
   Return the index of the *first* place `team` appears, or `-1` if it isn't
   in the list. Check items from the front and stop as soon as you find it.
2. **Binary search: `Search.binarySearch(sorted, team)`**. The list holds
   different team numbers, smallest first. Return the index of `team`, or `-1`.
   - Keep two indexes, `low` and `high`, for the part of the list the team
     could still be in. Start with the whole list.
   - While there's anything left between them, look at the item in the
     middle. Found it? Return its index. Too small? Move `low` past it. Too
     big? Move `high` before it.
   - When `low` passes `high`, the team isn't there.
   - Write the loop yourself: don't use `Collections.binarySearch`.
3. **Sort by hand: `Sorting.sortByScore(teams)`** in `src/Sorting.java`.
   Sort the array of `TeamScore`s in place, highest score first. Use a
   **selection sort** (find the highest of what's left, swap it to the front,
   repeat) or an **insertion sort** (take each team and slide it left until
   it's in the right spot). Don't use `Arrays.sort` or `List.sort` here.
   Teams with the same score can end up in either order.
4. **Rank with a Comparator: `Sorting.rankings(teams)`**. Return a *new* list
   of the same teams, highest score first. When two teams tie, the lower team
   number goes first. Copy the list, then call `sort` on the copy with a
   `Comparator`. Don't change the list you were given.
5. **Run it.** `Main` searches a pit map of 1024 teams both ways and prints how
   many looks each took, then prints both sorted lists.

The checker counts how many items your searches look at, on lists much bigger
than the ones in `Main`. A binary search that secretly checks every item gives
the right answer but still fails.

### New Java in this lesson

- **linear search** and **binary search**
- **selection sort** and **insertion sort**
- swapping two items in an array with a temporary variable
- `Comparator`: an object that says which of two items comes first.
  `Comparator.comparingInt(TeamScore::getScore)` compares by score;
  `.reversed()` flips it to highest first; `.thenComparingInt(...)` breaks ties
- `list.sort(comparator)` and `new ArrayList<>(list)` to make a copy

## Why this matters on the robot

Robot code searches and sorts all the time. A shooter's lookup table is
sorted by distance, so the code can binary-search for the two nearest
entries and blend between them. Vision code sorts the AprilTags it sees by
how close or how trusted they are and uses the best one. And at a
competition, the pick list is just your scouting data sorted with a
comparator your strategy team agreed on.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
