package uz.idea.mobio.repository.databaseRepository.errorRepository

import uz.idea.mobio.database.daoAndEntity.error.ErrorDao
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity
import javax.inject.Inject

class ErrorRepository @Inject constructor(
    private val errorDao: ErrorDao
) {
    fun saveError(errorEntity: ErrorEntity) = errorDao.saveError(errorEntity)

    fun deleteTableError() = errorDao.removeErrorTable()

    fun getLiveError() = errorDao.getAllError()
}