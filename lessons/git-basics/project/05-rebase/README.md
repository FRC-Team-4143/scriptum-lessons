# 5. Rebase

`issue-15-led-colors` branched off `develop` a while back and adds a
`RAINBOW` color. Since then, `develop` picked up an unrelated commit. Your
branch is now behind.

## Task

Rebase your branch onto the latest `develop`, instead of merging:

1. Make sure the branch name in the bottom-left says `issue-15-led-colors`.
2. In Source Control, open the **...** menu, choose **Branch**, then
   **Rebase Branch...**, and pick `develop`.

If there's nothing to resolve, this just replays your commit on top of the
new `develop`. (This scenario is set up so it rebases cleanly — no conflict
to resolve here, that was exercise 4.)

Don't touch `develop`. Run **Verify** when you're done.

## You're done when

The **Graph** in Source Control shows `issue-15-led-colors` as a straight
line on top of `develop`'s newest commit - no merge commit, and no fork in
the graph. `develop` is unchanged.
