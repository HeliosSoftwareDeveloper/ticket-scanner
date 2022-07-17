/* (c) Helios Software Developer. All rights reserved. */
package com.heliossoftwaredeveloper.ticketscanner.common.viewmodel

import androidx.lifecycle.ViewModel
import com.heliossoftwaredeveloper.ticketscanner.common.safeDispose
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * ViewModel base class that will handle shared events/transactions to all ViewModels that will subclass this
 *
 * @author Ruel N. Grajo on 01/23/2022.
 */

abstract class BaseViewModel : ViewModel() {

    private val disposable = CompositeDisposable()

    open fun onDestroy() {
        disposable.safeDispose()
    }
    open fun onStart() {
    }

    open fun onResume() {
    }

    open fun onPause() {
    }

    open fun onStop() {
    }
}