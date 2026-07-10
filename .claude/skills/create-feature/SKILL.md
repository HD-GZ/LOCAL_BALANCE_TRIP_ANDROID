---
name: create-feature
description: |
  Scaffolds a new feature module for the LocalBalanceTrip project using ViewModel,
  StateFlow, and Navigation-Compose. Creates an api module (Route only) and an impl
  module (ViewModel, Screen, NavGraphBuilder extension), registered in
  settings.gradle.kts.
  Usage: /create-feature <featureName>
  Example: /create-feature login
license: n/a
metadata:
  author: WooJin Kong
  last-updated: '2026-07-10'
  keywords:
  - android
  - viewmodel
  - stateflow
  - navigation-compose
  - feature module
  - scaffold
  - hilt
  - compose
---

## Prerequisites

- You MUST be at the root of the LocalBalanceTrip project (`/home/kongjak/Work/Android/LocalBalanceTrip`).
- The feature name is provided as the skill argument (e.g. `/create-feature login`).
  If no argument is given, ask the user for the feature name before proceeding.

## Step 1: Derive names

From the argument, derive:
- `<name>` — all lowercase (e.g. `login`). This is used for directory paths and package names.
- `<Name>` — PascalCase (e.g. `Login`). This is used for class and object names.

If the input is already multi-word (e.g. `tripDetail`), convert to lowercase `tripdetail` for `<name>` and `TripDetail` for `<Name>`.

## Step 2: Create the api module (Route only)

The api module's only job is to publish the feature's `@Serializable` route object — the
one thing other modules (mainly `:app`) need to reference this screen without depending
on its implementation.

**`feature/<name>/api/build.gradle.kts`**
```kotlin
plugins {
    alias(libs.plugins.convention.android.feature.api)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.<name>.api"
}
```

**`feature/<name>/api/src/main/AndroidManifest.xml`**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
```

**`feature/<name>/api/src/main/java/live/lb_trip/feature/<name>/<Name>Route.kt`**
```kotlin
package live.lb_trip.feature.<name>

import kotlinx.serialization.Serializable

@Serializable
object <Name>Route
```

## Step 3: Create the impl module (ViewModel, Screen, navigation wiring)

**`feature/<name>/impl/build.gradle.kts`**
```kotlin
plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.<name>"
}

dependencies {
    implementation(projects.feature.<name>.api)
    implementation(projects.core.designsystem)
}
```
Add `implementation(projects.domain)` too if the feature calls a UseCase.

**`feature/<name>/impl/src/main/AndroidManifest.xml`**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
```

**`feature/<name>/impl/src/main/java/live/lb_trip/feature/<name>/<Name>Screen.kt`**

Start with a minimal placeholder:

```kotlin
package live.lb_trip.feature.<name>

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun <Name>Screen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("<Name> Screen")
    }
}
```

If the user specifies that the feature needs to trigger navigation, add lambda parameters to `<Name>Screen`, e.g.:
```kotlin
@Composable
internal fun <Name>Screen(
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
)
```

**`feature/<name>/impl/src/main/java/live/lb_trip/feature/<name>/<Name>Navigation.kt`**

Note: `<Name>Route` is not imported here — it lives in the `api` module but shares the
same Kotlin package (`live.lb_trip.feature.<name>`), so it's visible without an import
as long as `impl`'s `build.gradle.kts` depends on the `api` module (already set up in Step 3).

```kotlin
package live.lb_trip.feature.<name>

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.<name>Screen(
    // Add parameters here if navigation callbacks are needed, e.g.
    // onNavigateToHome: () -> Unit,
    // onNavigateToSettings: () -> Unit,
) {
    composable<<Name>Route> {
        <Name>Screen(
            // Pass navigation callbacks here if present
        )
    }
}
```

## Step 4 (Optional): Create ViewModel and state classes

Only create these files if the user says the feature needs state management or business logic. Both go in the `impl` module.

**`feature/<name>/impl/src/main/java/live/lb_trip/feature/<name>/<Name>ViewModel.kt`**
```kotlin
package live.lb_trip.feature.<name>

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class <Name>ViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(<Name>UiState())
    val uiState: StateFlow<<Name>UiState> = _uiState
}
```

**`feature/<name>/impl/src/main/java/live/lb_trip/feature/<name>/<Name>UiState.kt`**
```kotlin
package live.lb_trip.feature.<name>

data class <Name>UiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)
```

If the feature does NOT need ViewModel, mention that state/business logic can be added later using the pattern shown above.

## Step 5: Register in settings.gradle.kts

Append these two lines to `settings.gradle.kts` (after the last existing `include` line):

```kotlin
include(":feature:<name>:api")
include(":feature:<name>:impl")
```

## Step 6: Report

List all files created. Then remind the user:

> To integrate this feature into the app:
> 1. Wire `<name>Screen(...)` into the `NavHost` in `app/src/main/java/live/lb_trip/localbalancetrip/MainActivity.kt` by calling the extension function you added, e.g.:
>    ```kotlin
>    <name>Screen(
>        onNavigateToHome = { navController.navigate(HomeRoute) },
>        // ... other navigation lambdas as needed
>    )
>    ```
> 2. Add both of these to `app/build.gradle.kts` dependencies:
>    ```kotlin
>    implementation(projects.feature.<name>.api)
>    implementation(projects.feature.<name>.impl)
>    ```

## Cross-feature navigation

If a feature needs a lambda that navigates to a route owned by a *different* feature
(e.g. onboarding navigating to signup), that lambda's implementation lives in `:app`'s
`MainActivity.kt`, not inside the feature module itself — features never depend on each
other's `api` or `impl` modules. `:app` already depends on every feature's `api` module,
so it can reference any `<Name>Route` directly when wiring the NavHost.
