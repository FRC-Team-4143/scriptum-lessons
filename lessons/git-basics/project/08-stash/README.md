# 8. Stash

You're partway through some work on `issue-21-new-motor`. It isn't ready to
commit, but your teammate just spotted a typo on `develop` ("Teh" in
`Notes.md`) that needs fixing right now.

If you try `git checkout develop` first, git refuses, because your unfinished
changes would be overwritten. A **stash** puts your unfinished work on a shelf
so you can switch away, and brings it back afterwards.

## Task

1. Put your unfinished work on the shelf: `git stash`.
2. Switch to `develop` and fix the typo ("Teh" should be "The"). Commit the fix.
3. Switch back to `issue-21-new-motor`.
4. Take your work off the shelf: `git stash pop`.

Don't commit your unfinished work on `issue-21-new-motor`. Run **Verify** when
you're done.

## You're done when

`develop` has your typo fix, you are back on `issue-21-new-motor` with your
unfinished change in the file (not committed), and the stash is empty.
