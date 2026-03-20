package com.prody.prashant.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.mock

class ContextExtensionsTest {

    @Test
    fun `findActivity returns activity when context is an activity`() {
        val activity = mock(Activity::class.java)

        val result = activity.findActivity()

        assertEquals(activity, result)
    }

    @Test
    fun `findActivity returns activity when context is a ContextWrapper around activity`() {
        val activity = mock(Activity::class.java)
        val contextWrapper = mock(ContextWrapper::class.java)
        org.mockito.Mockito.`when`(contextWrapper.baseContext).thenReturn(activity)

        val result = (contextWrapper as Context).findActivity()

        assertEquals(activity, result)
    }

    @Test
    fun `findActivity returns activity when context is a nested ContextWrapper around activity`() {
        val activity = mock(Activity::class.java)
        val contextWrapper1 = mock(ContextWrapper::class.java)
        org.mockito.Mockito.`when`(contextWrapper1.baseContext).thenReturn(activity)
        val contextWrapper2 = mock(ContextWrapper::class.java)
        org.mockito.Mockito.`when`(contextWrapper2.baseContext).thenReturn(contextWrapper1)

        val result = (contextWrapper2 as Context).findActivity()

        assertEquals(activity, result)
    }

    @Test
    fun `findActivity returns null when context is application context`() {
        val appContext = mock(Context::class.java)

        val result = appContext.findActivity()

        assertNull(result)
    }
}
