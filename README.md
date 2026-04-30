# intellij-starter-changelists-test

The test launches a real IntelliJ IDEA Community instance, opens a
public host project, and drives the UI through Settings → Version
Control → Changelists to enable the Create changelists automatically option.

## Scenario

1. Download (or reuse cached) IntelliJ IDEA Community 2024.3.5.
2. Clone the host project - [DannyBuilder/StockMarket] (https://github.com/DannyBuilder/StockMarket)
3. Wait for indexing to finish.
4. Open Settings… via the ShowSettings action.
5. Navigate the categories tree to Version Control → Changelists.
6. Tick the Create changelists automatically checkbox (only clicks if not already on).
7. Assert the checkbox is selected.
8. Click OK to apply and close.
9. Assert the dialog has been dismissed.

## Requirements

- JDK 21 - Gradle's foojay toolchain resolver downloads one automatically if you don't have it.
- Internet access - the framework downloads the IDE installer on first run.
- ~3 GB of free disk space - for the IDE cache (`~/out/ide-tests/`).
- A real desktop session - the test drives a GUI; it cannot run in a pure headless terminal without a virtual display.

## Running the test

The repository ships with the Gradle wrapper, so no system Gradle is required.

### Windows (PowerShell)

```powershell
.\gradlew.bat test
```

### macOS / Linux

```bash
./gradlew test
```

The first run takes a few minutes because Gradle downloads its dependencies
and the framework downloads ~1 GB for the IDE distribution. Subsequent
runs reuse the cached IDE and complete in roughly 45 seconds.

### Important: do not interact with the terminal or IDE during the run

The test physically moves the mouse and click on real screen coordinates. While the test runs please watch ant try not to interfere with it. If you are running it 
from a terminal **The IDE window often opens behind the terminal.** Please make sure to minimize the terminal as soon as you run it to make sure that the mouse is on the IDE and not the temrinal.
If you are running it from an IDE this should not be a problem.

The cleanest way to run the test is to **minimize the terminal window**
after starting it, then walk away for ~45 seconds. The test runs
unattended; come back when the build reports SUCCESSFUL.


### Forcing a re-run

Gradle caches passing test results. To force the test to actually
execute again on a clean run:

```powershell
.\gradlew.bat test --rerun-tasks --no-build-cache
```

## Cross-platform notes

A few details make the test stable across operating systems:

- invokeAction("ShowSettings", now = false) 
  * uses the action ID rather than the keyboard shortcut as it is different by OS.
- or(byTitle("Settings"), byTitle("Preferences")) 
  * older macOS uses "Preferences"; modern macOS, Windows, and Linux use "Settings".
- applyVMOptionsPatch { addSystemProperty("user.language", "en") } 
  * locks the running IDE to English so accessible-name lookups resolve regardless of the host OS language.
- useRelease("2024.3.5") 
  * pins to a specific stable release.


## References

- [Plugin development docs: Integration tests for UI](https://plugins.jetbrains.com/docs/intellij/integration-tests-ui.html)