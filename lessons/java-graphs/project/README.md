# Graphs

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Graphs](https://frc-team-4143.github.io/docs/software/java/advanced/graphs).

In autonomous, the robot drives from spot to spot: the start line, a lane,
the reef, the coral station. Some spots are joined by a clear lane, and some
aren't (a wall or a field element is in the way). How does the robot work out
the best way from here to there?

A map like that is called a **graph**. Each spot is a **node** (here, a
*waypoint*), and each lane between two spots is an **edge**. In a tree,
every node has exactly one parent. A graph has no such rule: a waypoint can
join any number of others, and lanes can go round in a loop.

You'll store the field as an **adjacency list**: a `Map` from each
waypoint's name to the `List` of waypoints one lane away (its
**neighbors**). Then you'll search it.

```java
"Start"  -> [Left Lane, Center, Right Lane]
"Center" -> [Start, Reef]
```

## What you need to do

Team standard: fields use `trailing_snake_case_`.

1. **Build the map in `src/FieldMap.java`.** The field `neighbors_` is the
   adjacency list.
   - `addWaypoint(name)` puts a waypoint on the map with an empty list. If
     it's already there, leave it alone (don't lose its lanes).
   - `connect(a, b)` joins two waypoints. A lane goes **both ways**, so `b`
     goes in `a`'s list *and* `a` goes in `b`'s list. Add either waypoint if
     it's new. Connecting the same two twice must not list them twice.
   - `neighbors(name)` returns the waypoint's list, or an **empty list** (not
     `null`) for a waypoint that isn't on the map.
2. **Write `PathFinder.canReach(field, from, to)`** in `src/PathFinder.java`.
   It says whether the robot can drive from `from` to `to`. Start at `from`,
   then keep visiting neighbors of places you've reached. Keep a `Set` of
   waypoints you've already visited and skip them, or a loop of lanes will
   send your search round and round forever.
   - A waypoint can always reach itself.
   - A waypoint that isn't on the map can't reach anything, or be reached.
3. **Write `PathFinder.shortestPath(field, from, to)`.** It returns the path
   with the **fewest hops** (lanes driven), as a `List` that starts with
   `from` and ends with `to`. Use a **breadth-first search**: a queue, plus a
   `Map` that remembers which waypoint you came from to reach each one. When
   you reach `to`, follow those "came from" links back to `from` to build the
   path.
   - From a waypoint to itself: a list with just that waypoint.
   - No path, or a waypoint that isn't on the map: an empty list.
   - If two paths tie for fewest hops, either one is fine.
4. **Write `PathFinder.unreachable(field, start)`.** It returns every
   waypoint on the map the robot *can't* get to from `start`, sorted
   alphabetically. Search once from `start`, then list the waypoints your
   search never visited. If `start` isn't on the map, nothing is reachable.
5. **Run it.** `Main` builds a small field and prints what your code finds.

### New Java in this lesson

- a **graph** stored as an adjacency list: `Map<String, List<String>>`
- `map.containsKey(key)` and `map.getOrDefault(key, fallback)`
- `Set<String>` and `HashSet`, to remember where you've been
- `Deque<String>` and `ArrayDeque`, which work as a **stack** (`push`,
  `pop`: last in, first out) or a **queue** (`addLast`, `removeFirst`:
  first in, first out)
- **depth-first search** (go as deep as you can, then back up) and
  **breadth-first search** (try everything 1 hop away, then 2, then 3...)
- `Collections.sort(list)`

## Why this matters on the robot

Path planners on real robots are graph searches. A planner splits the
field into a grid of small squares, treats each open square as a node and
each step between squares as an edge, and searches for the best way around
the field elements. It's a bigger cousin of the breadth-first search you
write here, one that also counts distances and guesses which way is
closer (it's called **A\***, "A star"). Graphs also show up in
state machines, where each state is a node and each allowed change is an
edge.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
