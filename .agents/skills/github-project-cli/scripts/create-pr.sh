#!/usr/bin/env bash

repo="DanceaVlad/codesio-magnum-opus"
repo_root=$(git rev-parse --show-toplevel)
template="$repo_root/.github/pull_request_template.md"
base_branch="${1:-main}"

# Extract issue number from current branch name (e.g. "53-spike-new-task" → 53)
current_branch=$(git rev-parse --abbrev-ref HEAD)
issue_number=$(echo "$current_branch" | grep -oP '^\d+')

if [[ -z "$issue_number" ]]; then
  echo "Error: could not extract issue number from branch \"$current_branch\"."
  echo "Branch must start with the issue number (e.g. 53-spike-new-task)."
  exit 1
fi

if [[ ! -f "$template" ]]; then
  echo "Error: PR template not found at $template"
  exit 1
fi

# Fetch issue title to use as PR title
echo "Fetching issue #$issue_number..."
issue_title=$(gh issue view "$issue_number" --repo "$repo" --json title --jq '.title')

# Pre-fill "Closes #" in the template body
body=$(sed "s/Closes #$/Closes #$issue_number/" "$template")

echo "Creating PR for issue #$issue_number: $issue_title"
gh pr create \
  --repo "$repo" \
  --base "$base_branch" \
  --head "$current_branch" \
  --title "$issue_title" \
  --body "$body"
