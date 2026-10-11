# Arbor

Background: [Arbor](https://github.com/coalman321/Arbor) is the editor we build
autonomous routines in. An auto is a **behavior tree**: a small tree of steps
the robot ticks through. Arbor lets you drag the steps together, fill in their
settings, and saves the result as an XML file the robot runs. The same tool
shows the tree running on the robot, node by node.

This lesson is entirely inside the **Arbor** pane on the right - there's no
Java to write. The project is `src/main/deploy/autos`: its `trees/` folder
holds the trees, and `nodes.xml` lists the robot-specific steps you can use.

## Getting started

When Arbor opens, **Open project** and pick `src/main/deploy/autos` if it
isn't already showing. You'll see:

- **Trees** (left): the tree files. `Example` is a small one to read first.
- **Nodes** (left, below): every step you can drag onto the canvas. Search
  box at the top. `SetWantedState` and `FollowPath` are the robot's own; the
  rest (`Sequence`, `Fallback`, `Timeout`, `Sleep`...) are built in.
- **The canvas** (middle): the tree. Click a node to select it.
- **Properties** (right): the selected node's settings, called *ports*.
- **Problems / XML / Live log** (bottom): mistakes Arbor found, the XML it
  will save, and the robot's live view.

## What to do

1. Click **+ New tree**, call it `MyFirstAuto`, and save it in a new file.
2. Build a **Sequence** of at least three steps. Drag nodes onto the canvas
   (or use the right-click menu, **Add child**): a `SetWantedState`, a
   `Sleep`, and a `FollowPath`.
3. Fill in their ports in the Properties panel: `state` (for example
   `AIM`), `msec` for the `Sleep`, and `trajectory` (any name, like
   `ToScore`).
4. Protect a step that might never finish: right-click it, **Wrap in...**
   a `Timeout`, and give it a number of milliseconds. Then wrap the whole
   thing in a `Fallback` whose second child is another `SetWantedState`, so
   something sensible happens if the first part fails.
5. Make a wait **tunable**: in the tree's properties declare an input port
   (for example `wait_msec` with a default), then use `{wait_msec}` as the
   `Sleep`'s `msec`. On the robot that value shows up on the dashboard.
6. **Save** (Ctrl+S) and look at the **XML** tab. That is the file the robot
   runs.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want. The
checkpoints read the files you've saved in `src/main/deploy/autos/trees`.
