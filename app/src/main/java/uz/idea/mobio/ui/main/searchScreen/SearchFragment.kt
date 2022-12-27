package uz.idea.mobio.ui.main.searchScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import uz.idea.mobio.R
import uz.idea.mobio.adapters.genericPagingAdapter.PagingAdapter
import uz.idea.mobio.databinding.FragmentSearchBinding
import uz.idea.mobio.databinding.ItemProductCategoryBinding
import uz.idea.mobio.databinding.ItemViewpagerBinding
import uz.idea.mobio.models.basketModel.addBasket.addBasketReq.AddBasketReq
import uz.idea.mobio.models.basketModel.addBasket.addBasketRes.AddBasketRes
import uz.idea.mobio.models.favoritesData.favoritesReq.FavoritesReq
import uz.idea.mobio.models.favoritesData.resSaveFavorite.ResSaveFavorite
import uz.idea.mobio.models.searchModel.DataX
import uz.idea.mobio.ui.auth.activity.AuthActivity
import uz.idea.mobio.ui.main.baseFragment.BaseFragment
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_BASKET
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_FAVORITES
import uz.idea.mobio.utils.appConstant.AppConstant.DEFAULT_CLICK
import uz.idea.mobio.utils.extension.*
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.basketVm.BasketViewModel
import uz.idea.mobio.vm.homeVm.HomeViewModel
import uz.idea.mobio.vm.searchVm.SearchViewModel
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    private val searchViewModel:SearchViewModel by viewModels()
    // basket viewModel
    private val basketViewModel:BasketViewModel by viewModels()
    // home viewModel
    private val homeViewModel:HomeViewModel by viewModels()
    private var countMotion = 0
    private val searchAdapter:PagingAdapter<DataX> by lazy {
        PagingAdapter(R.layout.item_product_category){ data, position, clickType,viewBinding ->
            when(clickType){
                DEFAULT_CLICK->{
                    activityMain.container?.screenNavigate?.createProductInfoScreen(data?.id?:0)
                }
                CLICK_BASKET->{
                    val addBasketReq = AddBasketReq(data?.id.toString())
                    addBasket(addBasketReq,viewBinding)
                }
                CLICK_FAVORITES->{
                    favoriteProduct(data?.id?:0,viewBinding)
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
                            if (clickType==2) activityMain.startActivity(Intent(activityMain, AuthActivity::class.java))
                            else if (clickType == 1) favoriteProduct(productID,viewBinding)
                            basketViewModel.clearErrorTable()
                        }
                    }
                }
            }
        }
    }
    private fun addBasket(addBasketReq: AddBasketReq,viewBinding:ViewBinding){
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
                            else if (clickType == 2) activityMain.startActivity(Intent(activityMain,AuthActivity::class.java))
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


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSearchBinding.inflate(inflater,container,false)

    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {
            binding.rvSearch.gone()
            lottie.visible()
            lottie.setAnimation(R.raw.search)
            swipeRefresh.setOnRefreshListener {
             if (search.text.toString().isNotEmptyOrNull()){
                 searchData(search.text.toString().trim())
             }else{
                 swipeRefresh.isRefreshing = false
                 binding.rvSearch.gone()
                 lottie.visible()
             }
            }
            swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(),R.color.circle_progress_color))
            search.addTextChangedListener {  text ->
                val searchText = text?.toString()?.trim()
                if (searchText.isNotEmptyOrNull()){
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (searchText.isNotEmptyOrNull()){
                            searchData(searchText.toString())
                            binding.rvSearch.visible()
                            lottie.gone()
                        }else{
                            binding.rvSearch.gone()
                            lottie.visible()
                        }
                    },1000)
                } else {
                    binding.rvSearch.gone()
                    lottie.visible()
                }
            }

            val layoutManager = GridLayoutManager(requireContext(),2)
            rvSearch.layoutManager = layoutManager
            rvSearch.addOnScrollListener(object:RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (searchAdapter.itemCount>8){
                        if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                            activityMain.bottomBarView(false)
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy < 0) {
                      activityMain.bottomBarView(true)
                    }
                }
            })

            rvSearch.adapter = searchAdapter
        }
    }

    private fun searchData(searchText:String){
        if (binding.search.text.toString().trim().isNotEmptyOrNull()){
            loadStat()
            binding.apply {
                lifecycleScope.launchWhenCreated {
                    searchViewModel.getSearchData(searchText).collect { pagingData->
                        searchAdapter.submitData(pagingData)
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }else{
            binding.rvSearch.gone()
            binding.lottie.visible()
        }
    }

    override fun start(savedInstanceState: Bundle?) {

    }

    private fun loadStat(){
        lifecycleScope.launchWhenStarted {
            searchAdapter.loadStateFlow.collectLatest { loadStates->
                binding.progress.isVisible = loadStates.refresh is LoadState.Loading
                binding.rvSearch.isVisible = loadStates.refresh !is LoadState.Loading
            }
        }

    }


}