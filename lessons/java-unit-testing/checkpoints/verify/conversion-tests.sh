#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT
# shellcheck source=mutation.sh
source "$HERE/mutation.sh"

# DriveMathTest must pass on the real DriveMath and fail on each buggy inchesToMeters.
mutation_check "$PROJECT" DriveMathTest DriveMath inchesToMeters conversion-1 conversion-2 conversion-3
