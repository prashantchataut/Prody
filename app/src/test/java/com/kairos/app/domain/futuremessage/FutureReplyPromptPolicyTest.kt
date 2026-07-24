package com.kairos.app.domain.futuremessage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class FutureReplyPromptPolicyTest {
    @Test
    fun `contextual prompt is stable for the same age`() {
        assertEquals(
            FutureReplyPromptPolicy.contextualPrompt(412),
            FutureReplyPromptPolicy.contextualPrompt(412)
        )
    }

    @Test
    fun `next prompt advances instead of choosing randomly`() {
        val current = FutureReplyPromptPolicy.contextualPrompt(7)
        assertNotEquals(current, FutureReplyPromptPolicy.nextPrompt(current))
    }

    @Test
    fun `unknown prompt starts at the first prompt`() {
        val first = FutureReplyPromptPolicy.contextualPrompt(0)
        assertEquals(first, FutureReplyPromptPolicy.nextPrompt("not in the catalogue"))
    }
}
