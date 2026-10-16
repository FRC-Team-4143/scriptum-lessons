# Arbor

Background: [Arbor](https://github.com/coalman321/Arbor) is the editor we build
autonomous routines in. An auto is a **behavior tree**: a small tree of steps
the robot ticks through. Arbor lets you drag the steps together, fill in their
settings, and saves the result as a file the robot runs. The same tool
shows the tree running on the robot, node by node.

This lesson is entirely inside the **Arbor** pane on the right - there's no
Java to write. The project is `src/main/deploy/autos`: its `trees/` folder
holds the trees, and `nodes.xml` lists the robot-specific steps you can use.

## Getting started

When Arbor opens, **Open project** and pick `src/main/deploy/autos` if it
isn't already showing. You'll see:

- **Trees** (left): the tree files. `Example` is a small one to read first; `ReplayDemo` goes with
  the sample log (see *Replay a run*).
- **Nodes** (left, below): every step you can drag onto the canvas. Search
  box at the top. `SetWantedState`, `IsSystemState` and `FollowPath` are the
  robot's own; the rest (`Sequence`, `Fallback`, `Timeout`, `Sleep`...) are built in.
- **The canvas** (middle): the tree. Click a node to select it.
- **Properties** (right): the selected step's settings. Arbor heads them **Ports**, which is
  just its word for settings.
- **Problems / XML / Live log** (bottom): mistakes Arbor found, the XML it
  will save, and the robot's live view.

## What to do

1. Click **+ New tree**, call it `MyFirstAuto`, and save it in a new file.
2. Build a **Sequence** of at least three steps. Drag nodes onto the canvas
   (or use the right-click menu, **Add child**): a `SetWantedState`, a
   `Sleep`, and a `FollowPath`.
3. Fill in their settings in the Properties panel: `state` (for example
   `AIM`), `msec` for the `Sleep`, and `trajectory` (any name, like
   `ToScore`).
4. Protect a step that might never finish: right-click it, **Wrap in...**
   a `Timeout`, and give it a number of milliseconds. Then wrap the whole
   thing in a `Fallback` whose second child is another `SetWantedState`, so
   something sensible happens if the first part fails.
5. Make a wait **tunable**: click an empty part of the canvas to select the tree, and under
   its own settings (the heading *SubTree interface*) click **+ Add port**: name it `wait_msec`
   with a default of `500`. Then type `{wait_msec}`, with the braces, as the `Sleep`'s `msec`. On the robot that value shows up on the dashboard.
6. **Save** (Ctrl+S). Arbor writes the tree into `trees/`, and that file is
   what the robot runs. You never edit it by hand: Arbor is how you change it.

## Replay a run

Every time the robot runs an auto it records a **log**: each node's status, and exactly when it
changed. Arbor can play one back, node by node, so you can see where an auto went wrong after the
match. On the robot the logs are in `/U/logs/bt` (on the USB stick); in simulation, in the project's
`logs/bt` folder. This lesson ships one to try: `sample-logs/ReplayDemo.btlog`, recorded from the
`ReplayDemo` tree.

1. Arbor opens a log from **your own computer**, so first download it: in the editor's file list,
   right-click `sample-logs/ReplayDemo.btlog` and choose **Download**.
2. In Arbor, click the **project name** in the top-left corner and choose **Replay a log file
   (.btlog)...**, then pick the file you downloaded. (Dragging the file onto Arbor works too.)
3. Arbor opens `ReplayDemo` and shows a playback bar: **Space** plays and pauses, **← / →** step one
   change at a time, **Home / End** jump to the start and end, and the menu on the right sets the
   speed.
4. Work out why the auto failed: which node went red, and which one never ran?

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want. The
checkpoints read the files you've saved in `src/main/deploy/autos/trees`.
