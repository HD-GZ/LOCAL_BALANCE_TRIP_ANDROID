# Manual QA plan and result

Worker result: DEFERRED. This worker did not touch a physical device, as required by task scope.

Parent invocation after integration:

`adb install -r app/build/outputs/apk/debug/app-debug.apk`

`adb shell am force-stop live.lb_trip.localbalancetrip`

`adb shell monkey -p live.lb_trip.localbalancetrip 1`

`adb shell uiautomator dump /sdcard/window.xml`

`adb exec-out screencap -p > main-mvi.png`

Pass observables: an authenticated token state renders `MainNavGraph`; an unauthenticated token state renders `AuthNavGraph` or onboarding; a simulated `401` produces exactly one session-expired Toast without a crash.

Branch availability: authenticated, unauthenticated, and simulated-401 device branches are deferred to the parent integration QA channel.

Artifact: this file.
