# scriptum-lessons

The MARS/WARS Scriptum lessons catalog, served to a Scriptum deployment by
pointing its `LESSONS_CATALOG_REPO` environment variable at this repo — no
Scriptum rebuild or redeploy needed to add or edit a lesson, just a commit here.

See [Authoring Lesson Modules](https://github.com/FRC-Team-4143/scriptum/blob/main/docs/lessons/authoring-modules.md)
in the Scriptum repo for the full schema reference this repo follows.

## Layout

One folder per lesson, and everything about a lesson is inside it:

```text
lessons/<id>/
  lesson.json            title, description, kind, track, order, requires, checkpoints,
                         and which shared guides the lesson uses
  project/               the starter files students get (copied into their workspace)
  checkpoints/           verify scripts (verify/<checkpoint id>.sh) and an optional setup.sh
  guides/<guide id>.json this lesson's Dozer guides: title, summary, topic, order, steps
  help.json              Dozer's hints for stuck students in this lesson
  solution/              reference files that make every checkpoint pass (for the tests)
  lesson.test.ts         the lesson's own test
common/
  guides/<guide id>.json guides several lessons use (a lesson lists them in lesson.json)
  help/errors.json       plain-English help for common Java errors, shared by every lesson
  scripts/               build-catalog.ts, which writes catalog.json
  tests/                 tests that look at the whole repo, and the kit lesson tests use
catalog.json             GENERATED index Scriptum reads. Run `bun run index` after changing it
```

`catalog.json` exists because GitHub's raw files can't be listed: it records which lessons and
guides there are, so Scriptum reads one file and then fetches a lesson's project, scripts, guides or
help only when they're needed. Don't edit it by hand; the tests fail when it is out of date.

### Adding a lesson

1. Copy a similar lesson's folder to `lessons/<new-id>/` (the id is lowercase kebab-case).
2. Edit `lesson.json` (title, order, track, checkpoints) and replace `project/` with the starter files.
3. Put each checkpoint's verify script in `checkpoints/verify/<checkpoint id>.sh`.
4. Add guides in `guides/` and hints in `help.json` if the lesson has them.
5. Run `bun run index`, then `bun test`, and commit.

Paths in `lesson.json` (`checkpoints/verify/x.sh`, `setupScript`) are relative to the lesson's
folder; the generated `catalog.json` makes them relative to the repo.

## What's here

Three tracks, in curriculum order (each lesson's `track` and `order` are in its `lesson.json`):

- **Tools**: `editor-basics` (Meet the Editor: files, folders, saving, the terminal, for someone
  who has never used a code editor), then git, AdvantageScope, Elastic, and Choreo introductions.
- **Java Programming**: `hello-world` through `java-inheritance`, each
  module locked until its prerequisite is complete.
- **FRC Robot**: a differential-drive robot that uses [MWLib](https://github.com/FRC-Team-4143/MW-Lib)
  from the first lesson. The seven modules follow the training lessons, one after another, and each
  starts from the previous module's finished robot:
  `robot-drivetrain` (Motors and Drivetrains), `robot-sensors` (Sensors and Feedback),
  `robot-drive-math` (Drive Math and Methods), `robot-odometry` (Objects and Odometry),
  `robot-subsystems` (Mechanisms and Subsystems), `robot-control-theory` (Control Theory),
  `robot-state-machines` (State Machines and Commands) and `robot-autonomous` (Autonomous).
  Each module's `README.md` is the lesson. Computer Vision has no robot code, so it has no module.
  See [Robot modules](#robot-modules).

## Tutor (Dozer)

The **Dozer** button in Scriptum's top bar opens short walkthroughs: Dozer, the team's plow
robot, spotlights the real buttons inside AdvantageScope, Choreo, Elastic, the editor and the
Driver Station, and can label code on a board. A lesson's own guides are in its `guides/` folder,
one file each (title, summary, topic, order, and the steps). A guide that several lessons use,
like `build-and-run`, goes in `common/guides/` and each lesson that uses it lists it in
`lesson.json` (`"guides": ["build-and-run"]`).

A guide shows up in Dozer's list only while one of the lessons it belongs to is loaded (there's no
browsing the rest). The step format (targets, regions, boards, "your turn") is in Scriptum's
`docs/lessons/tutor.md`; `bun test common/tests/guides.test.ts` checks every guide. To check a
walkthrough itself, open it in Scriptum: a step whose ring is missing has a selector that didn't
match.

## Publishing

1. Keep this repo public: Scriptum's remote catalog fetches over an
   unauthenticated `raw.githubusercontent.com` URL.
2. On the Scriptum control plane, set
   `LESSONS_CATALOG_REPO=FRC-Team-4143/scriptum-lessons` (and
   `LESSONS_CATALOG_BRANCH` if not using `main`).
3. Commit and push changes here (with `catalog.json` regenerated) — Scriptum caches each file for
   60 seconds, so edits go live within about a minute.

## Testing

```bash
bun test
```

Each lesson's `lesson.test.ts` round-trips its checkpoints (every one fails on the starter and passes
with `solution/`); `common/tests/` checks `catalog.json`, the repo's layout, and every guide and
help file. The Java round trips need a JDK on your PATH and are skipped without one.

## Robot modules

The first four `robot-*` modules keep the student's code in `Robot.java`, and the last three move to
subsystems and commands. They share one scaffold, so a change to the drivetrain or the build usually
has to be made in every module (`lessons/robot-*/project/`):

- `mechanisms/DifferentialDriveMech.java` is the lesson-local drive mech. It extends MWLib's
  `MechBase`, simulates the drivetrain (including slightly imperfect encoders, so a student's pose
  differs a little from `Drive/TruePose`), and grows a few methods per lesson.
- Modules 1-4: `lesson/LessonLoop.java` runs MWLib's `SubsystemManager` loop so students never call
  `readInputs` or `writeOutputs`. Modules 5-7 use a real `RobotContainer extends SubsystemManager`.
- `lesson/LessonChecks.java` (via `lesson/Checks.java` in modules 5-7) republishes the min and max of
  a few logged values under `Check/...`, so the `nt4-value` checkpoints still pass after the student
  lets go of the sticks. Each module's `.vscode/settings.json` hides it, the build files and other
  scaffolding from the Explorer.
- Modules 1-4 keep the chassis numbers in `Constants.java`; from `robot-subsystems` on that file is gone
  and they live in `subsystems/drive/DrivetrainConstants.java` (static fields, read as
  `DrivetrainConstants.WHEEL_RADIUS_METERS`). The wheel radius (3 in) and track width (24 in) are
  the real robot's; the gear ratio and mass are still placeholders.
- The shooter uses the real flywheel's wheel radius (3 in) and mass (2.3 kg). In `robot-subsystems`
  the flywheel runs at a fixed duty cycle (`SHOOT_DUTY_CYCLE`, 0.5) and the drivetrain has no state
  switch; velocity control (`SHOOT_VELOCITY`) first appears in `robot-control-theory` and the
  states-plus-`switch` drivetrain in `robot-state-machines`. In `robot-control-theory` the flywheel starts in bang-bang mode with untuned gains on purpose; that
  lesson's task is to write bang-bang and feedforward, then tune PID (`kV` about 0.12, `kP` about 0.2
  works). `lessons/robot-control-theory/solution/` is the finished version.
- MWLib comes from jitpack (`com.github.FRC-Team-4143.MW-Lib:mw-lib-java:<tag>`), with no
  credentials. Bump the tag in every `build.gradle` together.
- `lessons/<id>/solution/` holds the reference solution used by that lesson's `lesson.test.ts` to
  round-trip the script checkpoints. The `nt4-value` checkpoints need a running simulator.

### Java taught along the way

The robot lessons are an alternate way to learn Java: students are **not** expected to have done the Java
track first, so each lesson introduces its new concepts in a "Java you will learn in this lesson" section
and uses them right away. The Java modules remain available as a deeper, standalone path.

| Robot lesson | New Java introduced (and used) |
| --- | --- |
| 1 Motors and Drivetrains | calling methods, `double` variables, `+ - * /` operators, comments |
| 2 Sensors and Feedback | `if` / `else`, comparison operators, `static final` constants |
| 3 Drive Math and Methods | writing methods (parameters, return values), arrays, `for` loops, `%`, `Math` |
| 4 Objects and Odometry | classes and objects, `new`, `import`, `static`, `abstract` |
| 5 Mechanisms and Subsystems | inheritance (`extends`, `@Override`), constructors, lists, enums as a list of states, `if` / `else` in subsystem logic |
| 6 Control Theory | enums and `switch` (picking a control style), unit conversion, small methods |
| 7 State Machines and Commands | `switch` on a subsystem's state (practice), `&&` `\|\|` `!`, boolean methods, lambdas |
| 8 Autonomous | extending `Command`, command groups (and a `for` loop used again) |

Recursion is not used on the robot.
