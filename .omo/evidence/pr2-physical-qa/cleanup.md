# PR2 physical QA cleanup receipt

- App process teardown: `adb shell am force-stop live.lb_trip.localbalancetrip` completed successfully.
- Device layout dump teardown: `window.xml` removed from the device; final check reported `absent`.
- Temporary local captures and pulled XML: all `pr2-*` temporary files removed; final count `0`.
- Connectivity: Wi-Fi restored to its baseline enabled state (`wifi_on=2`); airplane-mode setting remained at its baseline; no mobile-data change was made.
- Orientation: restored to baseline `user_rotation=0`, `accelerometer_rotation=1`.
- App data/session: preserved; no clear-data or uninstall operation was used.
- Final process check: `pidof live.lb_trip.localbalancetrip` returned no PID.
- No QA-launched child process, port, browser, container, or temporary capture remained. Existing desktop processes were not modified.
- Retained evidence is limited to sanitized PNGs, this receipt, the report, and checksums.
