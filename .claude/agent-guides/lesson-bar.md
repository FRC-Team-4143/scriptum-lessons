# The lesson bar

What every change to a lesson has to meet before it goes in a PR. Every agent in this repo reads
it. `README.md` explains the repo layout; this file covers what the tests don't catch, plus the
rules for where work goes.

## Who it's for

High-school students on an FRC team. Many have never coded, and some have barely used a computer
(see `student-personas.md`). A lesson is right when a student can finish it from the lesson
itself: its README, Dozer's guides and the checkpoint messages, with no mentor standing by.

- Short sentences. Concrete words. Explain a term the first time you use it.
- Tie every idea to the robot ("Why this matters on the robot"), and list the new Java the
  lesson introduces ("New Java in this lesson").
- Team code style: fields `trailing_snake_case_`, methods `camelCase`, constants `UPPER_SNAKE`,
  4-space Java indent. Plain-Java lessons use the default package, with files in `project/src`.
  The student workspace runs **JDK 17**, so don't use newer Java features.

## Checkpoints

- **Round trip:** every checkpoint fails on the untouched starter and passes once `solution/` is
  laid over `project/`. The lesson's `lesson.test.ts` proves it. Give that test a `30_000`
  timeout: the default 5 s times out when the machine is busy.
- **Independent:** a student who has finished only checkpoint N passes checkpoint N. A plain-Java
  verify script copies every `src/*.java` except `Main.java`, and starter skeletons compile, so
  unfinished work elsewhere in the lesson can't block it (see `lessons/java-interfaces/`).
- **Fair to right answers:** accept any correct solution, not just the reference one. Check
  behavior (call the code with inputs it hasn't seen) rather than text, unless the lesson is
  about the text (a constant in a particular file, say).
- **Hard on wrong answers:** use cases a plausible mistake gets wrong: negatives, zero, empty,
  exact boundaries, ties, order. A check class can define its own implementations of the
  lesson's interfaces, so hard-coding the lesson's own examples doesn't pass.
- **Useful failure messages:** say what was called, what came back and what was expected, in
  plain words. Point at the kind of mistake, but don't hand over the answer.
- **Robot `nt4-value` checkpoints** need a running simulator, so no test covers them. Say so in
  the PR, and say how you checked them, if you did.
- **No JUnit in the workspace.** Scriptum has no JUnit verifier, and the workspace is a bare JDK.
  `java-unit-testing` ships a small JUnit-style `@Test` and `Assertions`, and grades the
  student's tests by running them against buggy versions of the code ("mutants").

## Dozer guides and help

`common/tests/guides.test.ts` and `help.test.ts` enforce the mechanics. Read them. What they
don't check:
- A guide teaches the idea on its own example first (a non-assist step), then shows the lesson's
  real code in an "In your lesson: ..." assist step. Explaining steps must not give the answer
  away. The test catches lines copied from `solution/`, but it can't catch a paraphrase.
- Every step in a `java-`, `robot-`, `git-` or `hello-world-` guide has `{never, bit}` wordings.
  `never` is for someone who has never coded: plain words, one idea, and which keys to press.
- Steps that Dozer can't watch keep the manual **"I did it"** button (a `waitFor` with only a
  `hint`). Don't replace it with detecting what the student typed; the team prefers the button.
- Guide `order` numbers group by track, so pick a free range. In use: Tools 1-36 and 40,
  Java Basics 202-366, Robot 387-466, Java Advanced 501-533.
- `help.json`'s `plan` has one stop per checkpoint, in checkpoint order, using this lesson's own
  guides.

## Testing a change

```bash
bun run index                                   # after any lesson.json or guide change
PATH=~/wpilib/2026/jdk/bin:$PATH bun test       # use the PATH only if javac isn't found
```

- Report the pass/fail/skip counts. A skip for the lesson you changed means it wasn't tested,
  so find a JDK rather than calling it done.
- **Robot modules:** also build every module you changed, with and without the solution applied,
  using `JAVA_HOME=~/wpilib/2026/jdk ./gradlew build --console=plain` in a scratch copy of
  `project/`. A cold build takes about 15 s. Don't leave `build/` or `.gradle/` output in the
  repo.
- Compile and run the lesson's `Main` (or the robot code) with the solution in place, and read the
  output as a student would.

## Where work goes

- **This repo:** branch off `origin/main` (`feat/<topic>` or `fix/<topic>`), commit with explicit
  paths (never `git add -A`), push, and open a PR. **Never merge**, because merging publishes to
  students. Use a separate PR for each piece of work.
- **Scriptum (the IDE) has to change** (a new verifier type, a tool in the workspace image, a
  Dozer feature): first try to solve it inside the lesson. If that can't work, file an issue
  on `FRC-Team-4143/marswars-apps` that describes the Scriptum change and links the lesson.
- **The docs site is now wrong** (track names, lesson lists, the Scriptum page): file an issue on
  `FRC-Team-4143/FRC-Team-4143.github.io` with the `documentation` label, naming the page and
  what changed. Don't edit the docs repo.
- **This repo is public** and marswars-apps is private. Never quote marswars-apps code in this
  repo's PRs or issues. Describe it instead.
