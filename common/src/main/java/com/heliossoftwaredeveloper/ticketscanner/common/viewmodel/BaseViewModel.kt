/* (c) Helios Software Developer. All rights reserved. */
package com.heliossoftwaredeveloper.ticketscanner.common.viewmodel

import androidx.lifecycle.ViewModel
import com.heliossoftwaredeveloper.ticketscanner.common.utils.BaseSchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * ViewModel base class that will handle shared events/transactions to all ViewModels that will subclass this
 *
 * @author Ruel N. Grajo on 01/23/2022.
 */

abstract class BaseViewModel : ViewModel() {

    open val disposables: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    @Inject
    lateinit var schedulers: BaseSchedulerProvider

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}