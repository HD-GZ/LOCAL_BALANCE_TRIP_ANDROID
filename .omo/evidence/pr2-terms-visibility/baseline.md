## Baseline characterization

- Baseline commit: `529176ebf46cb7cdc96394b88620ccc00c5c5a0c`.
- Target source (relative): `feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt`.
- Source characterization: `TermsScreen` is currently declared `public` by omission (`@Composable` followed by `fun TermsScreen`, line 37); Kotlin top-level declarations are public by default.
- Caller characterization: `SettingsNavigation.kt` calls `TermsScreen` from both `termsScreen` and `privacyScreen`; both are in the same `:feature:settings:impl` module and package `live.lb_trip.feature.settings`.
- Scope note: the requested path spelling includes an extra `impl` package segment, but no such file exists at this baseline; the uniquely matching source is the relative path above.
- Baseline worktree status: clean before evidence creation.

Evidence command: `rg -n "@Composable|fun TermsScreen|TermsScreen\(" feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt feature/settings/impl/src/main/java/live/lb_trip/feature/settings/SettingsNavigation.kt`.
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/SettingsNavigation.kt:23:        TermsScreen(
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/SettingsNavigation.kt:33:        TermsScreen(
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt:36:@Composable
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt:37:fun TermsScreen(
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt:76:@Composable
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt:77:private fun TermsScreenContent(
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt:126:@Composable
feature/settings/impl/src/main/java/live/lb_trip/feature/settings/TermsScreen.kt:149:@Composable
