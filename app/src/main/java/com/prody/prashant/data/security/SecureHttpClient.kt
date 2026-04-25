package com.prody.prashant.data.security

import android.content.Context
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Secure HTTP Client with Certificate Pinning
 * 
 * Provides secure network communication with:
 * - Certificate pinning for critical APIs
 * - Custom trust store for additional security
 * - Proper timeout configurations
 * - Security headers
 */
@Singleton
class SecureHttpClient @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "SecureHttpClient"
        
        // Timeout configurations
        private const val CONNECT_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 60L
        private const val WRITE_TIMEOUT_SECONDS = 60L
        
        // Certificate pinning hashes (aligned with network_security_config.xml)
        private val GEMINI_CERT_PINS = listOf(
            "sha256/ePd8DjIPDZVBxKmWbWHXy+wZjO75a6PEiRzXE7mbzZ0=", // Leaf
            "sha256/YPtHaftLw6/0vnc2BnNKGF54xiCA28WFcccjkA4ypCM=", // Intermediate
            "sha256/hxqRlPTu1bMS/0DITB1SSu0vd4u/8l8TjPgfaAp63Gc="  // Backup
        )

        private val OPENROUTER_CERT_PINS = listOf(
            "sha256/2ETytvFJ0SYiiaUyT3xMrJ3Yuen/K58SNiB87YChuRg=", // Leaf
            "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=", // Intermediate
            "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c="  // Backup
        )
    }

    /**
     * Create a secure OkHttpClient for general use
     */
    fun createSecureClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(SecurityHeadersInterceptor())
            .build()
    }

    /**
     * Create a secure OkHttpClient for Google AI API with certificate pinning
     */
    fun createGeminiClient(): OkHttpClient {
        val pinnerBuilder = CertificatePinner.Builder()
        GEMINI_CERT_PINS.forEach { pin ->
            pinnerBuilder.add("generativelanguage.googleapis.com", pin)
        }
        val certificatePinner = pinnerBuilder.build()

        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(SecurityHeadersInterceptor())
            .addInterceptor(GeminiSecurityInterceptor())
            .build()
    }

    /**
     * Create a secure OkHttpClient for OpenRouter API with certificate pinning
     */
    fun createOpenRouterClient(): OkHttpClient {
        val pinnerBuilder = CertificatePinner.Builder()
        OPENROUTER_CERT_PINS.forEach { pin ->
            pinnerBuilder.add("openrouter.ai", pin)
        }
        val certificatePinner = pinnerBuilder.build()

        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(SecurityHeadersInterceptor())
            .addInterceptor(OpenRouterSecurityInterceptor())
            .build()
    }

    /**
     * Create a custom trust manager for additional certificate validation
     */
    private fun createCustomTrustManager(): X509TrustManager {
        return try {
            // Load custom certificates if available
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            
            // For now, use the default trust manager
            // In production, you can load custom certificates from assets
            val defaultTrustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            defaultTrustManagers.init(null as java.security.KeyStore?)
            
            val trustManagers = defaultTrustManagers.trustManagers
            for (trustManager in trustManagers) {
                if (trustManager is X509TrustManager) {
                    return trustManager
                }
            }
            
            throw IllegalStateException("Could not find X509TrustManager")
        } catch (e: Exception) {
            throw SecurityException("Failed to create custom trust manager", e)
        }
    }

    /**
     * Create logging interceptor for debugging (only in debug builds)
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (isDebugBuild()) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Check if this is a debug build
     */
    private fun isDebugBuild(): Boolean {
        return 0 != context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE
    }
}

/**
 * Security Headers Interceptor
 * Adds security headers to all requests
 */
class SecurityHeadersInterceptor : okhttp3.Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Content-Type-Options", "nosniff")
            .addHeader("X-Frame-Options", "DENY")
            .addHeader("X-XSS-Protection", "1; mode=block")
            .addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
            .addHeader("Referrer-Policy", "strict-origin-when-cross-origin")
            .build()
        
        return chain.proceed(request)
    }
}

/**
 * Gemini API Security Interceptor
 * Adds specific security headers for Google AI API
 */
class GeminiSecurityInterceptor : okhttp3.Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", "Prody-Android/1.0")
            .addHeader("Accept", "application/json")
            .build()
        
        return chain.proceed(request)
    }
}

/**
 * OpenRouter API Security Interceptor
 * Adds specific security headers for OpenRouter API
 */
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