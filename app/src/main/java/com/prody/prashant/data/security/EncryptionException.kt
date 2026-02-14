package com.prody.prashant.data.security

/**
 * Custom exception for encryption and decryption failures.
 * @param message A descriptive error message.
 * @param cause The original exception that caused the failure.
 */
class EncryptionException(message: String, cause: Throwable? = null) : Exception(message, cause)
