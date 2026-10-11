# Shared by the tree checks (sourced). The autonomous trees are the XML files in deploy/autos/trees,
# read with comments removed and newlines flattened, so attribute order and line breaks (whatever
# Arbor writes) don't matter.
TREES="$PROJECT/src/main/deploy/autos/trees"
TREE="$TREES/PickupAndScore.xml"
strip() { tr '\n' ' ' | sed -E 's/<!--([^-]|-[^-])*-->//g'; }
all_trees() {
	shopt -s nullglob
	local files=("$TREES"/*.xml)
	[ ${#files[@]} -gt 0 ] && cat "${files[@]}" | strip || true
}
