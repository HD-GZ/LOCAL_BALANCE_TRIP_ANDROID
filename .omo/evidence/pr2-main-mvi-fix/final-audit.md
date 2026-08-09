# Final audit receipt

Pre-commit base verification invocation: `git rev-parse HEAD`

Binary observable: the assigned base commit matched the required value supplied with this task.

Post-commit exact-SHA verification invocation: `git rev-parse HEAD && git status --short && git show --check --format=fuller --stat HEAD`

Binary observable required: a 40-character commit identifier, no working-tree output, and no whitespace errors. The exact committed SHA is returned with the task delivery because a tracked file cannot contain its own final Git object identifier without changing that identifier.

Scope audit: only `app/src/main/java/live/lb_trip/localbalancetrip/MainViewModel.kt` and the sanitized files in this evidence directory are staged for the atomic commit.

Artifact: this file.
