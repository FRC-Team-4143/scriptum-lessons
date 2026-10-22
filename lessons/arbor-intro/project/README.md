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
   A tree has exactly one node at the top, its **root node**, and every
   other step hangs underneath it. On the empty canvas, click **Add root
   node** and pick **Sequence** (the node that runs its children in order).
2. Build that **Sequence** up to at least three steps. Drag nodes onto it
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
6. Reuse steps with a **SubTree**: click **+ New tree**, call it `AimAndShoot`, and save it in
   **an existing file**: `trees/my_first_auto.xml`, the file Arbor made for MyFirstAuto (the
   list starts on another one). Give it a root **Sequence** with a `SetWantedState`
   `AIM` and a `SetWantedState` `SHOOT`. Then open `MyFirstAuto` again and **drag** `AimAndShoot`
   from the Trees list onto its Sequence. It becomes one step that runs the whole small tree, and
   any auto can use it. (Shortcut for next time: right-click a node, **Extract to SubTree**.)
7. Mark the tree the robot runs. `trees/my_first_auto.xml` now holds two trees, and the robot starts the
   file's **main tree**. Right-click `MyFirstAuto` in the Trees list and choose **Set as main
   tree of file** (or tick **Main tree of its file** in the Properties panel): a **main** badge
   appears. (`AimAndShoot` shouldn't be the main one; it runs because MyFirstAuto uses it.)
8. **Save** (Ctrl+S). Arbor writes the trees into `trees/`, and that file is
   what the robot runs. You never edit it by hand: Arbor is how you change it.

## Replay a run

Every time the robot runs an auto it records a **log**: each node's status, and exactly when it
changed. Arbor can play one back, node by node, so you can see where an auto went wrong after the
match. On the robot the logs are in `/U/logs/bt` (on the USB stick); in simulation, in the project's
`logs/bt` folder. This lesson ships one to try: `sample-logs/ReplayDemo.btlog`, recorded from the
`ReplayDemo` tree.

1. In Arbor, click the **project name** in the top-left corner and choose **Replay a log file
   (.btlog)...**. A file browser opens, showing the files in your workspace.
2. It starts in the Arbor project folder (`src/main/deploy/autos`). Click **↑ Up** four times, until
   you see `sample-logs`, open it, click `ReplayDemo.btlog`, and press **Replay**. (Or type
   `/workspace/project/sample-logs` in the folder box and press Enter.) A log from the robot's USB
   stick is on your own computer instead: drag it onto Arbor to play it.
3. Arbor opens `ReplayDemo` and shows a playback bar: **Space** plays and pauses, **← / →** step one
   change at a time, **Home / End** jump to the start and end, and the menu on the right sets the
   speed.
4. Work out why the auto failed. Step through the whole run and answer the three questions in
   `sample-logs/Answers.java` (next to the log): when FollowPath finished, which node's failure
   stopped the Sequence, and which node never ran. Save it, and the **Read the replay** checkpoint
   checks your answers.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want. The
checkpoints read the files you've saved in `src/main/deploy/autos/trees`, and the last one,
**Read the replay**, reads `sample-logs/Answers.java`.
