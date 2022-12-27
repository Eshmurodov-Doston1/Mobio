package uz.idea.mobio.vm.mainVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.idea.mobio.models.userDataModel.UserDataModel
import uz.idea.mobio.repository.databaseRepository.errorRepository.ErrorRepository
import uz.idea.mobio.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.mobio.utils.appConstant.AppConstant.API
import uz.idea.mobio.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.appConstant.AppConstant.USER_DATA
import uz.idea.mobio.utils.networkHelper.NetworkHelper
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.utils.sharedPreferences.MySharedPreferences
import javax.inject.Inject

const val USER_DATA_PATH = "/$API/$USER_DATA"
@HiltViewModel
class MainViewModel @Inject constructor (
    private val mySharedPreferences: MySharedPreferences,
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val errorRepository: ErrorRepository
        ):ViewModel() {
            fun getMySharedPreferences() = mySharedPreferences

    // user data
    val userData:StateFlow<ResponseState<JsonElement?>> get() = _userData
    private val _userData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun getUserData() = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _userData.emit(ResponseState.Loading)
            apiUsesCase.methodeGet(USER_DATA_PATH, EMPTY_MAP).collect { response->
                _userData.emit(response)
            }
        }else{
            _userData.emit(ResponseState.Error(NO_INTERNET))
        }
    }

    fun getMyShared() = mySharedPreferences
    fun clearErrorTable() =errorRepository.deleteTableError()
}