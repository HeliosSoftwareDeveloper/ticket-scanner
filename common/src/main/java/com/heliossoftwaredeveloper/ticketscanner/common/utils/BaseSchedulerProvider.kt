package com.heliossoftwaredeveloper.ticketscanner.common.utils

import io.reactivex.rxjava3.core.CompletableTransformer
import io.reactivex.rxjava3.core.MaybeTransformer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.SingleTransformer

/**
 * Allow providing different types of [Scheduler]s.
 */
interface BaseSchedulerProvider {

    fun computation(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler

    fun <T> applySchedulers(): SingleTransformer<T, T>

    fun <T> applyMaybeSchedulers(): MaybeTransformer<T, T>

    fun applyCompletableSchedulers(): CompletableTransformer
}
