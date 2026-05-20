#!/usr/bin/env bash
# Render web/js/config.js from config.template.js.
# Resolution order per key: env var > ResumeCore/local.properties.
# If a key is set in neither, the script aborts.
# Used by serve.sh (local dev) and the GitHub Actions deploy job.

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
TEMPLATE="$ROOT/web/js/config.template.js"
OUTPUT="$ROOT/web/js/config.js"
LOCAL_PROPS="$ROOT/ResumeCore/local.properties"

read_local_prop() {
  local key="$1"
  [[ -f "$LOCAL_PROPS" ]] || return 1
  # Match `key=value` ignoring leading whitespace and comments.
  local line
  line=$(grep -E "^[[:space:]]*${key}[[:space:]]*=" "$LOCAL_PROPS" | head -n1 || true)
  [[ -n "$line" ]] || return 1
  printf '%s' "${line#*=}" | sed -E 's/^[[:space:]]+//; s/[[:space:]]+$//'
}

require() {
  local key="$1" val=""
  if [[ -n "${!key:-}" ]]; then
    val="${!key}"
  elif val=$(read_local_prop "$key"); then
    :
  else
    echo "Missing required config '$key'. Set it as an env var (CI) or in ResumeCore/local.properties (local)." >&2
    exit 1
  fi
  printf '%s' "$val"
}

DATA_HOST=$(require RESUME_DATA_HOST)
# config.js needs an origin (scheme + host); local.properties stores host only.
DATA_ORIGIN="https://${DATA_HOST}"
REPO_URL=$(require RESUME_REPO_URL)
VERSION=$(require RESUME_VERSION)

# Escape `&` and `|` so they survive sed substitution.
escape() { printf '%s' "$1" | sed -e 's/[\\&|]/\\&/g'; }

sed \
  -e "s|__RESUME_DATA_ORIGIN__|$(escape "$DATA_ORIGIN")|g" \
  -e "s|__RESUME_REPO_URL__|$(escape "$REPO_URL")|g" \
  -e "s|__RESUME_VERSION__|$(escape "$VERSION")|g" \
  "$TEMPLATE" > "$OUTPUT"

echo "Wrote $OUTPUT (dataOrigin=$DATA_ORIGIN, repoUrl=$REPO_URL, version=$VERSION)"
