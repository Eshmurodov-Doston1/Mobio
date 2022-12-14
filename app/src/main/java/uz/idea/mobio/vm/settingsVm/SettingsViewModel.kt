package uz.idea.mobio.vm.settingsVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.idea.mobio.repository.databaseRepository.errorRepository.ErrorRepository
import uz.idea.mobio.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.mobio.utils.appConstant.AppConstant.API
import uz.idea.mobio.utils.appConstant.AppConstant.EMPTY
import uz.idea.mobio.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.mobio.utils.appConstant.AppConstant.LOGOUT
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.networkHelper.NetworkHelper
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.utils.sharedPreferences.MySharedPreferences
import javax.inject.Inject

const val LOG_OUT_PATH = "/$API/$LOGOUT"
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val errorRepository: ErrorRepository,
    private val mySharedPreferences: MySharedPreferences
):ViewModel(){
    // log out
    val logOut:StateFlow<ResponseState<JsonElement?>> get() = _logOut
    private val _logOut = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun logOut() = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _logOut.emit(ResponseState.Loading)
            apiUsesCase.methodePost(LOG_OUT_PATH, EMPTY, EMPTY_MAP).collect { response->
                _logOut.emit(response)
            }
        }else{
            _logOut.emit(ResponseState.Error(NO_INTERNET))
        }
    }
    fun clearShared() = mySharedPreferences.clearToken()
    fun clearErrorTable() = errorRepository.deleteTableError()
}