# scriptum-lessons

The lesson catalog for Scriptum, MARS/WARS (FRC 4143)'s browser IDE. Students are high-schoolers,
many of whom have never coded. `README.md` is the reference for the layout, the tracks, the Dozer
tutor and the robot modules: read it before changing a lesson.

- **Public repo.** Scriptum fetches it over unauthenticated raw GitHub URLs. Never paste code from
  the private `FRC-Team-4143/marswars-apps` repo here, in a PR body or in an issue on this repo.
- **Merging to `main` publishes.** Scriptum caches each file for 60 seconds, so a merge reaches
  students within about a minute. Agents open PRs and never merge.
- **Build and test:** `bun run index` after changing any `lesson.json` or guide (it regenerates
  `catalog.json`, which is never edited by hand), then `bun test`.
- **The Java round trips need a JDK and skip silently without one.** If `javac` isn't on PATH, use
  the WPILib JDK: `PATH=~/wpilib/2026/jdk/bin:$PATH bun test`. A run that reports skips for the
  lesson you changed hasn't tested it.
- Stage explicit paths, never `git add -A` or `git add .`: robot projects have `build/` output
  and other untracked files.

## Agents

Four subagents live in `.claude/agents/`. `.claude/agent-guides/README.md` says what each one is
for, and `.claude/agent-guides/lesson-bar.md` is the shared bar every lesson change has to meet.
