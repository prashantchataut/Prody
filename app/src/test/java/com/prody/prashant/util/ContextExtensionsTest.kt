package com.prody.prashant.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContextExtensionsTest {

    @Test
    fun `findActivity returns activity when context is activity`() {
        val activity = mockk<Activity>()

        val result = activity.findActivity()

        assertEquals(activity, result)
    }

    @Test
    fun `findActivity returns activity when context is wrapped activity`() {
        val activity = mockk<Activity>()
        val wrapper = mockk<ContextWrapper>()
        every { wrapper.baseContext } returns activity

        val result = wrapper.findActivity()

        assertEquals(activity, result)
    }

    @Test
    fun `findActivity returns activity when context is deeply wrapped activity`() {
        val activity = mockk<Activity>()
        val wrapper1 = mockk<ContextWrapper>()
        val wrapper2 = mockk<ContextWrapper>()

        every { wrapper2.baseContext } returns wrapper1
        every { wrapper1.baseContext } returns activity

        val result = wrapper2.findActivity()

        assertEquals(activity, result)
    }

    @Test
    fun `findActivity returns null when no activity in hierarchy`() {
        val context = mockk<Context>()

        val result = context.findActivity()

        assertNull(result)
    }
}
