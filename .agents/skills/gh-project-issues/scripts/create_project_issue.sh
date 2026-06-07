#!/usr/bin/env bash
set -euo pipefail

repo="DanceaVlad/codesio-magnum-opus"
owner="DanceaVlad"
project_number="4"
status="Todo"
kind=""
title=""
body_file=""

usage() {
  cat <<'USAGE'
Usage:
  create_project_issue.sh --kind <task|feature|bug|spike|documentation> --title <title> --body-file <path> [--status <name>]

Creates a GitHub issue in DanceaVlad/codesio-magnum-opus, adds it to project 4,
sets the project Status field, and prints verified JSON.
USAGE
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --kind)
      kind="${2:-}"
      shift 2
      ;;
    --title)
      title="${2:-}"
      shift 2
      ;;
    --body-file)
      body_file="${2:-}"
      shift 2
      ;;
    --status)
      status="${2:-}"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

if [[ -z "$kind" || -z "$title" || -z "$body_file" ]]; then
  usage >&2
  exit 2
fi

case "$kind" in
  task|feature|bug|spike|documentation) ;;
  *)
    echo "Unsupported kind: $kind" >&2
    exit 2
    ;;
esac

if [[ ! -f "$body_file" ]]; then
  echo "Body file does not exist: $body_file" >&2
  exit 2
fi

if ! command -v gh >/dev/null 2>&1; then
  echo "gh is required" >&2
  exit 127
fi

if ! command -v jq >/dev/null 2>&1; then
  echo "jq is required" >&2
  exit 127
fi

prefix="$(tr '[:lower:]' '[:upper:]' <<<"${kind:0:1}")${kind:1}"
case "$title" in
  "$prefix: "*) full_title="$title" ;;
  *) full_title="$prefix: $title" ;;
esac

issue_url="$(gh issue create \
  --repo "$repo" \
  --title "$full_title" \
  --label "$kind" \
  --body-file "$body_file")"

item_id="$(gh project item-add "$project_number" \
  --owner "$owner" \
  --url "$issue_url" \
  --format json \
  --jq '.id')"

project_json="$(gh project view "$project_number" --owner "$owner" --format json)"
project_id="$(jq -r '.id' <<<"$project_json")"

fields_json="$(gh project field-list "$project_number" --owner "$owner" --format json)"
status_field_id="$(jq -r '.fields[] | select(.name == "Status") | .id' <<<"$fields_json")"
status_option_id="$(jq -r --arg status "$status" '.fields[] | select(.name == "Status") | .options[] | select(.name == $status) | .id' <<<"$fields_json")"

if [[ -z "$status_field_id" || "$status_field_id" == "null" ]]; then
  echo "Could not find Status field in project $owner/$project_number" >&2
  exit 1
fi

if [[ -z "$status_option_id" || "$status_option_id" == "null" ]]; then
  echo "Could not find Status option '$status' in project $owner/$project_number" >&2
  exit 1
fi

gh project item-edit \
  --project-id "$project_id" \
  --id "$item_id" \
  --field-id "$status_field_id" \
  --single-select-option-id "$status_option_id" >/dev/null

issue_number="${issue_url##*/}"

gh project item-list "$project_number" \
  --owner "$owner" \
  --limit 100 \
  --format json \
  | jq --argjson issue_number "$issue_number" '.items[] | select(.content.number == $issue_number) | {title, status, url: .content.url, labels}'
