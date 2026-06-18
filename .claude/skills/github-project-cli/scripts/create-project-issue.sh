#!/usr/bin/env bash

owner="DanceaVlad"
repo="DanceaVlad/codesio-magnum-opus"
project_number=4

title="$1"
status_name="$2"
area_name="$3"
type_name="$4"

if [[ -z "$title" || -z "$status_name" || -z "$area_name" || -z "$type_name" ]]; then
  echo "Usage: $0 <title> <status> <area> <type>"
  echo ""
  echo "  status:  Todo | In Progress | Done"
  echo "  area:    Frontend | Backend | Infra | Auth"
  echo "  type:    Spike | Feature | Task | Bug | Documentation"
  echo ""
  echo "Example: $0 \"My new task\" \"Todo\" \"Backend\" \"Spike\""
  exit 1
fi

# Fetch all field data once
fields=$(gh project field-list $project_number --owner $owner --format json)
project_id=$(gh project view $project_number --owner $owner --format json | jq -r '.id')

# Extract field and option IDs dynamically
status_field_id=$(echo "$fields" | jq -r '.fields[] | select(.name == "Status") | .id')
area_field_id=$(echo "$fields" | jq -r '.fields[] | select(.name == "Area") | .id')
type_field_id=$(echo "$fields" | jq -r '.fields[] | select(.name == "Type") | .id')

status_option_id=$(echo "$fields" | jq -r --arg n "$status_name" '.fields[] | select(.name == "Status") | .options[] | select(.name == $n) | .id')
area_option_id=$(echo "$fields" | jq -r --arg n "$area_name" '.fields[] | select(.name == "Area") | .options[] | select(.name == $n) | .id')
type_option_id=$(echo "$fields" | jq -r --arg n "$type_name" '.fields[] | select(.name == "Type") | .options[] | select(.name == $n) | .id')

# Create a real repo issue and add it to the project
echo "Creating issue..."
issue_url=$(gh issue create --repo "$repo" --title "$title" --body "" | tail -1)
echo "Adding to project..."
item_id=$(gh project item-add $project_number --owner $owner --url "$issue_url" --format json | jq -r '.id')

# Set Status, Area, and Type
echo "Setting Status: $status_name..."
gh project item-edit --id "$item_id" --project-id "$project_id" \
  --field-id "$status_field_id" --single-select-option-id "$status_option_id" > /dev/null

echo "Setting Area: $area_name..."
gh project item-edit --id "$item_id" --project-id "$project_id" \
  --field-id "$area_field_id" --single-select-option-id "$area_option_id" > /dev/null

echo "Setting Type: $type_name..."
gh project item-edit --id "$item_id" --project-id "$project_id" \
  --field-id "$type_field_id" --single-select-option-id "$type_option_id" > /dev/null

echo ""
echo "Done! \"$title\""
echo "  Issue:  $issue_url"
echo "  Status: $status_name  |  Area: $area_name  |  Type: $type_name"
