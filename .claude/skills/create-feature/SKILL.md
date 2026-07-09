---
name: create-feature
description: |
  Scaffolds a new feature module for the LocalBalanceTrip project using ViewModel,
  StateFlow, and Navigation-Compose. Creates a single module with a composable screen
  and navigation extension, registered in settings.gradle.kts.
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

## Step 2: Create the module directory structure

Create the following files for a single module at `feature/<name>/`:

**`feature/<name>/build.gradle.kts`**
```kotlin
plugins {
    alias(libs.plugins.convention.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.<name>"
}

dependencies {
    implementation(projects.core.designsystem)
}
```

**`feature/<name>/src/main/AndroidManifest.xml`**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
```

**`feature/<name>/src/main/java/live/lb_trip/feature/<name>/<Name>Screen.kt`**

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

**`feature/<name>/src/main/java/live/lb_trip/feature/<name>/<Name>Navigation.kt`**
```kotlin
package live.lb_trip.feature.<name>

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object <Name>Route

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

## Step 3 (Optional): Create ViewModel and state classes

Only create these files if the user says the feature needs state management or business logic:

**`feature/<name>/src/main/java/live/lb_trip/feature/<name>/<Name>ViewModel.kt`**
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

**`feature/<name>/src/main/java/live/lb_trip/feature/<name>/<Name>UiState.kt`**
```kotlin
package live.lb_trip.feature.<name>

data class <Name>UiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)
```

If the feature does NOT need ViewModel, mention that state/business logic can be added later using the pattern shown above.

## Step 4: Register in settings.gradle.kts

Append exactly one line to `settings.gradle.kts` (after the last existing `include` line):

```kotlin
include(":feature:<name>")
```

## Step 5: Report

List all files created. Then remind the user:

> To integrate this feature into the app:
> 1. Wire `<name>Screen(...)` into the `NavHost` in `app/src/main/java/live/lb_trip/localbalancetrip/MainActivity.kt` by calling the extension function you added, e.g.:
>    ```kotlin
>    <name>Screen(
>        onNavigateToHome = { navController.navigate(HomeRoute) },
>        // ... other navigation lambdas as needed
>    )
>    ```
> 2. Add `implementation(projects.feature.<name>)` to `app/build.gradle.kts` dependencies.
