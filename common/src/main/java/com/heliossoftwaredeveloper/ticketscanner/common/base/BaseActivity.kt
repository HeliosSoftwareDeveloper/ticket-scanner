package com.heliossoftwaredeveloper.ticketscanner.common.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.heliossoftwaredeveloper.ticketscanner.common.utils.BaseSchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Automatically initializes ViewDataBinding class for your activity.
 */
abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {

    lateinit var binding: B

    protected val disposables: CompositeDisposable = CompositeDisposable()

    @Inject
    lateinit var scheduler: BaseSchedulerProvider

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            getLayoutId()
        )
        binding.lifecycleOwner = this
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (canBack()) {
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * @return true if should use back button on toolbar
     */
    protected open fun canBack(): Boolean {
        return false
    }

    fun setToolbarHomeIndicatorIcon(@DrawableRes iconRes: Int) {
        supportActionBar?.setHomeAsUpIndicator(iconRes)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    fun enableToolbarHomeIndicator() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    fun disableToolbarBackButton() {
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun showDialog(
        isCancellable: Boolean = true,
        title: String = "",
        message: String,
        primaryBtnText: String = "",
        secondaryBtnText: String = "",
        primaryAction: ((DialogInterface) -> Unit)? = null,
        secondaryAction: ((DialogInterface) -> Unit)? = null,
        cancelAction: ((DialogInterface) -> Unit)? = null,
    ) {
        MaterialAlertDialogBuilder(this)
            .apply {
                setMessage(message)
                setCancelable(isCancellable)
                setOnCancelListener(cancelAction)
                if (title.isNotBlank()) {
                    setTitle(title)
                }
                if (primaryBtnText.isNotBlank()) {
                    setPositiveButton(primaryBtnText) { dialog, _ ->
                        primaryAction?.invoke(dialog)
                    }
                }

                if (secondaryBtnText.isNotBlank()) {
                    setNegativeButton(secondaryBtnText) { dialog, _ ->
                        secondaryAction?.invoke(dialog)
                    }
                }
            }
            .show()
    }
}
