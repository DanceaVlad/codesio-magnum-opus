---
name: github-project-cli
description: "Reliably follow the GitHub Project CLI workflow for DanceaVlad/codesio-magnum-opus: create issues, add them to the Codesio-Magnum-Opus GitHub Project, create correctly named branches, make conventional commits, and create pull requests from the repository template. Use when an agent must create a task, feature, bug, spike, or documentation issue, add it to https://github.com/users/DanceaVlad/projects/4, set project Status, branch from an issue, commit work, or open a PR."
---

# GitHub Project CLI Workflow

## Overview

Follow the repository's GitHub-native workflow for issues, branches, commits, and pull requests. Issues describe planned work, branches are named from issues, commits use lightweight Conventional Commits, and pull requests use the repository PR template.

For live issue creation, prefer the bundled script because GitHub Projects requires separate item and field operations.

## Project Defaults

- Repository: `DanceaVlad/codesio-magnum-opus`
- Project owner: `DanceaVlad`
- Project number: `4`
- Default project status: `Todo`
- Supported issue kinds/labels: `task`, `feature`, `bug`, `spike`, `documentation`

GitHub issue types are not currently enabled for this repository through the API. Treat the issue label and template body as the effective type unless the API starts returning issue type IDs.

## End-To-End Workflow

1. Create or identify the issue.
2. Ensure the issue is in project `DanceaVlad/4` with the intended `Status`.
3. Create a branch from the current base branch using the repository branch naming standard.
4. Make the change and commit with the repository commit standard.
5. Push the branch upstream.
6. Create a pull request with the repository PR title standard and template body.
7. Verify the final URLs, branch, clean worktree, and relevant checks.

## Issue Workflow

1. Build the issue body from the matching local template shape in `.github/ISSUE_TEMPLATE/`.
2. Use the bundled script to create the issue, add it to project 4, set the project `Status`, and verify it.
3. Report the issue URL and final project status.

Use a temporary body file under `/tmp`; do not write generated issue bodies into the repository unless the user asks.

### Issue Naming Standard

Use this format:

```text
<Type>: <Verb in imperative> <short description>
```

Supported type prefixes:

- `Feature:`
- `Bug:`
- `Task:`
- `Spike:`
- `Documentation:`

Examples:

```text
Feature: Add Tailwind color scheme
Task: Add agent skills
Bug: Disallow overlapping court reservations
Spike: Research branch naming convention
Documentation: Document local development setup
```

## Scripts

Three scripts live in `.agents/skills/github-project-cli/scripts/`. Run them from the repository root.

### create-project-issue.sh

Creates a real repo issue, adds it to project 4, and sets Status, Area, and Type in one go.

```bash
.agents/skills/github-project-cli/scripts/create-project-issue.sh \
  "<title>" "<status>" "<area>" "<type>"
```

Arguments (all required, case-sensitive):

| Argument | Options |
|---|---|
| title | Any string |
| status | `Todo` \| `In Progress` \| `Done` |
| area | `Frontend` \| `Backend` \| `Infra` \| `Auth` |
| type | `Spike` \| `Feature` \| `Task` \| `Bug` \| `Documentation` |

Example:

```bash
.agents/skills/github-project-cli/scripts/create-project-issue.sh \
  "Feature: Add Tailwind color scheme" "Todo" "Frontend" "Feature"
```

The script intentionally uses `gh issue create`, `gh project item-add`, and `gh project item-edit` instead of `createIssue(projectV2Ids: ...)`. In practice, `createIssue(projectV2Ids: ...)` may create the issue without returning or materializing the project item reliably enough for immediate status editing.

### create-issue-branch.sh

Creates a branch linked to a GitHub issue (equivalent to clicking "Create a branch" in the issue's Development section) and checks it out locally.

```bash
.agents/skills/github-project-cli/scripts/create-issue-branch.sh \
  <issue-number> [branch-name] [base-branch]
```

- `issue-number` — required.
- `branch-name` — optional; defaults to GitHub's auto-generated name. Prefer the branch naming standard below.
- `base-branch` — optional; defaults to `main`.

Example:

```bash
.agents/skills/github-project-cli/scripts/create-issue-branch.sh \
  26 26-task-add-agent-skills
```

### create-pr.sh

Creates a pull request using `.github/pull_request_template.md`, auto-detecting the issue number from the current branch name and pre-filling `Closes #<number>`.

```bash
.agents/skills/github-project-cli/scripts/create-pr.sh [base-branch]
```

- `base-branch` — optional; defaults to `main`.
- Must be run from the issue branch (branch name must start with the issue number).

Example:

```bash
.agents/skills/github-project-cli/scripts/create-pr.sh
```

## Issue Body Templates

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

## Branch Workflow

Create the work branch from the current base branch, normally `main`, after identifying the issue number and title.

Before branching:

```bash
git status --short --branch
git branch --all --sort=-committerdate
```

If the issue details are not already known, use:

```bash
gh issue list --state open --limit 20
```

### Branch Naming Standard

Use this format:

```text
<issue-number>-<issue-type>-<issue-title-words-separated-by-hyphen>
```

Rules:

- Lowercase the issue type and words.
- Keep the issue type from the issue title: `feature`, `bug`, `task`, `spike`, or `documentation`.
- Remove punctuation such as `:`, commas, and periods.
- Replace spaces with hyphens.
- Keep the branch tied to the issue number.

Examples:

```text
26-task-add-agent-skills
20-task-add-cloudflare-tunnel-credentials
17-feature-deploy-repo-on-vps
15-feature-add-formatting-and-linting-workflows
```

Create the branch using the bundled script, which links it to the issue and checks it out:

```bash
.agents/skills/github-project-cli/scripts/create-issue-branch.sh \
  26 26-task-add-agent-skills
```

If there are uncommitted changes before switching, preserve them unless the user explicitly asks otherwise. Verify they are visible on the new branch with `git status --short --branch`.

## Commit Workflow

Stage only files related to the issue:

```bash
git status --short --branch
git add <paths>
git diff --cached --stat
```

Commit with the repository's lightweight Conventional Commit style:

```bash
git commit -m "task: add agent skills"
```

### Commit Naming Standard

Use this format:

```text
<type><(scope)>: <verb in imperative> <short description>
```

Common types:

- `feat` for product behavior or user-facing capability.
- `fix` for bug fixes.
- `docs` for documentation-only changes.
- `chore` for tooling, config, templates, and maintenance.
- `refactor` for restructuring without behavior changes.
- `test` for tests.
- `style` for formatting-only changes.
- `task` is also used in this repository for task issue work when it matches existing history.

Examples from this repository:

```text
task: add agent skills
feature: deploy repo on vps
task: add angular and springboot scaffolding
docs: document contribution guidelines
feat: add template for all issue labels
```

Use imperative wording. Do not add an AI assistant as a co-author unless the user explicitly asks. After committing, verify:

```bash
git log -1 --format=full
git status --short --branch
```

The log output should not contain a `Co-authored-by:` trailer for an AI assistant unless explicitly requested.

## Pull Request Workflow

Push the branch upstream:

```bash
git push -u origin 26-task-add-agent-skills
```

Optionally inspect recent PR titles for style reference:

```bash
gh pr list --state all --limit 10 --json number,title,headRefName,baseRefName
```

Create the PR using the bundled script, which reads the template and pre-fills `Closes #<number>` automatically:

```bash
.agents/skills/github-project-cli/scripts/create-pr.sh
```

Fill in the remaining template sections (`Summary`, `Changes`, `Testing`, `Notes`) before or after running the script by editing the PR body on GitHub.

### Pull Request Naming Standard

Use the issue title without the issue type prefix.

Rules:

- Remove `Feature:`, `Bug:`, `Task:`, `Spike:`, or `Documentation:`.
- Keep the title short and human-readable.
- Prefer sentence/title case matching recent merged PRs.
- Do not include the issue number in the title.
- Do not include the type prefix in the title.

Examples:

```text
Add agent skills
Add AI configs for codex and claude code
Add Angular and Springboot scaffolding
Document contribution guidelines
Advance k3s GitOps deployment scaffold
```

### Pull Request Body Standard

Use `.github/pull_request_template.md` and fill every section:

```markdown
## Summary

...

## Related issue

Closes #26

## Changes

- ...

## Testing

- [ ] I tested this locally
- [ ] I added or updated tests where useful
- [x] I checked that existing tests pass
- [ ] Not applicable

## Notes

...

## Screenshots

Not applicable.
```

Use `Closes #<issue-number>` when the PR fully completes the issue. For non-UI changes, `Screenshots` can be `Not applicable.`

## Authentication And Failure Handling

- If `gh` reports missing project scope, run `gh auth refresh -h github.com -s project -s read:project` and complete the device flow.
- If the project item appears under `No Status`, set the `Status` field explicitly with `gh project item-edit`; adding an issue to a project does not set `Status`.
- If the user asks for the fewest commands, use the bundled script in one terminal command after preparing the body file.
- If GitHub CLI commands fail because of network sandboxing, rerun the same command with the required escalation instead of guessing.
- If Git cannot write refs or objects because `.git` is read-only in the sandbox, rerun the exact Git operation with the required escalation.
