# Shared by the checkpoint scripts: MutationCheck compiles the student's test
# class with the check's own Test/Assertions, the correct code and each mutant,
# then grades the tests (see MutationCheck.java).
#
#   mutation_check <project> <TestClass> <SubjectClass> <method> <mutant name>...
mutation_check() {
	local project="$1"
	shift
	local here runner=()
	here="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
	if command -v timeout > /dev/null; then runner=(timeout 60); fi

	set +e
	"${runner[@]}" java "$here/MutationCheck.java" "$project/src" "$1" "$2" "$3" "$here" "$WORK" "${@:4}" > "$WORK/run.log" 2>&1
	local code=$?
	set -e
	if [ "$code" = 124 ]; then
		echo "Your tests took too long to finish. Is there an endless loop in $1?"
		exit 1
	fi
	tail -1 "$WORK/run.log"
	return "$code"
}
