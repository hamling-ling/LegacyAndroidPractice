package com.example.veryoldapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.veryoldapp.test.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@UninstallModules(MainModule::class)
@RunWith(AndroidJUnit4::class)
class MainViewModelAndroidTest {

    @get:Rule(order=0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getText() {
        val viewModel = MainViewModel(AwsFakeApiImpl())
        assertEquals("Hello ViewModel", viewModel.text.value)
    }

    @Test
    fun updateText() {

        val scenario = ActivityScenario.launch(HiltTestActivity::class.java)
        scenario.onActivity { activity ->
            // at this point I could not replace MainViewModel's constructor argument through Hilt.
            val viewModel: MainViewModel = ViewModelProvider(activity)[MainViewModel::class.java]
            assertEquals("Hello ViewModel", viewModel.text.value)
        }
    }
}