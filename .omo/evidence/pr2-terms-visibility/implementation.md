## Implementation

- Minimal production change: `public-by-omission fun TermsScreen` -> `internal fun TermsScreen`.
- Changed production file (relative): `feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt`.
- No callers or other production files changed.
- Intended compatibility: both callers remain in `:feature:settings:impl`; no API module exposure is required.
