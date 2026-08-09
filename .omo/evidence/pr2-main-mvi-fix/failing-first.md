# Failing-first proof

Scenario: require the target MVI shape before implementation.

Invocation: `rg -n 'data (class|object) .*: MainIntent|when \\(intent\\)' app/src/main/java/live/lb_trip/localbalancetrip/MainViewModel.kt`

Binary observable: exit code `1` at the base commit because no operational `MainIntent` case or `when (intent)` dispatcher existed. The legacy-characterization command recorded in `baseline.md` exited `0`.

Artifact: this file. This is source characterization only; no unsupported mirroring test was added.
