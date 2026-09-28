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
- **FRC Robot**: `robot-starter`, unlocked by `git-basics`.

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
