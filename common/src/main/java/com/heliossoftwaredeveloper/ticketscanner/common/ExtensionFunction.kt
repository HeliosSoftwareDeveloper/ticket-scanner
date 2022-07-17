/* (c) Helios Software Developer. All rights reserved. */
package com.heliossoftwaredeveloper.ticketscanner.common

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * File that contains extension functions
 *
 * @author Ruel N. Grajo on 01/23/2022.
 */

/**
 * Extension function to set the text value of textView. It also set the visibility depending on the message value
 *
 * @param message the informative message to display
 */
fun TextView.setLabelWithVisibility(message: String?) {
    with(this) {
        visibility = message?.let {
            text = message
            View.VISIBLE
        } ?: View.GONE
    }
}

/**
 * Extension function to handle rxJava disposal safely
 *
 * @return boolean object to state the result of safe dispose
 */
fun Disposable?.safeDispose() = if (this != null && !isDisposed) {
    dispose()
    true
} else false

abstract class SingleClickListener(private val minimumDelayInMillis: Long = 1500L) :
        View.OnClickListener {
    private var lastTimeClicked = 0L

    override fun onClick(view: View) {
        val currentTime = System.nanoTime()
        if (lastTimeClicked == 0L ||
                (currentTime - lastTimeClicked) > TimeUnit.MILLISECONDS.toNanos(minimumDelayInMillis)
        ) {
            lastTimeClicked = currentTime
            onSingleClick(view)
        }
    }

    protected abstract fun onSingleClick(view: View)
}

fun View.setSingleClickListener(onClick: (View) -> Unit) {
    setOnClickListener(object : SingleClickListener() {
        override fun onSingleClick(view: View) {
            onClick.invoke(view)
        }
    })
}

fun dpToPx(dp: Float): Float {
    return dp * Resources.getSystem().displayMetrics.density
}

fun NavController.navigateTo(currentNavId: Int, navId: Int, bundle: Bundle? = null,
                             navOptions: NavOptions? = null) {
    if (currentDestination?.id == currentNavId) {
        navigate(navId, bundle, navOptions)
    } else {
        // This can happen only when user tapped the current item twice or again,
        // in that case current destination id will be mismatched with nav id
    }
}

fun NavController.navigateTo(direction: NavDirections, navExtras: Navigator.Extras? = null) {
    with(this) {
        currentDestination?.getAction(direction.actionId)?.let {
            navExtras?.let { extras ->
                navigate(direction, extras)
            } ?: navigate(direction)
        }
    }
}
