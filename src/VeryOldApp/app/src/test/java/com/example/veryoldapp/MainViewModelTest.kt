package com.example.veryoldapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
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
        val viewModel = MainViewModel(AwsFakeApiImpl())

        val latch = CountDownLatch(1)
        val observer = Observer<String> {
            latch.countDown()
        }
        viewModel.text.observeForever(observer)

        viewModel.updateText()

        // 変更を待ち合わせる
        assertTrue(latch.await(2, TimeUnit.SECONDS))
        assertEquals("fake api call!", viewModel.text.value)

        viewModel.text.removeObserver(observer)
    }
}