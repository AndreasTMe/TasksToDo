package com.andreast.taskstodo.application.utils

import kotlinx.coroutines.coroutineScope

sealed class Try {
    companion object {
        fun resolve(
            onSuccess: () -> Unit = { },
            onError: (ex: Exception) -> Unit = { },
            onFinally: () -> Unit = { },
        ) {
            try {
                onSuccess()
            } catch (ex: Exception) {
                onError(ex)
            } finally {
                onFinally()
            }
        }

        suspend fun resolveAsync(
            onSuccess: () -> Unit = { },
            onError: (ex: Exception) -> Unit = { },
            onFinally: () -> Unit = { },
        ) {
            try {
                coroutineScope {
                    onSuccess()
                }
            } catch (ex: Exception) {
                onError(ex)
            } finally {
                onFinally()
            }
        }

        fun <R> resolve(
            result: R,
            onSuccess: (result: R) -> Unit = { },
            onError: (ex: Exception) -> Unit = { },
            onFinally: () -> Unit = { },
        ) {
            try {
                onSuccess(result)
            } catch (ex: Exception) {
                onError(ex)
            } finally {
                onFinally()
            }
        }

        suspend fun <R> resolveAsync(
            result: R,
            onSuccess: (result: R) -> Unit = { },
            onError: (ex: Exception) -> Unit = { },
            onFinally: () -> Unit = { },
        ) {
            try {
                coroutineScope {
                    onSuccess(result)
                }
            } catch (ex: Exception) {
                onError(ex)
            } finally {
                onFinally()
            }
        }
    }
}
