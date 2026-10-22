# Shared by the subtree and main-tree checks (sourced after _common.sh). For each of the
# student's files: the trees it defines, the SubTrees its trees use, and its main tree. A file's
# <TreeNodesModel> also lists <SubTree ID=...> entries (a tree's own settings), which are not uses,
# so only the text before it is searched for uses.
DEFINED=()  # "file|tree" for every tree the student defined
USED=()     # every tree ID used as a SubTree somewhere
MAINS=()    # "file|tree" for every file that names a main tree
for f in "${USER_FILES[@]}"; do
	text="$(strip < "$f")"
	body="${text%%<TreeNodesModel*}"
	for id in $(echo "$body" | grep -oE '<BehaviorTree\b[^>]*\bID="[^"]+"' | sed -E 's/.*ID="([^"]+)"/\1/' || true); do
		DEFINED+=("$f|$id")
	done
	for id in $(echo "$body" | grep -oE '<SubTree\b[^>]*\bID="[^"]+"' | sed -E 's/.*ID="([^"]+)"/\1/' || true); do
		USED+=("$id")
	done
	main="$(echo "$text" | grep -oE '\bmain_tree_to_execute="[^"]+"' | head -1 | sed -E 's/.*="([^"]+)"/\1/' || true)"
	[ -n "$main" ] && MAINS+=("$f|$main")
done
defined_anywhere() { local id="$1" d; for d in "${DEFINED[@]}"; do [ "${d#*|}" = "$id" ] && return 0; done; return 1; }
defined_in() { local file="$1" id="$2"; local d; for d in "${DEFINED[@]}"; do [ "$d" = "$file|$id" ] && return 0; done; return 1; }
is_used() { local id="$1" u; for u in "${USED[@]}"; do [ "$u" = "$id" ] && return 0; done; return 1; }
# True if the tree has at least one node in it (an empty or self-closing tree has none).
has_steps() {
	local id="$1" f
	for f in "${USER_FILES[@]}"; do
		strip < "$f" | grep -qE "<BehaviorTree\b[^>]*\bID=\"$id\"[^>/]*>[[:space:]]*<[A-Za-z]" && return 0
	done
	return 1
}
