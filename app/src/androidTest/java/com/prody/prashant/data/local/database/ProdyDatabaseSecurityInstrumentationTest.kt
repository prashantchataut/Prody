package com.prody.prashant.data.local.database

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProdyDatabaseSecurityInstrumentationTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Before
    fun setup() {
        context.deleteDatabase(ProdyDatabase.DATABASE_NAME)
        ProdyDatabase.resetForTests()
    }

    @After
    fun tearDown() {
        ProdyDatabase.resetForTests()
        context.deleteDatabase(ProdyDatabase.DATABASE_NAME)
    }

    @Test
    fun secureInitFailure_doesNotCreatePlaintextDatabaseFile() {
        ProdyDatabase.secureContextInitializer = {
            throw IllegalStateException("Synthetic secure init failure")
        }

        var thrown: Throwable? = null
        try {
            ProdyDatabase.getInstance(context)
        } catch (t: Throwable) {
            thrown = t
        }

        assertTrue(
            "Expected secure init exception to be thrown",
            thrown is ProdyDatabase.Companion.SecureDatabaseInitializationException
        )

        val status = ProdyDatabase.secureDatabaseStatus
        assertTrue(status is ProdyDatabase.Companion.SecureDatabaseStatus.Blocked)

        val dbFile = context.getDatabasePath(ProdyDatabase.DATABASE_NAME)
        assertFalse(
            "Unencrypted fallback database file must not be created after secure init failure",
            dbFile.exists()
        )
    }

    @Test
    fun secureInitFailure_setsGuidedRemediationState() {
        ProdyDatabase.secureContextInitializer = {
            throw SecurityException("Keystore unavailable")
        }

        try {
            ProdyDatabase.getInstance(context)
        } catch (_: Throwable) {
            // Expected
        }

        val status = ProdyDatabase.secureDatabaseStatus as ProdyDatabase.Companion.SecureDatabaseStatus.Blocked
        assertEquals("Secure local storage is unavailable.", status.reason)
        assertTrue("Expected remediation steps", status.remediationSteps.isNotEmpty())
        assertTrue("Expected correlation id", status.correlationId.isNotBlank())
    }
}
