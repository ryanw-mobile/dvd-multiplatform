/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 * Sponsored by RW MobiMedia UK Limited
 *
 */

package com.rwmobi.dvdmultiplatform.ui

import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import com.rwmobi.dvdmultiplatform.ui.test.ComposePagerTestRule

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
}
