package com.heliossoftwaredeveloper.ticketscanner.common.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.CompletableTransformer
import io.reactivex.rxjava3.core.MaybeTransformer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Provides different types of schedulers.
 */
class SchedulerProvider // Prevent direct instantiation.
private constructor() : BaseSchedulerProvider {

    override fun <T> applySchedulers(): SingleTransformer<T, T> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> applyMaybeSchedulers(): MaybeTransformer<T, T> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun applyCompletableSchedulers(): CompletableTransformer {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    companion object {

        private var INSTANCE: SchedulerProvider? = null

        fun getInstance(): SchedulerProvider =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SchedulerProvider().also { INSTANCE = it }
            }
    }
}
