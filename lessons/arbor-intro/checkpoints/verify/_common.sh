# Shared by the arbor-intro checks (sourced). The student's trees are every XML file in
# deploy/autos/trees except the two that ship with the lesson: Example.xml (a tree to read) and
# ReplayDemo.xml (the tree the sample log in sample-logs/ was recorded from). Text is read with comments removed and
# newlines flattened, so attribute order and line breaks don't matter.
TREES="$PROJECT/src/main/deploy/autos/trees"
shopt -s nullglob
USER_FILES=()
for f in "$TREES"/*.xml; do
	case "$(basename "$f")" in
	Example.xml | ReplayDemo.xml) ;;
	*) USER_FILES+=("$f") ;;
	esac
done
strip() { tr '\n' ' ' | sed -E 's/<!--([^-]|-[^-])*-->//g'; }
user_text() { [ ${#USER_FILES[@]} -gt 0 ] && cat "${USER_FILES[@]}" | strip || true; }
