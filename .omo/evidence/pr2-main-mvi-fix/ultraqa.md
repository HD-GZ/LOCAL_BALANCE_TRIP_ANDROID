# UltraQA matrix

| Class | Scenario and invocation | Binary observable / result |
| --- | --- | --- |
| Stale state | Force-stop and relaunch | N/A on this worker: device access is explicitly out of scope. Deferred in `manual-qa-plan.md`. |
| Dirty worktree | `git status --short` before work and before commit | Baseline exit code `0` with empty output. Pre-commit status is limited to the assigned source and sanitized evidence. Post-commit check is recorded in `final-audit.md`. |
| Misleading success output | Bounded compile and static commands in `gates.md` | Each returned exit code `0` and explicit `BUILD SUCCESSFUL`, not merely task-looking output. |
| Interruption or hang | `timeout 240s` compile; `timeout 300s` static checks | Both completed inside their bounds with exit code `0`; no hung command was observed. |
| Flakiness | Targeted compile/static checks | No flaky failure occurred. After the initial-token guard was added, the targeted compile and static checks were rerun against the changed source; bounded terminal-tail invocations preserved the required exit code and success marker. |
| Malformed input, prompt injection, cancel/resume | No parser or interactive worker surface exists in this change | N/A. |

Artifact: this file. No credentials, tokens, device identifiers, raw logs, or absolute paths are stored.
