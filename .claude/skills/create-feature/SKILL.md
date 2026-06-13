---
name: create-feature
description: |
  Scaffolds a new Circuit feature module for the LocalBalanceTrip project.
  Creates the api (Screen) and impl (State, Event, Presenter, Ui, Module) modules
  and registers them in settings.gradle.kts. Usage: /create-feature <featureName>
  Example: /create-feature login
license: n/a
metadata:
  author: WooJin Kong
  last-updated: '2026-06-13'
  keywords:
  - android
  - circuit
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

## Step 2: Create the api module

Create the following two files:

**`feature/<name>/api/build.gradle.kts`**
```kotlin
plugins {
    alias(libs.plugins.convention.android.feature.api)
}

android {
    namespace = "live.lb_trip.feature.<name>.api"
}
```

**`feature/<name>/api/src/main/java/live/lb_trip/feature/<name>/<Name>Screen.kt`**
```kotlin
package live.lb_trip.feature.<name>

import android.os.Parcelable
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object <Name>Screen : Screen, Parcelable
```

## Step 3: Create the impl module

Create the following six files:

**`feature/<name>/impl/build.gradle.kts`**
```kotlin
plugins {
    alias(libs.plugins.convention.android.feature.impl)
}

android {
    namespace = "live.lb_trip.feature.<name>"
}

dependencies {
    implementation(projects.feature.<name>.api)
    implementation(projects.domain)
}
```

**`feature/<name>/impl/src/main/java/live/lb_trip/feature/<name>/<Name>State.kt`**
```kotlin
package live.lb_trip.feature.<name>

import com.slack.circuit.runtime.CircuitUiState

data class <Name>State(
    val eventSink: (<Name>Event) -> Unit = {}
) : CircuitUiState
```

**`feature/<name>/impl/src/main/java/live/lb_trip/feature/<name>/<Name>Event.kt`**
```kotlin
package live.lb_trip.feature.<name>

import com.slack.circuit.runtime.CircuitUiEvent

sealed interface <Name>Event : CircuitUiEvent
```

**`feature/<name>/impl/src/main/java/live/lb_trip/feature/<name>/<Name>Presenter.kt`**
```kotlin
package live.lb_trip.feature.<name>

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class <Name>Presenter @AssistedInject constructor(
    @Assisted private val screen: <Name>Screen,
    @Assisted private val navigator: Navigator,
) : Presenter<<Name>State> {

    @AssistedFactory
    interface Factory {
        fun create(screen: <Name>Screen, navigator: Navigator): <Name>Presenter
    }

    @Composable
    override fun present(): <Name>State {
        return <Name>State()
    }
}
```

**`feature/<name>/impl/src/main/java/live/lb_trip/feature/<name>/<Name>Ui.kt`**
```kotlin
package live.lb_trip.feature.<name>

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.ui.Ui

class <Name>Ui : Ui<<Name>State> {
    @Composable
    override fun Content(state: <Name>State, modifier: Modifier) {
        Box(modifier = modifier.fillMaxSize())
    }
}
```

**`feature/<name>/impl/src/main/java/live/lb_trip/feature/<name>/<Name>Module.kt`**
```kotlin
package live.lb_trip.feature.<name>

import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

class <Name>PresenterFactory @Inject constructor(
    private val presenterFactory: <Name>Presenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is <Name>Screen -> presenterFactory.create(screen, navigator)
            else -> null
        }
}

class <Name>UiFactory @Inject constructor() : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is <Name>Screen -> <Name>Ui()
            else -> null
        }
}

@Module
@InstallIn(SingletonComponent::class)
interface <Name>Module {
    @Binds
    @IntoSet
    fun bind<Name>PresenterFactory(factory: <Name>PresenterFactory): Presenter.Factory

    @Binds
    @IntoSet
    fun bind<Name>UiFactory(factory: <Name>UiFactory): Ui.Factory
}
```

## Step 4: Register in settings.gradle.kts

Append these two lines to `settings.gradle.kts` (after the last existing `include` line):

```kotlin
include(":feature:<name>:api")
include(":feature:<name>:impl")
```

## Step 5: Report

List all files created. Then remind the user:

> To navigate **to** `<Name>Screen` from another feature, add `implementation(projects.feature.<name>.api)` to that feature's `impl/build.gradle.kts` dependencies.
