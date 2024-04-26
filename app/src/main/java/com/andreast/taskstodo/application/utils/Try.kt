package com.andreast.taskstodo.application.utils

import kotlinx.coroutines.coroutineScope

sealed class Try {
    companion object {
        fun resolve(
            onSuccess: (() -> Unit)? = null,
            onError: ((ex: Exception) -> Unit)? = null,
            onFinally: (() -> Unit)? = null,
        ) {
            assert(onSuccess != null || onError != null || onFinally != null)

            try {
                if (onSuccess != null) {
                    onSuccess()
                }
            } catch (ex: Exception) {
                if (onError != null) {
                    onError(ex)
                }
            } finally {
                if (onFinally != null) {
                    onFinally()
                }
            }
        }

        suspend fun resolveAsync(
            onSuccess: (() -> Unit)? = null,
            onError: ((ex: Exception) -> Unit)? = null,
            onFinally: (() -> Unit)? = null,
        ) {
            assert(onSuccess != null || onError != null || onFinally != null)

            try {
                if (onSuccess != null) {
                    coroutineScope {
                        onSuccess()
                    }
                }
            } catch (ex: Exception) {
                if (onError != null) {
                    onError(ex)
                }
            } finally {
                if (onFinally != null) {
                    onFinally()
                }
            }
        }

        fun <R> resolve(
            result: R,
            onSuccess: ((result: R) -> Unit)? = null,
            onError: ((ex: Exception) -> Unit)? = null,
            onFinally: (() -> Unit)? = null,
        ) {
            assert(onSuccess != null || onError != null || onFinally != null)

            try {
                if (onSuccess != null) {
                    onSuccess(result)
                }
            } catch (ex: Exception) {
                if (onError != null) {
                    onError(ex)
                }
            } finally {
                if (onFinally != null) {
                    onFinally()
                }
            }
        }

        suspend fun <R> resolveAsync(
            result: R,
            onSuccess: ((result: R) -> Unit)? = null,
            onError: ((ex: Exception) -> Unit)? = null,
            onFinally: (() -> Unit)? = null,
        ) {
            assert(onSuccess != null || onError != null || onFinally != null)

            try {
                if (onSuccess != null) {
                    coroutineScope {
                        onSuccess(result)
                    }
                }
            } catch (ex: Exception) {
                if (onError != null) {
                    onError(ex)
                }
            } finally {
                if (onFinally != null) {
                    onFinally()
                }
            }
        }
    }
}
