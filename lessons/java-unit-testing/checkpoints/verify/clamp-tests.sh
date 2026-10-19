#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT
# shellcheck source=mutation.sh
source "$HERE/mutation.sh"

# DriveMathTest must pass on the real DriveMath and fail on each buggy clamp.
mutation_check "$PROJECT" DriveMathTest DriveMath clamp clamp-1 clamp-2 clamp-3
