## UltraQA matrix

| Probe | Result | Evidence |
|---|---|---|
| Stale state | N/A — visibility-only compile change; no persisted state touched. | Source diff + targeted gates. |
| Dirty worktree | PASS — status checked before and after; only intended source plus scoped evidence are present. | `git status --short --untracked-files=all` captured in final audit. |
| Misleading success output | PASS — gate command exit code 0 and `BUILD SUCCESSFUL` both verified; no partial log used as proof. | `gates.md`. |
| Repeated interruption/hung command | PASS — bounded `timeout 240s` wrapper used; command completed in 44s. | `gates.md` invocation/result. |
| Malformed input/prompt injection | N/A — no parser or untrusted external text involved. | Scope characterization. |
| Cancel/resume | N/A — no interactive flow. | Scope characterization. |
