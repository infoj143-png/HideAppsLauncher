package com.hideapps.launcher.di

import android.content.Context
import androidx.room.Room
import com.hideapps.launcher.data.local.AppSettingsDao
import com.hideapps.launcher.data.local.LauncherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLauncherDatabase(
        @ApplicationContext context: Context
    ): LauncherDatabase {
        return Room.databaseBuilder(
            context,
            LauncherDatabase::class.java,
            LauncherDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAppSettingsDao(database: LauncherDatabase): AppSettingsDao {
        return database.appSettingsDao
    }
}
