package com.dannybuilder.changelists

import com.intellij.driver.sdk.waitForIndicators
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.GitHubProject
import com.intellij.ide.starter.runner.CurrentTestMethod
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.minutes

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

        testContext.runIdeWithDriver().useDriverAndCloseIde {
            waitForIndicators(5.minutes)
        }
    }
}