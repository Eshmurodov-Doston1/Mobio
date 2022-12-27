package uz.idea.mobio.vm.authVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.idea.mobio.models.auth.AuthReq
import uz.idea.mobio.models.auth.ResAuth
import uz.idea.mobio.models.register.RegisterReq
import uz.idea.mobio.repository.databaseRepository.errorRepository.ErrorRepository
import uz.idea.mobio.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.mobio.utils.appConstant.AppConstant.API
import uz.idea.mobio.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.mobio.utils.appConstant.AppConstant.LOGIN
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.appConstant.AppConstant.REGISTER
import uz.idea.mobio.utils.extension.LogData
import uz.idea.mobio.utils.networkHelper.NetworkHelper
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.utils.sharedPreferences.MySharedPreferences
import javax.inject.Inject

const val AUTH_PATH = "/$API/$LOGIN"
const val REGISTER_PATH = "/$API/$REGISTER"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val errorRepository: ErrorRepository,
    private val mySharedPreferences: MySharedPreferences
):ViewModel() {

    val mysharedPref:MySharedPreferences get() = mySharedPreferences

    fun saveData(password:String,phone:String){
        mySharedPreferences.phone = phone
        mySharedPreferences.password = password
    }

    val authData:StateFlow<ResponseState<JsonElement?>> get() = _authData
    private val _authData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun authMethode(authReq:AuthReq) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _authData.emit(ResponseState.Loading)
            apiUsesCase.methodePost(AUTH_PATH,authReq, EMPTY_MAP).collect { response->
                _authData.emit(response)
            }
        } else {
            _authData.emit(ResponseState.Error(errorCode = NO_INTERNET))
        }
    }


    val registerData:StateFlow<ResponseState<JsonElement?>> get() = _registerData
    private val _registerData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun registerUser(registerReq: RegisterReq) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _registerData.emit(ResponseState.Loading)
            apiUsesCase.methodePost(REGISTER_PATH,registerReq, EMPTY_MAP).collect { response->
                LogData(response.toString())
                _registerData.emit(response)
            }
        } else {
            _registerData.emit(ResponseState.Error(errorCode = NO_INTERNET))
        }
    }


    fun clearErrorTable() = errorRepository.deleteTableError()
    fun getMyShared() = mySharedPreferences
    fun saveSharedPref(resAuth: ResAuth?){
        mySharedPreferences.accessToken = resAuth?.access_token
        mySharedPreferences.refreshToken = resAuth?.refresh_token
        mySharedPreferences.tokenType = resAuth?.token_type
    }
}