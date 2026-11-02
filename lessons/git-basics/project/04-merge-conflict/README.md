# 4. Merge Conflict

`develop` and a feature branch, `issue-9-max-speed`, both changed
`MAX_SPEED` in `Constants.java` — to different values. Merging is going to
conflict.

## Task

1. Click the repository name in the bottom-left corner (just left of the
   branch name) and choose **04-merge-conflict**.
2. Make sure the branch name beside it says `develop`. Then, in Source Control,
   find the **04-merge-conflict** row and click its **...** menu. Choose
   **Branch**, then **Merge...**, and pick `issue-9-max-speed`.
3. Git will stop and mark the conflict in `Constants.java`. Open the file —
   VSCodium shows the two versions with **Accept Current Change**, **Accept
   Incoming Change** and **Accept Both Changes** links right above the conflict. Pick
   one value for `MAX_SPEED` (either is fine) and remove the
   `<<<<<<<`/`=======`/`>>>>>>>` markers.
4. Save, then stage the resolved file with the **+** next to it (under
   **Merge Changes**) and press **Continue**, the button that replaces
   **Commit** during a merge. The message box is already filled in with a merge
   message; just accept it.

Run **Verify** when you're done.

## You're done when

Source Control shows no merge in progress, `Constants.java` has a single
`MAX_SPEED` with no conflict markers left in it, and the **Graph** shows a
merge commit on `develop` with both parents.
