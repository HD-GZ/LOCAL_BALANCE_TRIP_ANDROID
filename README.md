# Local Balance Trip

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.21-blue.svg)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/gradle-9.3.1-green.svg)](https://gradle.org/)
[![Android Gradle](https://img.shields.io/badge/AGP-9.1.1-green.svg)](https://gradle.org/)

[![minSdkVersion](https://img.shields.io/badge/minSdkVersion-29-red)](https://developer.android.com/distribute/best-practices/develop/target-sdk)
[![compileSdkVersion](https://img.shields.io/badge/compileSdkVersion-36-red)](https://developer.android.com/distribute/best-practices/develop/target-sdk)
[![targetSdkVersion](https://img.shields.io/badge/targetSdkVersion-36-red)](https://developer.android.com/distribute/best-practices/develop/target-sdk)

## 🏗 Architecture

- **MVI** — 각 feature 모듈은 `ViewModel` + `sealed Intent` + `StateFlow<UiState>` 패턴을 따른다 (`onIntent(Intent)` 단일 진입점).
- **Navigation-Compose** — 화면 전환은 `androidx.navigation.compose` 기반. 각 `impl` 모듈이 `NavGraphBuilder` 확장 함수로 자신의 그래프를 등록한다.
- **Hilt** — DI. `ViewModel`은 `@HiltViewModel`, Repository 구현체는 `data` 모듈에서 인터페이스에 바인딩.
- **Ktor** — 서버 통신 클라이언트.

## 🛠 Tech Stack

- **[Kotlin](https://kotlinlang.org/) 2.3.21**
- **[Jetpack Compose](https://developer.android.com/jetpack/compose) BOM 2026.05.01**
- **[Navigation Compose](https://developer.android.com/develop/ui/compose/navigation) 2.9.5**
- **[Hilt](https://dagger.dev/hilt/) 2.59.2**
- **[Ktor](https://ktor.io/) 3.5.0**
- **[kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) 1.11.0**
- **[Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) 1.11.0**
- **[Naver Map Compose](https://github.com/fornewid/naver-map-compose) 1.9.0** — 투어 GPS 화면

## 📦 Module Graph

```
:app
├── :core:designsystem            # LbColors, LbButton 등 DS 컴포넌트, 테마, 타이포그래피
├── :domain                       # Repository 인터페이스, UseCase
├── :data                         # Repository 구현체, Ktor HttpClient
├── :feature:home:api/impl        # 홈 (저장한 코스 목록 등)
├── :feature:settings:api/impl    # 설정
├── :feature:onboarding:api/impl  # 온보딩
├── :feature:signup:api/impl      # 회원가입
├── :feature:signin:api/impl      # 로그인
├── :feature:propensity:api/impl  # 성향 진단
├── :feature:recommendation:api/impl  # 코스 추천, 지역/코스 상세
├── :feature:tour:api/impl        # 투어 GPS 화면 (Naver Maps)
└── :feature:savedcourses:api/impl    # 저장한 코스 목록
```

### Feature 모듈 의존성 규칙

```
feature:XXX:impl  ──►  feature:XXX:api   (자신의 api)
                  ──►  feature:YYY:api   (navigate할 다른 feature의 api)
                  ─X►  feature:YYY:impl  (금지)
```

새 feature 모듈은 `/create-feature <featureName>` 스캐폴딩 스킬로 생성한다 (ViewModel + Intent + UiState + api/impl 모듈 + settings.gradle.kts 등록까지 포함).

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

### Lint & Static Analysis

CI(`.github/workflows/ci.yml`)에서 PR/푸시마다 실행되는 검사를 로컬에서 동일하게 돌릴 수 있다.

```bash
./gradlew ktlintCheck
./gradlew detekt
./gradlew lintDebug
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
