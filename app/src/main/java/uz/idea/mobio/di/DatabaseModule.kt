package uz.idea.mobio.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.idea.mobio.database.AppDatabase
import uz.idea.mobio.database.daoAndEntity.error.ErrorDao
import uz.idea.mobio.utils.appConstant.AppConstant.DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context,AppDatabase::class.java,DATABASE_NAME)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    @Singleton
    fun providesErrorDao(appDatabase: AppDatabase):ErrorDao = appDatabase.errorDao()
}