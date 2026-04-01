package com.prody.prashant.data.security

/**
 * Custom exception for failures in the EncryptionManager.
 *
 * This ensures that encryption/decryption errors are handled explicitly
 * and do not result in sensitive data being exposed (fail-secure).
 *
 * @param message A descriptive message about the failure.
 * @param cause The underlying cause of the exception.
 */
class EncryptionException(message: String, cause: Throwable? = null) : Exception(message, cause)
