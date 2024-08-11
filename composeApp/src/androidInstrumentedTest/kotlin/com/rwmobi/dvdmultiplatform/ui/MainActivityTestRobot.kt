/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 * Sponsored by RW MobiMedia UK Limited
 *
 */

package com.rwmobi.dvdmultiplatform.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import com.rwmobi.dvdmultiplatform.ui.test.ComposePagerTestRule
import dvdmultiplatform.composeapp.generated.resources.Res
import dvdmultiplatform.composeapp.generated.resources.content_description_dvd_logo
import org.jetbrains.compose.resources.getString

internal class MainActivityTestRobot(
    private val composeTestRule: ComposePagerTestRule,
) {
    // Actions
    fun printSemanticTree() {
        with(composeTestRule) {
            onRoot().printToLog("SemanticTree")
        }
    }

    // Assertions
    suspend fun asserDvdLogoIsDisplayed() {
        with(composeTestRule) {
            onNodeWithContentDescription(label = getString(resource = Res.string.content_description_dvd_logo)).assertIsDisplayed()
        }
    }
}
