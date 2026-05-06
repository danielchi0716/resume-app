#!/usr/bin/env bash
# Local dev server for the resume site.
# Required because the page uses fetch() to load JSON, which browsers block on file:// origins.
# Usage:
#   ./serve.sh        # default port 8000
#   ./serve.sh 9000   # custom port

set -euo pipefail

PORT="${1:-8000}"
ROOT="$(cd "$(dirname "$0")" && pwd)"

# Local dev marker — CI overrides this with the deploy version.
RESUME_VERSION="${RESUME_VERSION:-local-dev}" "$ROOT/scripts/render-config.sh"

echo "Serving $ROOT on http://localhost:$PORT"
echo "  TC: http://localhost:$PORT/index.html"
echo "  EN: http://localhost:$PORT/index_en.html"
echo "Ctrl+C to stop."
echo ""

cd "$ROOT"
exec python3 -m http.server "$PORT"
