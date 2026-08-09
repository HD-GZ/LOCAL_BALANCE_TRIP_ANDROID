# Cleanup receipt

- App force-stopped with `adb shell am force-stop live.lb_trip.localbalancetrip`.
- Remote `/sdcard/window.xml` removed.
- Temporary local `/tmp/pr2-fix-*.png` captures removed; only sanitized PNGs listed in `checksums.txt` remain under this evidence directory.
- Wi-Fi restored/enabled with `adb shell svc wifi enable`; no Wi-Fi disable was left active.
- Orientation restored to automatic portrait: `accelerometer_rotation=1`, `user_rotation=0`.
- Final `adb shell pidof live.lb_trip.localbalancetrip` returned no PID.
- Session state at cleanup is logged-out/onboarding because the bounded authorized relogin did not transition back to authenticated home; no data clear or uninstall was performed.
- No credentials, tokens, raw UI XML, raw logs, email addresses, device serials/models, transport IDs, or network identifiers are retained.
