## Failing-first proof

Invocation: `rg -q "^internal fun TermsScreen" feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt`
Exit code: 1 (expected non-zero at baseline because declaration is public-by-omission).
Observed baseline declaration: `fun TermsScreen`.
This is a source-characterization gate, not an application failure.
