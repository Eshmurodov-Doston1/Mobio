package uz.idea.mobio.vm.searchVm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.idea.mobio.models.searchModel.SearchModel
import uz.idea.mobio.network.pagingService.PagingService
import uz.idea.mobio.repository.apiServiceRepository.pagingRepository.createPager
import uz.idea.mobio.repository.databaseRepository.errorRepository.ErrorRepository
import uz.idea.mobio.utils.appConstant.AppConstant.API
import uz.idea.mobio.utils.appConstant.AppConstant.DEFAULT_PAGE_SIZE
import uz.idea.mobio.utils.extension.isNotEmptyOrNull
import uz.idea.mobio.utils.extension.parseClass
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val pagingService: PagingService,
    private val errorRepository: ErrorRepository
):ViewModel() {

    fun getSearchData(searchText: String) = createPager {  page->
        val urlSearch = "/$API/product"
        val queryMap = HashMap<String,String>()
        if (searchText.isNotEmptyOrNull()){
            queryMap["search"] = searchText
        }
        queryMap["page"] = page.toString()
        queryMap["count"] = DEFAULT_PAGE_SIZE.toString()
        val response = pagingService.methodeGet(urlSearch, queryMap)
        return@createPager if (response.isSuccessful) {
            response.body()?.parseClass(SearchModel::class.java)?.data?.data?: emptyList()
        } else {
            emptyList()
        }
    }.flow


    fun clearErrorTable() = errorRepository.deleteTableError()
}