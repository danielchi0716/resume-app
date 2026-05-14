#!/usr/bin/env bash
# Render ResumeCore/iosApp/Configuration/AppConfig.xcconfig from AppConfig.xcconfig.template.
# Resolution order per key: env var > ResumeCore/local.properties.
# If a key is set in neither, the script aborts.
# Used by GitHub Actions iOS build job and during local Xcode setup.

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
TEMPLATE="$ROOT/ResumeCore/iosApp/Configuration/AppConfig.xcconfig.template"
OUTPUT="$ROOT/ResumeCore/iosApp/Configuration/AppConfig.xcconfig"
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
SHARE_URL=$(require RESUME_SHARE_URL)
REPO_URL=$(require RESUME_REPO_URL)

# In xcconfig, `//` starts a comment. Insert $() to break the sequence so
# URLs survive parsing: https://example.com/ -> https:/$()/example.com/
xc_escape() { printf '%s' "$1" | sed -e 's|//|/$()/|g'; }

# Escape sed's metachars in the replacement string so values survive substitution.
sed_escape() { printf '%s' "$1" | sed -e 's/[\\&|]/\\&/g'; }

sed \
  -e "s|__RESUME_DATA_HOST__|$(sed_escape "$(xc_escape "$DATA_HOST")")|g" \
  -e "s|__RESUME_SHARE_URL__|$(sed_escape "$(xc_escape "$SHARE_URL")")|g" \
  -e "s|__RESUME_REPO_URL__|$(sed_escape "$(xc_escape "$REPO_URL")")|g" \
  "$TEMPLATE" > "$OUTPUT"

echo "Wrote $OUTPUT"
echo "  RESUME_DATA_HOST = $DATA_HOST"
echo "  RESUME_SHARE_URL = $SHARE_URL"
echo "  RESUME_REPO_URL  = $REPO_URL"
