package com.prody.prashant.domain.common

/**
 * A sealed class representing the result of an operation.
 * Used throughout the app for consistent error handling.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()

    data class Error(
        val exception: Throwable,
        val userMessage: String,
        val errorType: ErrorType
    ) : Result<Nothing>()

    data object Loading : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data

    fun getOrDefault(default: @UnsafeVariance T): T = getOrNull() ?: default

    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }

    suspend fun <R> suspendMap(transform: suspend (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Error) -> Unit): Result<T> {
        if (this is Error) action(this)
        return this
    }

    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)

        fun error(
            exception: Throwable,
            userMessage: String = exception.message ?: "An error occurred",
            errorType: ErrorType = ErrorType.UNKNOWN
        ): Result<Nothing> = Error(exception, userMessage, errorType)

        fun loading(): Result<Nothing> = Loading
    }
}

/**
 * Enum representing different types of errors that can occur in the app.
 */
enum class ErrorType {
    NETWORK,
    DATABASE,
    AI_SERVICE,
    VALIDATION,
    AUTHENTICATION,
    PERMISSION,
    NOT_FOUND,
    TIMEOUT,
    RATE_LIMIT,
    UNKNOWN;

    val isRetryable: Boolean
        get() = when (this) {
            NETWORK, AI_SERVICE, TIMEOUT, RATE_LIMIT -> true
            DATABASE, VALIDATION, AUTHENTICATION, PERMISSION, NOT_FOUND, UNKNOWN -> false
        }

    val userFriendlyName: String
        get() = when (this) {
            NETWORK -> "Network Error"
            DATABASE -> "Storage Error"
            AI_SERVICE -> "AI Service Error"
            VALIDATION -> "Validation Error"
            AUTHENTICATION -> "Authentication Error"
            PERMISSION -> "Permission Error"
            NOT_FOUND -> "Not Found"
            TIMEOUT -> "Request Timeout"
            RATE_LIMIT -> "Too Many Requests"
            UNKNOWN -> "Unknown Error"
        }
}

/**
 * Extension function to safely execute a block and wrap the result in Result.
 */
inline fun <T> runCatching(
    errorType: ErrorType = ErrorType.UNKNOWN,
    errorMessage: String = "An error occurred",
    block: () -> T
): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.error(e, errorMessage, errorType)
    }
}

/**
 * Extension function to safely execute a suspending block and wrap the result in Result.
 */
suspend inline fun <T> runSuspendCatching(
    errorType: ErrorType = ErrorType.UNKNOWN,
    errorMessage: String = "An error occurred",
    crossinline block: suspend () -> T
): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.error(e, errorMessage, errorType)
    }
}
