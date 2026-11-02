# Git Basics

Eight small git exercises, each in its own numbered folder. Every folder is
its own real git repository. Everything in this lesson is done with buttons
and menus. You don't need the terminal.

## Finding your way around

- **Pick the repository in the bottom-left corner.** At the very bottom of the
  window, the repository name sits just left of the branch name. Click it and
  choose the folder you're working on. Your choice stays until you change it,
  even if you open a file from another folder, so pick again for each exercise.
  Reloading the page clears it, so check the name after a reload.
  Don't see a name there yet? Open any file in the folder, or click the folder's
  row in Source Control, and it appears.
- **The branch name beside it** is the branch of the repository you picked.
  Click it to switch branches or make a new one.
- **Source Control** (the branch icon on the left) has one row per folder, with
  that folder's message box and **Commit** button under it. Scroll the list to
  reach the later folders.
- **Each row has its own ...** menu (More Actions), with Branch, Pull, Push and
  Stash. It always works on its own folder, so use the one on the row of the
  folder you picked.

There's no GitHub account or login here. If anything asks you to sign in to
GitHub, click **Cancel**. Each exercise has a shared copy (`origin`), kept out
of sight on this machine, that behaves like GitHub would. Only exercises 6 and 7
use it. This lesson is about how git works, one button at a time. For background on why we use git the way we do,
see the team's [GitHub docs page](https://frc-team-4143.github.io/docs/software/tools/github).

## Our conventions

- **`main`** is production-ready code.
- **`develop`** is the integration branch — finished feature branches merge
  into `develop`, not `main`.
- Feature branches are named **`issue-<number>-<short-description>`**, e.g.
  `issue-12-add-turbo-boost`. Branch names are all lowercase, with hyphens
  between words.
- Nobody pushes straight to `main` or `develop`.
- Commit often, with clear messages.

## The exercises

Open each folder's own `README.md` for the task. Do them in order — later
ones build on the same ideas. Dozer (top bar) walks you through the buttons.

You will not see a blue **Publish Branch** or **Sync Changes** button in this
lesson, because the exercises use the **...** menu instead. Leave the little
cloud icons alone: this lesson doesn't need them. If a
pop-up asks whether to "periodically run git fetch", click **No**.

1. **`01-first-commit/`** — make your first commit.
2. **`02-feature-branch/`** — branch, commit, follow the naming convention.
3. **`03-merge/`** — merge a finished feature branch into `develop`.
4. **`04-merge-conflict/`** — merge a branch that touches the same line you
   changed, and resolve the conflict.
5. **`05-rebase/`** — rebase a feature branch onto a `develop` that moved on
   without it.
6. **`06-push/`** — send your branch to the shared copy (`origin`).
7. **`07-pull/`** — bring a teammate's new commit into your copy.
8. **`08-stash/`** — shelve unfinished work so you can switch branches.

## Checking your work

Open the lesson chip at the top and choose **Checkpoints** to see the list and
run **Verify**.
Each checkpoint tells you what it's looking for if it doesn't pass yet —
nothing here is graded, so verify as many times as you want.
