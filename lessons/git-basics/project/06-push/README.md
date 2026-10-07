# 6. Push

Real teams keep a shared copy of the repository on a server (GitHub). Your
copy is called the **local** repository; the shared one is the **remote**,
and by convention it is named `origin`. There's no login here: `origin` is a
private copy kept out of sight on this machine, and it behaves exactly like
GitHub would.

You already have a finished feature branch, `issue-18-team-colors`, with one
commit on it. Only your computer knows about it so far.

## Task

Send your branch to `origin`:

```
git push origin issue-18-team-colors
```

(`git push -u origin issue-18-team-colors` also remembers the link, so a later
plain `git push` knows where to go.) Run **Verify** when you're done.

## You're done when

`origin` has an `issue-18-team-colors` branch that points at the same commit
as yours.
