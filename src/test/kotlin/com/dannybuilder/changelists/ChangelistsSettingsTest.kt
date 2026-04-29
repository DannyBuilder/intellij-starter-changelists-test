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
import com.intellij.ide.starter.ci.CIServer
import com.intellij.ide.starter.ci.NoCIServer
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.junit5.hyphenateWithClass
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.GitHubProject
import com.intellij.ide.starter.runner.CurrentTestMethod
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


/**
 * Smoke test: verify the Starter framework can launch IntelliJ IDEA
 * Community, open a project, finish indexing, and shut down cleanly.
 *
 */
class ChangelistsSettingsTest {

    /**
     * This tests uses IntelliJ Community with a fixed version from 2024 for stability now. Will change elater
     */
    @Test
    @DisplayName("IDE launches, opens project, and finishes indexing")
    fun ideStartsCleanly() {
        val testContext = Starter.newContext(
            "changelists-settings-test/ide-start-test",
            TestCase(
                IdeProductProvider.IC,
                GitHubProject.fromGithub(
                    branchName = "main",
                    repoRelativeUrl = "DannyBuilder/StockMarket.git",
                ),
            ).useRelease("2024.3.5"),
        )

        testContext.runIdeWithDriver(launchName = "IDETest").useDriverAndCloseIde {
            waitForIndicators(5.minutes)
        }
    }

    @Test
    @DisplayName(
        "Settings -> Version Control -> Changelists: " +
                "'Create changelists automatically' can be enabled",
    )
    fun fullTest() {
        val testContext = Starter.newContext(
            "changelists-settings-test/fullTest",
            TestCase(
                IdeProductProvider.IC,
                GitHubProject.fromGithub(
                    branchName = "main",
                    repoRelativeUrl = "DannyBuilder/StockMarket.git",
                ),
            ).useRelease("2024.3.5"),
        )

        testContext.runIdeWithDriver(launchName = "FullTest").useDriverAndCloseIde {
            waitForIndicators(5.minutes)

            ideFrame {
                invokeAction("ShowSettings", now = false)

                // "Preferences" on older macOS.
                dialog(xQuery { or(byTitle("Settings"), byTitle("Preferences")) }) {
                    //We can also use clickpath?
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
                    assertTrue(createAutomatically.isSelected())

                    button("OK").click()
                }
            }
        }
    }


}