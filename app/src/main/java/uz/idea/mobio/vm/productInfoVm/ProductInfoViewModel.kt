package uz.idea.mobio.vm.productInfoVm

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
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.appConstant.AppConstant.PRODUCT_ID_PATH
import uz.idea.mobio.utils.networkHelper.NetworkHelper
import uz.idea.mobio.utils.resPonseState.ResponseState
import javax.inject.Inject

const val PRODUCT_PATH ="/$API/$PRODUCT_ID_PATH"
@HiltViewModel
class ProductInfoViewModel  @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val errorRepository: ErrorRepository,
):ViewModel(){

    // get product data
    val productData:StateFlow<ResponseState<JsonElement?>> get() = _productData
    private val _productData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun getProductByID(productID:Int) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _productData.emit(ResponseState.Loading)
            val queryMap = HashMap<String,String>()
            queryMap["id"] = productID.toString()
            apiUsesCase.methodeGet(PRODUCT_PATH,queryMap).collect { response-> _productData.emit(response) }
        }else{
            _productData.emit(ResponseState.Error(NO_INTERNET))
        }
    }

    fun clearErrorTable() = errorRepository.deleteTableError()
}