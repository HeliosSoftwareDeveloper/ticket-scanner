package com.heliossoftwaredeveloper.ticketscanner.common.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.heliossoftwaredeveloper.ticketscanner.common.viewmodel.BaseViewModel
import com.heliossoftwaredeveloper.ticketscanner.common.viewmodel.ViewModelFactory
import java.lang.reflect.ParameterizedType
import javax.inject.Inject

/**
 * Automatically initializes ViewDataBinding class and ViewModel class for your activity.
 */
abstract class BaseViewModelActivity<B : ViewDataBinding, VM : BaseViewModel> : BaseActivity<B>() {

    @Inject
    lateinit var factory: ViewModelFactory<VM>

    lateinit var viewModel: VM

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gets the class type passed in VM parameter.
        // https://stackoverflow.com/a/52073780/5285687
        val viewModelClass = (javaClass.genericSuperclass as ParameterizedType)
                .actualTypeArguments[1] as Class<VM>

        viewModel = ViewModelProvider(this, factory).get(viewModelClass)
    }
}
