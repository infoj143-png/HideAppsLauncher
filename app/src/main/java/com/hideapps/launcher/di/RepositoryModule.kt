package com.hideapps.launcher.di

import com.hideapps.launcher.data.repository.LauncherRepositoryImpl
import com.hideapps.launcher.domain.repository.LauncherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLauncherRepository(
        launcherRepositoryImpl: LauncherRepositoryImpl
    ): LauncherRepository
}
