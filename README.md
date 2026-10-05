# scriptum-lessons

The MARS/WARS Scriptum lessons catalog, served to a Scriptum deployment by
pointing its `LESSONS_CATALOG_REPO` environment variable at this repo — no
Scriptum rebuild or redeploy needed to add or edit a lesson, just a commit here.

See [Authoring Lesson Modules](https://github.com/FRC-Team-4143/scriptum/blob/main/docs/lessons/authoring-modules.md)
in the Scriptum repo for the full schema reference this repo follows.

## Layout

```text
modules.json               curriculum-order index: each module's id, order, and track
modules-meta/<id>.json     one module's title, description, kind, prerequisites, checkpoints
modules/<id>/              one directory per module: the complete starting project
checkpoints/<id>/setup.sh  optional, runs once right after the module loads
checkpoints/<id>/verify/   per-checkpoint verifier scripts
tests/                     bun tests for the checkpoint verifiers
```

## What's here

Three tracks, listed in curriculum order in `modules.json`:

- **Tools**: git, AdvantageScope, Elastic, and Choreo introductions.
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

## Publishing

1. Keep this repo public: Scriptum's remote catalog fetches over an
   unauthenticated `raw.githubusercontent.com` URL.
2. On the Scriptum control plane, set
   `LESSONS_CATALOG_REPO=FRC-Team-4143/scriptum-lessons` (and
   `LESSONS_CATALOG_BRANCH` if not using `main`).
3. Commit and push changes here — Scriptum caches the module list for 60
   seconds, so edits go live within about a minute.

## Testing

```bash
bun test
```

## Robot modules

The first four `robot-*` modules keep the student's code in `Robot.java`, and the last three move to
subsystems and commands. They share one scaffold, so a change to the drivetrain or the build usually
has to be made in every module (`modules/robot-*/`):

- `mechanisms/DifferentialDriveMech.java` is the lesson-local drive mech. It extends MWLib's
  `MechBase`, simulates the drivetrain (including slightly imperfect encoders, so a student's pose
  differs a little from `Drive/TruePose`), and grows a few methods per lesson.
- Modules 1-4: `lesson/LessonLoop.java` runs MWLib's `SubsystemManager` loop so students never call
  `readInputs` or `writeOutputs`. Modules 5-7 use a real `RobotContainer extends SubsystemManager`.
- `lesson/LessonChecks.java` (via `lesson/Checks.java` in modules 5-7) republishes the min and max of
  a few logged values under `Check/...`, so the `nt4-value` checkpoints still pass after the student
  lets go of the sticks. Each module's `.vscode/settings.json` hides it, the build files and other
  scaffolding from the Explorer.
- `Constants.java` holds the chassis numbers. The wheel radius (3 in) and track width (24 in) are the
  real robot's; the gear ratio and mass are still placeholders.
- The shooter uses the real flywheel's wheel radius (3 in) and mass (2.3 kg). In
  `robot-control-theory` the flywheel starts in bang-bang mode with untuned gains on purpose; that
  lesson's task is to write bang-bang and feedforward, then tune PID (`kV` about 0.12, `kP` about 0.2
  works). `tests/solutions/robot-control-theory/` is the finished version.
- MWLib comes from jitpack (`com.github.FRC-Team-4143.MW-Lib:mw-lib-java:<tag>`), with no
  credentials. Bump the tag in every `build.gradle` together.
- `tests/solutions/<module>/` holds the reference solution used by `tests/robot-lessons.test.ts` to
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
| 5 Mechanisms and Subsystems | enums and `switch`, inheritance (`extends`, `@Override`), constructors, lists |
| 6 Control Theory | practice: unit conversion, `switch`, small methods |
| 7 State Machines and Commands | `&&` `\|\|` `!`, boolean methods, lambdas |
| 8 Autonomous | extending `Command`, command groups (and a `for` loop used again) |

Recursion is not used on the robot.
