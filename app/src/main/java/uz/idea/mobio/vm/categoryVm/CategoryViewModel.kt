package uz.idea.mobio.vm.categoryVm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.idea.mobio.models.categoryProductModel.CategoryProductModel
import uz.idea.mobio.network.pagingService.PagingService
import uz.idea.mobio.repository.apiServiceRepository.pagingRepository.createPager
import uz.idea.mobio.utils.appConstant.AppConstant.API
import uz.idea.mobio.utils.appConstant.AppConstant.CATEGORY_PRODUCT_PATH
import uz.idea.mobio.utils.appConstant.AppConstant.DEFAULT_PAGE_SIZE
import uz.idea.mobio.utils.extension.parseClass
import uz.idea.mobio.utils.extension.queryCateGoryMap
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel  @Inject constructor(
    private val pagingService: PagingService
):ViewModel(){
    fun getCategoryProducts(productId:Int) = createPager { page->
        val url = "/$API/$CATEGORY_PRODUCT_PATH/$productId"
        val response = pagingService.methodeGet(url, queryCateGoryMap(page, DEFAULT_PAGE_SIZE))
        return@createPager if (response.isSuccessful)
            response.body()?.parseClass(CategoryProductModel::class.java)?.data?.data ?: emptyList()
        else emptyList()
    }.flow
}