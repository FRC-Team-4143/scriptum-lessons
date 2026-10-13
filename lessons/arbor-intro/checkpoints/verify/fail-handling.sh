#!/usr/bin/env bash
set -euo pipefail
PROJECT="$1"
source "$(dirname "$0")/_common.sh"
text="$(user_text)"
if ! echo "$text" | grep -qE '<Timeout\b[^>]*\bmsec="[^"]+"'; then
	echo "Wrap a step that might never finish in a Timeout (right-click it, Wrap in..., Timeout) and set its msec."
	exit 1
fi
if ! echo "$text" | grep -qE '<Fallback\b'; then
	echo "Add a Fallback: it tries its children in order until one succeeds, so a failed step can lead to a plan B."
	exit 1
fi
echo "A Timeout and a Fallback protect your tree."
