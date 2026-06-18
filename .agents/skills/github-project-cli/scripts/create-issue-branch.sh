#!/usr/bin/env bash

repo="DanceaVlad/codesio-magnum-opus"

issue_number="$1"
branch_name="$2"
base_branch="${3:-main}"

if [[ -z "$issue_number" ]]; then
  echo "Usage: $0 <issue-number> [branch-name] [base-branch]"
  echo ""
  echo "  issue-number:  GitHub issue number (required)"
  echo "  branch-name:   Name for the new branch (default: auto-generated from issue title)"
  echo "  base-branch:   Branch to create from (default: main)"
  echo ""
  echo "Example: $0 53"
  echo "Example: $0 53 feat/my-custom-branch-name"
  echo "Example: $0 53 feat/my-custom-branch-name develop"
  exit 1
fi

args=(--repo "$repo" --base "$base_branch" --checkout)
[[ -n "$branch_name" ]] && args+=(--name "$branch_name")

echo "Creating branch for issue #$issue_number from $base_branch..."
gh issue develop "$issue_number" "${args[@]}"
