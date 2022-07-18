/* (c) Helios Software Developer. All rights reserved. */
package com.heliossoftwaredeveloper.ticketscanner.common.di

import com.heliossoftwaredeveloper.ticketscanner.common.utils.BaseSchedulerProvider
import com.heliossoftwaredeveloper.ticketscanner.common.utils.SchedulerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {
    @Provides
    @Singleton
    fun providesSchedulerSource(): BaseSchedulerProvider =
        SchedulerProvider.getInstance()
}