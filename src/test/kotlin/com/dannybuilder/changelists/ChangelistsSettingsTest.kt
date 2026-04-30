package com.dannybuilder.changelists

import com.intellij.driver.sdk.invokeAction
import com.intellij.driver.sdk.ui.components.UiComponent.Companion.waitFound
import com.intellij.driver.sdk.ui.components.button
import com.intellij.driver.sdk.ui.components.checkBox
import com.intellij.driver.sdk.ui.components.dialog
import com.intellij.driver.sdk.ui.components.ideFrame
import com.intellij.driver.sdk.ui.components.tree
import com.intellij.driver.sdk.ui.xQuery
import com.intellij.driver.sdk.waitForIndicators
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.GitHubProject
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


class ChangelistsSettingsTest {

    /**
     * Integration UI test for IntelliJ IDEA's Changelists settings page.
     *
     * Drives a real IDE through Settings → Version Control → Changelists,
     * toggles the "Create changelists automatically" checkbox, and applies.
     * Built on the JetBrains intellij-ide-starter framework + Driver SDK.
     */
    @Test
    @DisplayName("Settings → Version Control → Changelists")
    @Timeout(value = 15, unit = TimeUnit.MINUTES)
    fun enableCreateChangelistsAutomatically() {
        val testContext = Starter.newContext(
            "changelists-settings-test/enable-create-changelists-automatically",
            TestCase(
                IdeProductProvider.IC,
                GitHubProject.fromGithub(
                    branchName = "main",
                    repoRelativeUrl = "DannyBuilder/StockMarket.git",
                ),
            ).useRelease("2024.3.5"),
            ).applyVMOptionsPatch {
                // Lock the IDE to English so "Create changelists automatically" always matches
                addSystemProperty("user.language", "en")
                addSystemProperty("user.country", "US")
            }

        testContext.runIdeWithDriver().useDriverAndCloseIde {
            waitForIndicators(5.minutes)

            ideFrame {
                invokeAction("ShowSettings", now = false)

                // "Preferences" on older macOS.
                val settingsDialog = dialog(xQuery {
                    or(byTitle("Settings"), byTitle("Preferences"))
                })

                settingsDialog.apply {
                    val settingsTree = tree()
                    settingsTree.waitFound(timeout = 15.seconds)
                    settingsTree.clickPath("Version Control", "Changelists")

                    // Get the correct checkbox and check it only if it is not already checked
                    val createAutomatically = checkBox(xQuery {
                        byAccessibleName("Create changelists automatically")
                    })
                    createAutomatically.waitFound(timeout = 15.seconds)

                    if (!createAutomatically.isSelected()) {
                        createAutomatically.click()
                    }
                    assertTrue(createAutomatically.isSelected()) {
                        "checkbox should be selected after click."
                    }

                    button("OK").click()
                }
                assertFalse(settingsDialog.present()) {
                    "Settings dialog should be dismissed after clicking OK."
                }
            }
        }
    }


}