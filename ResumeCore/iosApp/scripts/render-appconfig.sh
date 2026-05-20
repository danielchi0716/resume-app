#!/usr/bin/env bash
# Render ResumeCore/iosApp/Configuration/AppConfig.xcconfig from AppConfig.xcconfig.template.
# Resolution order per key: env var > interactive prompt.
# Used by GitHub Actions iOS build job (env vars) and during local Xcode setup
# (interactive). The iOS project intentionally does NOT read local.properties —
# that file belongs to the Android (Gradle) project.

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
TEMPLATE="$ROOT/ResumeCore/iosApp/Configuration/AppConfig.xcconfig.template"
OUTPUT="$ROOT/ResumeCore/iosApp/Configuration/AppConfig.xcconfig"

# Resolve a required value. Env var wins; otherwise prompt interactively.
# Aborts when neither env nor a TTY is available (CI / non-interactive shell).
require() {
  local key="$1" val=""
  if [[ -n "${!key:-}" ]]; then
    val="${!key}"
  elif [[ -t 0 ]]; then
    while [[ -z "$val" ]]; do
      read -r -p "$key: " val
    done
  else
    echo "Missing required config '$key'. Set it as an env var (CI) or run this script in an interactive shell." >&2
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
