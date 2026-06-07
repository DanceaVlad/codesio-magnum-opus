---
name: gh-project-issues
description: Reliably create GitHub issues for DanceaVlad/codesio-magnum-opus and place them in the Codesio-Magnum-Opus GitHub Project. Use when an agent must create a task, feature, bug, spike, or documentation issue, add it to https://github.com/users/DanceaVlad/projects/4, set the project Status column such as Todo, and verify the result with the GitHub CLI.
---

# GitHub Project Issues

## Overview

Create repository issues with the repo's issue-template structure, add them to user project `DanceaVlad/4`, set the project `Status`, and verify the item. Prefer the bundled script for live issue creation because GitHub Projects requires separate item and field operations.

## Project Defaults

- Repository: `DanceaVlad/codesio-magnum-opus`
- Project owner: `DanceaVlad`
- Project number: `4`
- Default project status: `Todo`
- Supported issue kinds/labels: `task`, `feature`, `bug`, `spike`, `documentation`

GitHub issue types are not currently enabled for this repository through the API. Treat the issue label and template body as the effective type unless the API starts returning issue type IDs.

## Workflow

1. Build the issue body from the matching local template shape in `.github/ISSUE_TEMPLATE/`.
2. Use the bundled script to create the issue, add it to project 4, set the project `Status`, and verify it.
3. Report the issue URL and final project status.

Use a temporary body file under `/tmp`; do not write generated issue bodies into the repository unless the user asks.

## Script Usage

Run from the repository root:

```bash
.agents/skills/gh-project-issues/scripts/create_project_issue.sh \
  --kind feature \
  --title "Add Tailwind color scheme" \
  --body-file /tmp/issue-body.md \
  --status Todo
```

The script intentionally uses `gh issue create`, `gh project item-add`, and `gh project item-edit` instead of `createIssue(projectV2Ids: ...)`. In practice, `createIssue(projectV2Ids: ...)` may create the issue without returning or materializing the project item reliably enough for immediate status editing.

## Body Templates

Feature body:

```markdown
### Goal

...

### User value

...

### Scope

- ...

### Out of scope

...
```

Task body:

```markdown
### Task

...

### Why

...

### Acceptance criteria

- [ ] ...

### Parent issue

_No response_

### Notes

...
```

For other issue kinds, read the matching `.github/ISSUE_TEMPLATE/<kind>.yml` and mirror its headings.

## Authentication And Failure Handling

- If `gh` reports missing project scope, run `gh auth refresh -h github.com -s project -s read:project` and complete the device flow.
- If the project item appears under `No Status`, set the `Status` field explicitly with `gh project item-edit`; adding an issue to a project does not set `Status`.
- If the user asks for the fewest commands, use the bundled script in one terminal command after preparing the body file.
