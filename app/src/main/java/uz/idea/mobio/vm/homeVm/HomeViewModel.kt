package uz.idea.mobio.vm.homeVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity
import uz.idea.mobio.models.categoryModel.CategoryModel
import uz.idea.mobio.models.favoritesData.favoritesReq.FavoritesReq
import uz.idea.mobio.models.mainChildModel.MainChildModel
import uz.idea.mobio.models.mainProduct.MainProductModel
import uz.idea.mobio.network.pagingService.PagingService
import uz.idea.mobio.repository.apiServiceRepository.pagingRepository.createPager
import uz.idea.mobio.repository.databaseRepository.errorRepository.ErrorRepository
import uz.idea.mobio.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.mobio.utils.appConstant.AppConstant.API
import uz.idea.mobio.utils.appConstant.AppConstant.CATEGORY
import uz.idea.mobio.utils.appConstant.AppConstant.DEFAULT_PAGE_SIZE
import uz.idea.mobio.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.mobio.utils.appConstant.AppConstant.FAVORITE_ADD
import uz.idea.mobio.utils.appConstant.AppConstant.MAIN_PRODUCT_CHILD_PATH
import uz.idea.mobio.utils.appConstant.AppConstant.MAIN_PRODUCT_PATH
import uz.idea.mobio.utils.appConstant.AppConstant.NEW_PRODUCT
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.extension.LogData
import uz.idea.mobio.utils.extension.parseClass
import uz.idea.mobio.utils.extension.queryCateGoryMap
import uz.idea.mobio.utils.extension.queryNewMap
import uz.idea.mobio.utils.networkHelper.NetworkHelper
import uz.idea.mobio.utils.resPonseState.ResponseState
import java.net.SocketTimeoutException
import javax.inject.Inject

const val PATH_CATEGORY = "/$API/$CATEGORY"
const val PATH_NEW_PRODUCT = "/$API/$NEW_PRODUCT"
const val PATH_MAIN_PRODUCT = "/$API/$MAIN_PRODUCT_PATH"
const val PATH_MAIN_CHILD = "/$API/$MAIN_PRODUCT_CHILD_PATH"
const val FAVORITE_ADD_PATH = "/$API/$FAVORITE_ADD"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val pagingService: PagingService,
    private val errorRepository: ErrorRepository,
    private val apiUsesCase: ApiUsesCase
):ViewModel() {

    // get category paging
    fun getCategory() = createPager { page->
        val response = pagingService.methodeGet(PATH_CATEGORY, queryCateGoryMap(page, DEFAULT_PAGE_SIZE))
        return@createPager if (response.isSuccessful) {
            response.body()?.parseClass(CategoryModel::class.java)?.category?.data ?: emptyList()
        } else {
            emptyList()
        }
    }.flow

    // get mainData paging
    val mainProduct:StateFlow<ResponseState<List<JsonElement?>>> get() = _mainProduct
    private val _mainProduct = MutableStateFlow<ResponseState<List<JsonElement?>>>(ResponseState.Loading)

    fun getMainProducts() = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            try {
                _mainProduct.emit(ResponseState.Loading)
                apiUsesCase.methodeMainData(PATH_MAIN_PRODUCT, EMPTY_MAP, PATH_MAIN_CHILD, EMPTY_MAP).collect {  response->
                    _mainProduct.emit(response)
                }
            } catch (e:SocketTimeoutException){
                errorRepository.saveError(ErrorEntity(error = e.message, errorCode = e.hashCode()))
                _mainProduct.emit(ResponseState.Error(e.hashCode(),errorRepository.getLiveError()))
            }
        }else{
            _mainProduct.emit(ResponseState.Error(NO_INTERNET))
        }
    }

    // new product
    val newProduct:StateFlow<ResponseState<JsonElement?>> get() = _newProduct
    private val _newProduct = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun getNewProduct() = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            try {
                _newProduct.emit(ResponseState.Loading)
                apiUsesCase.methodeGet(PATH_NEW_PRODUCT, queryNewMap(6)).collect { response->
                    _newProduct.emit(response)
                }
            }catch (e:SocketTimeoutException){
                errorRepository.saveError(ErrorEntity(error = e.message, errorCode = e.hashCode()))
                _mainProduct.emit(ResponseState.Error(e.hashCode(),errorRepository.getLiveError()))
            }
        }else{
            _newProduct.emit(ResponseState.Error(NO_INTERNET))
        }
    }

    // save favorites
    val favoritesData:StateFlow<ResponseState<JsonElement?>> get() = _favoritesData
    private val _favoritesData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun saveFavorite(favoritesReq: FavoritesReq) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            try {
                _favoritesData.emit(ResponseState.Loading)
                apiUsesCase.methodePost(FAVORITE_ADD_PATH, favoritesReq,queryNewMap(6)).collect { response->
                    _favoritesData.emit(response)
                }
            }catch (e:SocketTimeoutException){
                errorRepository.saveError(ErrorEntity(error = e.message, errorCode = e.hashCode()))
                _mainProduct.emit(ResponseState.Error(e.hashCode(),errorRepository.getLiveError()))
            }
        }else{
            _favoritesData.emit(ResponseState.Error(NO_INTERNET))
        }
    }

    // clear errorTable
    fun clearErrorTable() = errorRepository.deleteTableError()
}