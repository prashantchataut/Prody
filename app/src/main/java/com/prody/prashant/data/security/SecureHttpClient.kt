package com.prody.prashant.data.security

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure HTTP Client
 *
 * Provides secure network communication with:
 * - Proper timeout configurations
 * - Security headers for outgoing requests
 * - Debug logging only in debug builds
 *
 * NOTE: Certificate pinning is handled by network_security_config.xml
 * rather than OkHttp CertificatePinner, because pin hashes must be
 * derived from actual server certificates. See:
 * res/xml/network_security_config.xml
 */
@Singleton
class SecureHttpClient @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "SecureHttpClient"

        private const val CONNECT_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 60L
        private const val WRITE_TIMEOUT_SECONDS = 60L
    }

    fun createSecureClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(SecurityHeadersInterceptor())
            .build()
    }

    fun createGeminiClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(SecurityHeadersInterceptor())
            .addInterceptor(GeminiSecurityInterceptor())
            .build()
    }

    fun createOpenRouterClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(SecurityHeadersInterceptor())
            .addInterceptor(OpenRouterSecurityInterceptor())
            .build()
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (isDebugBuild()) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    private fun isDebugBuild(): Boolean {
        return 0 != context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE
    }
}

/**
 * Security Headers Interceptor
 * Adds client-side headers for API authentication and request tracking.
 * Note: X-Content-Type-Options, X-Frame-Options, etc. are response headers
 * set by servers, not request headers — they have no effect on outgoing requests.
 */
class SecurityHeadersInterceptor : okhttp3.Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Referrer-Policy", "strict-origin-when-cross-origin")
            .build()

        return chain.proceed(request)
    }
}

class GeminiSecurityInterceptor : okhttp3.Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", "Prody-Android/1.0")
            .addHeader("Accept", "application/json")
            .build()

        return chain.proceed(request)
    }
}

class OpenRouterSecurityInterceptor : okhttp3.Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", "Prody-Android/1.0")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()

        return chain.proceed(request)
    }
}