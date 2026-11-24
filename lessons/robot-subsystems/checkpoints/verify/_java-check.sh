# Shared by shoot-logic.sh and oi-buttons.sh. Compiles the student's files against the stand-ins in
# stubs/ (plus any EXTRA_STUBS dir), runs CHECK_CLASS, and prints its one line.
# java_check <check class> <extra stubs dir or ""> <student file>...
# OWN_NAMES: a regex for "location:" lines that point at the student's own classes.
# Returns 0 pass, 1 fail (message printed), 2 when the student's code uses something the stand-ins
# don't have, so the caller can fall back to reading the code instead.
java_check() {
	local check="$1" extra="$2"
	shift 2
	# No JDK here: let the caller read the code instead.
	command -v javac >/dev/null && command -v java >/dev/null || return 2
	local here work
	here="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
	work="$(mktemp -d)"
	mkdir -p "$work/src" "$work/out"
	cp -r "$here/stubs/." "$work/src/"
	rm -f "$work/src/README.md"
	[[ -n "$extra" ]] && cp -r "$here/$extra/." "$work/src/"
	local file rel
	for file in "$@"; do
		rel="${file#*/src/main/java/}"
		mkdir -p "$work/src/$(dirname "$rel")"
		cp "$file" "$work/src/$rel"
	done
	cp "$here/$check.java" "$work/src/"
	if ! (cd "$work/src" && javac -nowarn -d ../out $(find . -name '*.java') >../compile.log 2>&1); then
		# A missing name in the student's own classes (OWN_NAMES) is the student's mistake, and so is a
		# syntax error. A missing name anywhere else, or wrong arguments to a library method, usually means
		# the stand-ins are smaller than the real library.
		local first
		first="$(awk -v own="$OWN_NAMES" '
			/: error: / { if (err != "" && !gap) { print err; exit } err = $0; gap = ($0 ~ /error: (package .* does not exist|.* cannot be applied|no suitable|cannot access)/); sym = ($0 ~ /error: cannot find symbol/); next }
			sym && /^ *symbol:/ { s = $0; sub(/^ *symbol: */, "", s); err = err " (" s ")" }
			sym && /^ *location:/ { gap = ($0 !~ own); sym = 0 }
			END { if (err != "" && !gap) print err }
		' "$work/compile.log" | head -1 | sed -E 's|^(\./)?.*/([A-Za-z]+\.java)|\2|')"
		rm -rf "$work"
		[[ -z "$first" ]] && return 2
		echo "Your code doesn't compile: $first"
		return 1
	fi
	local status=0
	(cd "$work" && java -cp out "$check" >run.log 2>&1) || status=1
	grep -v '^Picked up JAVA_TOOL_OPTIONS' "$work/run.log" | tail -1
	rm -rf "$work"
	return $status
}

# The student's file with comments taken out (whole-line and end-of-line //, and /* */ blocks).
code_without_comments() {
	sed -E 's|//.*$||' "$1" | tr '\n' ' ' | sed -E 's|/\*([^*]\|\*+[^*/])*\*+/| |g'
}
