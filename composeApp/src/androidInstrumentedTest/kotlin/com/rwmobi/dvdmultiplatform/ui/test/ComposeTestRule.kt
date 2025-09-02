/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 * Sponsored by RW MobiMedia UK Limited
 *
 */

package com.rwmobi.dvdmultiplatform.ui.test

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.rwmobi.dvdmultiplatform.MainActivity

typealias ComposeTestRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
