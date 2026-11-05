#!/usr/bin/env bash
# Builds the git lesson's eight scenario repos. Runs once, right after
# the lesson's files are copied into /workspace/project (see
# ImportManager.executeCatalogLoad in apps/control/src/imports.ts) — real
# .git directories can't ship as static catalog files, so this script builds
# each scenario's starting history deterministically instead.
#
#   bash setup.sh               builds all eight scenarios (a lesson load)
#   bash setup.sh 05-rebase     rebuilds only that one (a checkpoint's
#                               "Reset this exercise", via its resetPath);
#                               Scriptum has already put that folder's
#                               starting files back, and nothing else is touched
#
# Every commit here uses fixed author/committer identity and dates, so the
# resulting SHAs are the same on every load. The verify scripts in
# checkpoints/verify/ rely on that: they tag the "before" state of
# each scenario so they can tell what the student actually changed.
set -euo pipefail

# Run with cwd already set to /workspace/project by the caller (ImportManager
# passes workdir explicitly) - don't guess a path from the script's own
# location, which lives under /opt/frc-catalog instead.
ROOT="${PWD}"
SCENARIOS=(01-first-commit 02-feature-branch 03-merge 04-merge-conflict 05-rebase 06-push 07-pull 08-stash)

if [ "$#" -gt 1 ]; then
	echo "git setup: give at most one exercise folder, got $#." >&2
	exit 1
fi
if [ "$#" -eq 1 ]; then
	ONLY="$1"
	known=0
	for name in "${SCENARIOS[@]}"; do [ "$name" = "$ONLY" ] && known=1; done
	if [ "$known" -ne 1 ]; then
		echo "git setup: \"$ONLY\" isn't one of this lesson's exercises." >&2
		exit 1
	fi
	if [ ! -d "$ROOT/$ONLY" ]; then
		echo "git setup: the $ONLY folder is missing under $ROOT." >&2
		exit 1
	fi
	TARGETS=("$ONLY")
else
	if [ ! -d "$ROOT/01-first-commit" ]; then
		echo "git setup: expected scenario folders under $ROOT, found none." >&2
		exit 1
	fi
	TARGETS=("${SCENARIOS[@]}")
fi

# Only set these if the student hasn't already configured their own -
# students may return to this lesson after having set a real identity.
git config --global user.name >/dev/null 2>&1 || git config --global user.name "Scriptum Student"
git config --global user.email >/dev/null 2>&1 || git config --global user.email "student@scriptum.local"
git config --global init.defaultBranch main
git config --global core.editor nano
git config --global pull.rebase false
git config --global advice.detachedHead false

# Every scripted commit uses this one fixed date. Nothing here depends on
# what ran before, so rebuilding one exercise alone gives exactly the
# history a full load gives it. (This used to be a counter, but it was only
# ever read through "$(...)", a subshell, so it never moved past 00:01:00;
# keeping that value keeps every commit SHA the same as before.)
LESSON_DATE="2026-01-01T00:01:00"

# Commits as the lesson's fixed "mentor" identity at LESSON_DATE, so every
# load produces byte-identical history.
bot_commit() {
	local msg="$1"
	local d="$LESSON_DATE"
	GIT_AUTHOR_NAME="Team 4143 Bot" GIT_AUTHOR_EMAIL="bot@frc4143.local" \
	GIT_COMMITTER_NAME="Team 4143 Bot" GIT_COMMITTER_EMAIL="bot@frc4143.local" \
	GIT_AUTHOR_DATE="$d" GIT_COMMITTER_DATE="$d" \
		git commit -q -m "$msg"
}

insert_before_last_brace() {
	# Inserts $2 as a new indented line right before the final line of file $1
	# (each of these tiny fixture files ends with a lone closing brace).
	local file="$1" line="$2" tmp
	tmp="$(mktemp)"
	head -n -1 "$file" > "$tmp"
	printf '    %s\n' "$line" >> "$tmp"
	tail -n 1 "$file" >> "$tmp"
	mv "$tmp" "$file"
}

# Every exercise gets a hidden "origin": a bare repository kept outside the
# project, so it doesn't clutter the file tree and behaves like GitHub without
# needing an account. Exercises 6 and 7 use it. The others leave it empty, but
# still need it: in a repo with no remote, the editor's Publish Branch and cloud
# icons offer to create a repository on the student's own GitHub; with one
# remote they push there instead.
ORIGINS="${SCRIPTUM_ORIGINS:-$HOME/.cache/scriptum-git-origins}"
mkdir -p "$ORIGINS"

add_hidden_origin() {
	local name="$1"
	rm -rf "$ORIGINS/$name.git"
	git init -q --bare -b main "$ORIGINS/$name.git"
	git remote add origin "$ORIGINS/$name.git"
}

# --- 1. First commit ---------------------------------------------------
build_01_first_commit() {
	(
		cd "$ROOT/01-first-commit"
		git init -q -b main
		add_hidden_origin 01-first-commit
		git add -A
		bot_commit "Add roster file"
		git tag -f lesson-start >/dev/null
	)
}

# --- 2. Feature branch ---------------------------------------------------
build_02_feature_branch() {
	(
		cd "$ROOT/02-feature-branch"
		git init -q -b main
		add_hidden_origin 02-feature-branch
		git add -A
		bot_commit "Initial FEATURES.md"
		git branch develop
		git tag -f lesson-main-start >/dev/null
	)
}

# --- 3. Merge -------------------------------------------------------------
build_03_merge() {
	(
		cd "$ROOT/03-merge"
		git init -q -b main
		add_hidden_origin 03-merge
		git add -A
		bot_commit "Initial robot constants"
		git branch develop
		git tag -f lesson-main-start >/dev/null
		git checkout -q -b issue-7-auto-distance develop
		insert_before_last_brace Constants.java 'public static final double AUTO_DISTANCE_METERS = 2.0;'
		git add -A
		bot_commit "Add AUTO_DISTANCE_METERS for auto-drive"
		git tag -f lesson-feature-issue-7 >/dev/null
		git checkout -q develop
	)
}

# --- 4. Merge conflict ----------------------------------------------------
build_04_merge_conflict() {
	(
		cd "$ROOT/04-merge-conflict"
		git init -q -b main
		add_hidden_origin 04-merge-conflict
		git add -A
		bot_commit "Initial robot constants"
		git branch develop
		git tag -f lesson-main-start >/dev/null

		git checkout -q -b issue-9-max-speed develop
		sed -i 's/MAX_SPEED = 3.0/MAX_SPEED = 5.0/' Constants.java
		git add -A
		bot_commit "Raise MAX_SPEED for competition"
		git tag -f lesson-feature-issue-9 >/dev/null

		git checkout -q develop
		sed -i 's/MAX_SPEED = 3.0/MAX_SPEED = 4.0/' Constants.java
		git add -A
		bot_commit "Tune MAX_SPEED after practice"
		git tag -f lesson-develop-pre-merge >/dev/null
	)
}

# --- 5. Rebase --------------------------------------------------------------
build_05_rebase() {
	(
		cd "$ROOT/05-rebase"
		git init -q -b main
		add_hidden_origin 05-rebase
		git add -A
		bot_commit "Initial LED colors and robot map"
		git branch develop
		git tag -f lesson-main-start >/dev/null

		git checkout -q -b issue-15-led-colors develop
		insert_before_last_brace LedColors.java 'public static final String RAINBOW = "RAINBOW";'
		git add -A
		bot_commit "Add rainbow LED color"

		git checkout -q develop
		sed -i 's/DRIVE_RIGHT_LEADER = 3;/DRIVE_RIGHT_LEADER = 3;\n    public static final int DRIVE_LEFT_FOLLOWER = 2;/' RobotMap.java
		git add -A
		bot_commit "Add left follower CAN ID"
		git tag -f lesson-develop-tip >/dev/null
		git checkout -q issue-15-led-colors
	)
}

# --- 6, 7. Push and pull ------------------------------------------------------
teammate_commit() {
	local msg="$1" d="$LESSON_DATE"
	GIT_AUTHOR_NAME="Riley Teammate" GIT_AUTHOR_EMAIL="riley@frc4143.local" \
	GIT_COMMITTER_NAME="Riley Teammate" GIT_COMMITTER_EMAIL="riley@frc4143.local" \
	GIT_AUTHOR_DATE="$d" GIT_COMMITTER_DATE="$d" \
		git commit -q -m "$msg"
}

build_06_push() {
	(
		cd "$ROOT/06-push"
		git init -q -b main
		git add -A
		bot_commit "Initial team colors"
		git branch develop
		add_hidden_origin 06-push
		git push -q -u origin main develop
		git checkout -q -b issue-18-team-colors develop
		printf 'Secondary: gold\n' >> TeamColors.txt
		git add -A
		bot_commit "Add secondary team color"
		git tag -f lesson-push-tip >/dev/null
	)
}

build_07_pull() {
	(
		cd "$ROOT/07-pull"
		git init -q -b main
		git add -A
		bot_commit "Initial practice schedule"
		git branch develop
		add_hidden_origin 07-pull
		git push -q -u origin main develop
		git tag -f lesson-develop-before >/dev/null
		# A teammate pushes to develop on origin. Our copy doesn't know yet.
		tmp="$(mktemp -d)"
		git clone -q "$ORIGINS/07-pull.git" "$tmp"
		(
			cd "$tmp"
			git checkout -q develop
			printf 'Saturday 10am\n' >> Schedule.txt
			git add -A
			teammate_commit "Add Saturday practice"
			git push -q origin develop
		)
		rm -rf "$tmp"
		git checkout -q develop
	)
}

# --- 8. Stash ---------------------------------------------------------------
build_08_stash() {
	(
		cd "$ROOT/08-stash"
		git init -q -b main
		add_hidden_origin 08-stash
		git add -A
		bot_commit "Initial pit notes"
		git branch develop
		git tag -f lesson-main-start >/dev/null
		git checkout -q -b issue-21-new-motor develop
		sed -i 's/The new motor is not ordered yet\./The new motor is a Kraken X60./' Notes.md
		git add -A
		bot_commit "Note the new motor"
		git tag -f lesson-issue-tip >/dev/null
		# The student's unfinished work: modified, not committed.
		printf 'WIP: compare Kraken and Falcon prices\n' >> Notes.md
	)
}

for name in "${TARGETS[@]}"; do
	# A scenario always starts from a fresh repository. Scriptum hands over
	# freshly copied files, but if a .git is somehow left in the folder,
	# "git init" would keep its old history underneath the new commits.
	rm -rf "${ROOT:?}/$name/.git"
	"build_${name//-/_}"
done

if [ "${#TARGETS[@]}" -eq 1 ]; then
	echo "git: ${TARGETS[0]} is back to how it started."
else
	echo "git: scenario repos ready."
fi
