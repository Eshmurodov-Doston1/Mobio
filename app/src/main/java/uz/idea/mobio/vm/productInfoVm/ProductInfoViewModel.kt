package uz.idea.mobio.vm.productInfoVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.idea.mobio.models.comment.saveComment.SaveComment
import uz.idea.mobio.repository.databaseRepository.errorRepository.ErrorRepository
import uz.idea.mobio.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.mobio.utils.appConstant.AppConstant.API
import uz.idea.mobio.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.appConstant.AppConstant.PRODUCT_COMMENT_SAVE_PATH
import uz.idea.mobio.utils.appConstant.AppConstant.PRODUCT_ID_PATH
import uz.idea.mobio.utils.networkHelper.NetworkHelper
import uz.idea.mobio.utils.resPonseState.ResponseState
import javax.inject.Inject

const val PRODUCT_PATH ="/$API/$PRODUCT_ID_PATH"
const val PRODUCT_SAVE_COMMENT_PATH_API ="/$API/$PRODUCT_COMMENT_SAVE_PATH"
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

    // get product data
    val productComment:StateFlow<ResponseState<JsonElement?>> get() = _productComment
    private val _productComment = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun getProductComment(productId:Int) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "/$API/comment/$productId"
            _productComment.emit(ResponseState.Loading)
            apiUsesCase.methodeGet(url, EMPTY_MAP).collect { response->
                _productComment.emit(response)
            }
        } else {
            _productComment.emit(ResponseState.Error(NO_INTERNET))
        }
    }

    // get save comment
    val saveComment:StateFlow<ResponseState<JsonElement?>> get() = _saveComment
    private val _saveComment = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun saveComment(saveComment: SaveComment) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _saveComment.emit(ResponseState.Loading)
            apiUsesCase.methodePost(PRODUCT_SAVE_COMMENT_PATH_API, saveComment,EMPTY_MAP).collect { response->
                _saveComment.emit(response)
            }
        } else {
            _saveComment.emit(ResponseState.Error(NO_INTERNET))
        }
    }

    // save comment
    val commentData:StateFlow<ResponseState<JsonElement?>> get() = _commentData
    private val _commentData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)
}