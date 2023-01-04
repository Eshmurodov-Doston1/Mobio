package uz.idea.mobio.ui.main.categoryProductsScreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import uz.idea.mobio.R
import uz.idea.mobio.adapters.genericPagingAdapter.PagingAdapter
import uz.idea.mobio.databinding.FragmentCategoryProductBinding
import uz.idea.mobio.databinding.FragmentHomeBinding
import uz.idea.mobio.databinding.ItemChildProductCategoryBinding
import uz.idea.mobio.databinding.ItemProductCategoryBinding
import uz.idea.mobio.models.basketModel.addBasket.addBasketReq.AddBasketReq
import uz.idea.mobio.models.basketModel.addBasket.addBasketRes.AddBasketRes
import uz.idea.mobio.models.categoryProductModel.CategoryProductModel
import uz.idea.mobio.models.categoryProductModel.DataX
import uz.idea.mobio.models.favoritesData.favoritesReq.FavoritesReq
import uz.idea.mobio.models.favoritesData.resSaveFavorite.ResSaveFavorite
import uz.idea.mobio.ui.auth.activity.AuthActivity
import uz.idea.mobio.ui.main.baseFragment.BaseFragment
import uz.idea.mobio.utils.appConstant.AppConstant
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_BASKET
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_FAVORITES
import uz.idea.mobio.utils.appConstant.AppConstant.DEFAULT_CLICK
import uz.idea.mobio.utils.appConstant.AppConstant.PRODUCT_ID
import uz.idea.mobio.utils.appConstant.AppConstant.TITLE_NAME
import uz.idea.mobio.utils.extension.LogData
import uz.idea.mobio.utils.extension.gone
import uz.idea.mobio.utils.extension.parseClass
import uz.idea.mobio.utils.extension.visible
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.basketVm.BasketViewModel
import uz.idea.mobio.vm.categoryVm.CategoryViewModel
import uz.idea.mobio.vm.homeVm.HomeViewModel

@AndroidEntryPoint
class CategoryProductFragment : BaseFragment<FragmentCategoryProductBinding>() {
  private val categoryViewModel:CategoryViewModel by viewModels()
    // basketViewModel
    private var countMotion = 0
  private val basketViewModel:BasketViewModel by viewModels()
    // homeViewModel
  private val homeViewModel:HomeViewModel by viewModels()
    private val categoryProductPagingAdapter:PagingAdapter<DataX> by lazy {
        PagingAdapter(R.layout.item_product_category){ data, position, clickType ,viewBinding->
            when(clickType){
                CLICK_FAVORITES->{
                    favoriteProduct(data?.productss?.id?:0,viewBinding)
                }
                CLICK_BASKET->{
                    val addBasketReq = AddBasketReq(data?.productss?.id.toString())
                    addBasket(addBasketReq,viewBinding)
                }
                DEFAULT_CLICK->{
                    activityMain.container?.screenNavigate?.createProductInfoScreen(data?.productss?.id?:0)
                }
            }
        }
    }


    private fun favoriteProduct(productID:Int,viewBinding:ViewBinding){
        val favoritesReq = FavoritesReq(product_id = productID.toString())
        homeViewModel.saveFavorite(favoritesReq)
        lifecycleScope.launchWhenCreated {
            homeViewModel.favoritesData.collect { result->
                when(result){
                    is ResponseState.Loading->{
                        if (viewBinding is ItemProductCategoryBinding){ viewBinding.loadingCons.visible() }
                    }
                    is ResponseState.Success->{
                        if (viewBinding is ItemProductCategoryBinding){ viewBinding.loadingCons.gone() }
                        val resSaveFavorite = result.data?.parseClass(ResSaveFavorite::class.java)
                        activityMain.motionAnimation("success",resSaveFavorite?.message)
                    }
                    is ResponseState.Error->{
                        if (viewBinding is ItemProductCategoryBinding){ viewBinding.loadingCons.gone() }
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==2) {
                                val intent = Intent(activityMain,AuthActivity::class.java)
                                intent.putExtra(AppConstant.NO_AUTH_STATUS,2)
                                activityMain.startActivity(intent)
                            }
                            else if (clickType==1) favoriteProduct(productID, viewBinding)
                            basketViewModel.clearErrorTable()
                        }
                    }
                }
            }
        }
    }


    // addBasket
    private fun addBasket(addBasketReq: AddBasketReq,viewBinding: ViewBinding){
        countMotion = 0
        basketViewModel.saveBasket(addBasketReq)
        lifecycleScope.launchWhenCreated {
            basketViewModel.addBasket.collect { result->
                when(result){
                    is ResponseState.Success->{
                        if (viewBinding is ItemProductCategoryBinding){
                            viewBinding.loadingCons.gone()
                            val addBasketRes = result.data?.parseClass(AddBasketRes::class.java)
                            countMotion++
                            if (countMotion==1){
                                activityMain.motionAnimation(addBasketRes?.status,addBasketRes?.message)
                            }
                        }
                    }
                    is ResponseState.Error->{
                        if (viewBinding is ItemProductCategoryBinding){ viewBinding.loadingCons.gone() }
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==1) addBasket(addBasketReq,viewBinding)
                            else if (clickType == 2) {
                                val intent = Intent(activityMain,AuthActivity::class.java)
                                intent.putExtra(AppConstant.NO_AUTH_STATUS,2)
                                activityMain.startActivity(intent)
                            }
                            basketViewModel.clearErrorTable()
                        }
                    }
                    else->{
                        if (viewBinding is ItemProductCategoryBinding){
                            viewBinding.loadingCons.visible()
                        }
                    }
                }
            }
        }
    }

    private var categoryId:Int = 0
    private var titleName:String = ""
    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {
            loadStat()
            activityMain.supportActionBar?.title = titleName
            pagingData()
            swipeRefresh.setOnRefreshListener {
                pagingData()
                loadStat()
            }
            rvProduct.adapter = categoryProductPagingAdapter

            swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(),R.color.circle_progress_color))
        }
    }

    private fun pagingData(){
        lifecycleScope.launchWhenCreated {
            categoryViewModel.getCategoryProducts(categoryId).collect { pagingData->
                categoryProductPagingAdapter.submitData(pagingData)
            }
        }
    }

    override fun start(savedInstanceState: Bundle?) {
        arguments?.let {
           categoryId = it.getInt(PRODUCT_ID,0)
           titleName = it.getString(TITLE_NAME,"")
        }
    }

    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentCategoryProductBinding.inflate(inflater,container,false)

    private fun loadStat(){
        lifecycleScope.launchWhenStarted {
            categoryProductPagingAdapter.loadStateFlow.collectLatest { loadStates->
                binding.progress.isVisible = loadStates.refresh is LoadState.Loading
                binding.rvProduct.isVisible = loadStates.refresh !is LoadState.Loading
                binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }

    }

}