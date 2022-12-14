package uz.idea.mobio.di.repositoryModel

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import uz.idea.mobio.repository.apiServiceRepository.apiRepository.ApiRepository
import uz.idea.mobio.repository.apiServiceRepository.apiRepositoryImpl.ApiRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModel {
    @Binds
    fun providesApiRepository(apiRepositoryImpl: ApiRepositoryImpl):ApiRepository
}