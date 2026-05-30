# Local Balance Trip

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.21-blue.svg)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/gradle-9.3.1-green.svg)](https://gradle.org/)
[![Android Gradle](https://img.shields.io/badge/AGP-9.1.1-green.svg)](https://gradle.org/)

[![minSdkVersion](https://img.shields.io/badge/minSdkVersion-29-red)](https://developer.android.com/distribute/best-practices/develop/target-sdk)
[![compileSdkVersion](https://img.shields.io/badge/compileSdkVersion-36-red)](https://developer.android.com/distribute/best-practices/develop/target-sdk)
[![targetSdkVersion](https://img.shields.io/badge/targetSdkVersion-36-red)](https://developer.android.com/distribute/best-practices/develop/target-sdk)

## 🛠 Tech Stack

- **[Kotlin](https://kotlinlang.org/) 2.3.21**
- **[Jetpack Compose](https://developer.android.com/jetpack/compose) BOM 2026.05.01**
- **[Circuit](https://slackhq.github.io/circuit/) 0.33.1**
- **[Hilt](https://dagger.dev/hilt/) 2.59.2**
- **[Ktor](https://ktor.io/) 3.5.0**
- **[kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) 1.11.0**
- **[Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) 1.11.0**

## 📦 Module Graph

```
:app
├── :core:designsystem       # 테마, 색상, 타이포그래피
├── :domain                  # Repository 인터페이스, UseCase
├── :data                    # Repository 구현체, Ktor HttpClient
├── :feature:home:api        # HomeScreen
├── :feature:home:impl       # HomePresenter, HomeUi, HomeModule
├── :feature:settings:api    # SettingsScreen
└── :feature:settings:impl   # SettingsPresenter, SettingsUi, SettingsModule
```

### Feature 모듈 의존성 규칙

```
feature:XXX:impl  ──►  feature:XXX:api   (자신의 api)
                  ──►  feature:YYY:api   (navigate할 다른 feature의 api)
                  ─X►  feature:YYY:impl  (금지)
```

## 🚀 Getting Started

### Requirements

- Android Studio Meerkat (2024.3.1) 이상
- JDK 17

### Build

```bash
# Stage (debug)
./gradlew assembleDebug

# Production (release)
./gradlew assembleRelease
```

## 📄 License

```
MIT License

Copyright (c) 2026 HD-GZ

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
