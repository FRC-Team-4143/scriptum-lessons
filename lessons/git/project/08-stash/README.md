# 8. Stash

You're partway through some work on `issue-21-new-motor`. It isn't ready to
commit, but your teammate just spotted a typo on `develop` ("Teh" in
`Notes.md`) that needs fixing right now.

If you try to switch to `develop` first (the branch name in the bottom-left),
a message says your local changes would be overwritten. Press **Cancel** there:
**Force Checkout** would throw your work away. A **stash** puts your unfinished
work on a shelf so you can switch away, and brings it back afterwards.

## Task

1. Click the repository name in the bottom-left corner (just left of the
   branch name) and choose **08-stash**.
2. Put your unfinished work on the shelf: in Source Control, point at the
   **CHANGES** heading and click its **...** menu. Choose **Stash**, then **Stash**
   in the second menu. Press **Enter** when it asks for a stash message.
3. Click the branch name in the bottom-left, switch to `develop` and fix the
   typo ("Teh" should be "The"). Save, then find `Notes.md` in Source Control.
   Stage it with **+**, write a
   message and press **Commit**.
4. Switch back to `issue-21-new-motor`.
5. Take your work off the shelf: the **...** menu on the **CHANGES** heading,
   **Stash**, then **Pop Latest Stash**.

Don't commit your unfinished work on `issue-21-new-motor`. Run **Verify** when
you're done.

## You're done when

`develop` has your typo fix, you are back on `issue-21-new-motor` with your
unfinished change in the file (not committed), and the stash is empty.
