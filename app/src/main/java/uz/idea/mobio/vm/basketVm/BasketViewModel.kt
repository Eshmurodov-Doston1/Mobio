package uz.idea.mobio.vm.basketVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.idea.mobio.models.basketModel.addBasket.addBasketReq.AddBasketReq
import uz.idea.mobio.models.basketModel.basketList.BasketModel
import uz.idea.mobio.models.basketModel.deleteBasket.reqDeleteBasket.DeleteReqModel
import uz.idea.mobio.models.basketModel.udateBasket.UpdateBasketModel
import uz.idea.mobio.network.pagingService.PagingService
import uz.idea.mobio.repository.apiServiceRepository.pagingRepository.createPager
import uz.idea.mobio.repository.databaseRepository.errorRepository.ErrorRepository
import uz.idea.mobio.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.mobio.utils.appConstant.AppConstant.ADD_BASKET
import uz.idea.mobio.utils.appConstant.AppConstant.API
import uz.idea.mobio.utils.appConstant.AppConstant.BASKET_LIST_PATH
import uz.idea.mobio.utils.appConstant.AppConstant.DEFAULT_PAGE_SIZE
import uz.idea.mobio.utils.appConstant.AppConstant.DELETE_BASKET
import uz.idea.mobio.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.appConstant.AppConstant.UPDATE_BASKET
import uz.idea.mobio.utils.extension.parseClass
import uz.idea.mobio.utils.extension.queryCateGoryMap
import uz.idea.mobio.utils.networkHelper.NetworkHelper
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.utils.sharedPreferences.MySharedPreferences
import javax.inject.Inject

const val BASKET_PATH = "/$API/$BASKET_LIST_PATH"
const val UPDATE_BASKET_PATH = "/$API/$UPDATE_BASKET"
const val DELETE_BASKET_PATH = "/$API/$DELETE_BASKET"
const val ADD_BASKET_PATH = "/$API/$ADD_BASKET"
@HiltViewModel
class BasketViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val pagingService: PagingService,
    private val apiUsesCase: ApiUsesCase,
    private val errorRepository: ErrorRepository,
    val mySharedPreferences: MySharedPreferences
):ViewModel() {
    // get basket
    fun getBasketList() = createPager {  page->
        val response = pagingService.methodeGet(BASKET_PATH, queryCateGoryMap(page, DEFAULT_PAGE_SIZE))
        return@createPager if (response.isSuccessful){
            val basketModel = response.body()?.parseClass(BasketModel::class.java)
            basketModel?.data?.data?:emptyList()
        }else {
            emptyList()
        }
    }.flow

    // update basket
    val updateBasket:StateFlow<ResponseState<JsonElement?>> get() = _updateBasket
    private val _updateBasket = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun updateBasket(updateBasketModel: UpdateBasketModel) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _updateBasket.emit(ResponseState.Loading)
            apiUsesCase.methodePost(UPDATE_BASKET_PATH,updateBasketModel, EMPTY_MAP).collect { response->
                _updateBasket.emit(response)
            }
        }else{
            _updateBasket.emit(ResponseState.Error(NO_INTERNET))
        }
    }

     // update basket
    val deleteBasket:StateFlow<ResponseState<JsonElement?>> get() = _deleteBasket
    private val _deleteBasket = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun deleteBasket(deleteReqModel: DeleteReqModel) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _deleteBasket.emit(ResponseState.Loading)
            apiUsesCase.methodePost(DELETE_BASKET_PATH,deleteReqModel, EMPTY_MAP).collect { response->
                _deleteBasket.emit(response)
            }
        }else{
            _deleteBasket.emit(ResponseState.Error(NO_INTERNET))
        }
    }


    // save  productBasket

    val addBasket:StateFlow<ResponseState<JsonElement?>> get() = _addBasket
    private val _addBasket = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun saveBasket(addBasketReq: AddBasketReq) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _addBasket.emit(ResponseState.Loading)
            apiUsesCase.methodePost(ADD_BASKET_PATH,addBasketReq, EMPTY_MAP).collect { response->
                _addBasket.emit(response)
            }
        }else{
            _addBasket.emit(ResponseState.Error(NO_INTERNET))
        }
    }


    fun clearErrorTable() = errorRepository.deleteTableError()
}