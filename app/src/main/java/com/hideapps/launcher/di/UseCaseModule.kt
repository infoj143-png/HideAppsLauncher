package com.hideapps.launcher.di

import com.hideapps.launcher.domain.repository.AppsRepository
import com.hideapps.launcher.domain.repository.LauncherRepository
import com.hideapps.launcher.domain.usecase.ClearPinUseCase
import com.hideapps.launcher.domain.usecase.GetInstalledAppsUseCase
import com.hideapps.launcher.domain.usecase.IsPinSetupUseCase
import com.hideapps.launcher.domain.usecase.SavePinUseCase
import com.hideapps.launcher.domain.usecase.VerifyPinUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideIsPinSetupUseCase(repository: LauncherRepository): IsPinSetupUseCase {
        return IsPinSetupUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSavePinUseCase(repository: LauncherRepository): SavePinUseCase {
        return SavePinUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideVerifyPinUseCase(repository: LauncherRepository): VerifyPinUseCase {
        return VerifyPinUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideClearPinUseCase(repository: LauncherRepository): ClearPinUseCase {
        return ClearPinUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetInstalledAppsUseCase(repository: AppsRepository): GetInstalledAppsUseCase {
        return GetInstalledAppsUseCase(repository)
    }
}
