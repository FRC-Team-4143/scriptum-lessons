# Agents for scriptum-lessons

Launched only when someone asks for them. None of them merge: a merge to `main` publishes to
students within a minute.

| Agent | Model | What it does | Hands off as |
| --- | --- | --- | --- |
| `lesson-author` | opus | Designs and builds a lesson (Tools, Java or robot), or changes an existing one. Proposes a plan first and stops; builds only once the plan is approved. | A PR |
| `checkpoint-breaker` | opus | Attacks one lesson's checkpoints with crafted student code: wrong answers that pass, and right answers that fail. Never edits the repo. | One GitHub issue per lesson, labelled `bug` |
| `lesson-reviewer` | sonnet | Reviews a lesson PR as six student personas and against the lesson bar. Read-only. | One PR comment |
| `robot-module-sync` | sonnet | Carries a scaffold change from one `robot-*` module to all of them, or bumps MWLib in every module, then builds each one. | A PR |

Shared files:
- `lesson-bar.md`: what every lesson change must meet, how to test it, and where problems that
  don't belong in this repo get reported. Every agent reads it.
- `student-personas.md`: the students the reviewer reads as. They're the same people the
  marswars-apps playtest kit uses, so a review and a playtest agree.

Typical flow for a new lesson:
1. `lesson-author` proposes a plan.
2. You approve it, and the author builds the lesson and opens a PR.
3. `lesson-reviewer` comments on that PR.
4. `checkpoint-breaker` attacks the lesson's checkers.
