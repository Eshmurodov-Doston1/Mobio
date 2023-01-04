package uz.idea.mobio.ui.main.homeScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.muddassir.connection_checker.ConnectionChecker
import com.muddassir.connection_checker.ConnectionState
import com.muddassir.connection_checker.ConnectivityListener
import com.muddassir.connection_checker.checkConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import uz.idea.mobio.BuildConfig
import uz.idea.mobio.R
import uz.idea.mobio.adapters.genericPagingAdapter.PagingAdapter
import uz.idea.mobio.adapters.genericRvAdapter.GenericRvAdapter
import uz.idea.mobio.adapters.mainChildAdapter.MainChildProductAdapter
import uz.idea.mobio.adapters.mainProductAdapter.MainProductAdapter
import uz.idea.mobio.databinding.FragmentHomeBinding
import uz.idea.mobio.databinding.ItemChildProductCategoryBinding
import uz.idea.mobio.databinding.ItemProductCategoryBinding
import uz.idea.mobio.databinding.ItemViewpagerBinding
import uz.idea.mobio.models.basketModel.addBasket.addBasketReq.AddBasketReq
import uz.idea.mobio.models.basketModel.addBasket.addBasketRes.AddBasketRes
import uz.idea.mobio.models.categoryModel.Data
import uz.idea.mobio.models.favoritesData.favoritesReq.FavoritesReq
import uz.idea.mobio.models.favoritesData.resSaveFavorite.ResSaveFavorite
import uz.idea.mobio.models.mainChildModel.MainChildModel
import uz.idea.mobio.models.mainProduct.MainProductModel
import uz.idea.mobio.models.mainProduct.Product
import uz.idea.mobio.models.newProductModel.DataX
import uz.idea.mobio.models.newProductModel.NewProduct
import uz.idea.mobio.ui.auth.activity.AuthActivity
import uz.idea.mobio.ui.main.baseFragment.BaseFragment
import uz.idea.mobio.utils.appConstant.AppConstant
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_FAVORITES
import uz.idea.mobio.utils.appConstant.AppConstant.DEFAULT_CLICK
import uz.idea.mobio.utils.extension.*
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.basketVm.BasketViewModel
import uz.idea.mobio.vm.homeVm.HomeViewModel
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val homeViewModel:HomeViewModel by viewModels()
    private var countMotion = 0
    // basket viewModel
    private val basketViewModel:BasketViewModel by viewModels()
    var handler = Handler(Looper.getMainLooper())

    private var listProduct = ArrayList<uz.idea.mobio.models.mainProduct.Data>()

    //slide runnable
    private val slideRunnable:Runnable = Runnable {
        try {
            binding.viewPager2.currentItem = binding.viewPager2.currentItem+1
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    //category adapter
    private val categoryPagingAdapter:PagingAdapter<Data> by lazy {
        PagingAdapter(R.layout.item_category_main){ data, _, _,viewBinding ->
            activityMain.container?.screenNavigate?.createCategoryProduct(data?.id?:0,data?.name.toString())
        }
    }

    // new product adapter
    private val newProductAdapter:GenericRvAdapter<DataX> by lazy {
        GenericRvAdapter(R.layout.item_viewpager){ data, position, clickType,viewBinding ->
            when(clickType){
                DEFAULT_CLICK->{
                    activityMain.container?.screenNavigate?.createProductInfoScreen(data.id)
                }
                CLICK_FAVORITES->{
                    favoriteProduct(data.id,viewBinding)
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
                        if (viewBinding is ItemViewpagerBinding){ viewBinding.linearProgress.visible() }
                    }
                    is ResponseState.Success->{
                        if (viewBinding is ItemViewpagerBinding){ viewBinding.linearProgress.gone() }
                        val resSaveFavorite = result.data?.parseClass(ResSaveFavorite::class.java)
                        activityMain.motionAnimation("success",resSaveFavorite?.message)
                    }
                    is ResponseState.Error->{
                        if (viewBinding is ItemViewpagerBinding){ viewBinding.linearProgress.gone() }
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==1) favoriteProduct(productID,viewBinding) else
                                if (clickType == 2) {
                                    val intent = Intent(activityMain,AuthActivity::class.java)
                                    intent.putExtra(AppConstant.NO_AUTH_STATUS,2)
                                    activityMain.startActivity(intent)
                                }
                            basketViewModel.clearErrorTable()
                        }
                    }
                }
            }
        }
    }

    // main product adapter
    private val mainProductAdapter: MainProductAdapter by lazy {
        MainProductAdapter(object: MainProductAdapter.OnItemClickListener{
            override fun onItemClick(data: uz.idea.mobio.models.mainProduct.Data, position: Int,viewBinding: ViewBinding) {
                activityMain.container?.screenNavigate?.createCategoryProduct(data.id,data.title)
            }

            override fun onItemChildClick(product: Product, position: Int,viewBinding: ViewBinding) {
                activityMain.container?.screenNavigate?.createProductInfoScreen(product.productsss!![0].id)
            }

            override fun onItemChildClickFavorites(product: Product, position: Int,viewBinding: ViewBinding) {
                favoriteProduct((product.productsss ?: emptyList())[0].id,viewBinding)
            }

            override fun onItemChildClickBasket(product: Product, position: Int,viewBinding: ViewBinding) {
                val addBasketReq = AddBasketReq(product.productsss!![0].id.toString())
                addBasket(addBasketReq,viewBinding)
            }
        })
    }

    // addBasket
    private fun addBasket(addBasketReq: AddBasketReq,viewBinding: ViewBinding){
        countMotion = 0
        basketViewModel.saveBasket(addBasketReq)
        lifecycleScope.launchWhenCreated {
            basketViewModel.addBasket.collect { result->
                when(result){
                    is ResponseState.Success->{
                        if (viewBinding is ItemChildProductCategoryBinding){
                            viewBinding.loadingCons.gone()
                            val addBasketRes = result.data?.parseClass(AddBasketRes::class.java)
                            countMotion++
                            if (countMotion==1){
                                activityMain.motionAnimation(addBasketRes?.status,addBasketRes?.message)
                            }
                        }
                    }
                    is ResponseState.Error->{
                        if (viewBinding is ItemChildProductCategoryBinding){ viewBinding.loadingCons.gone() }
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==1) addBasket(addBasketReq,viewBinding)
                            if (clickType == 2){
                                val intent = Intent(activityMain,AuthActivity::class.java)
                                intent.putExtra(AppConstant.NO_AUTH_STATUS,2)
                                activityMain.startActivity(intent)
                            }
                            basketViewModel.clearErrorTable()
                        }
                    }
                    else->{
                        if (viewBinding is ItemChildProductCategoryBinding){
                            viewBinding.loadingCons.visible()
                        }
                    }
                }
            }
        }
    }

    /// val mainChild data
    private val mainChildProductAdapter: MainChildProductAdapter by lazy {
        MainChildProductAdapter(object: MainChildProductAdapter.OnItemClickListener{
            override fun onItemClick(
                data: uz.idea.mobio.models.mainChildModel.Data,
                position: Int
            ) {
                activityMain.container?.screenNavigate?.createCategoryProduct(data.id,data.title)
            }

            override fun onItemChildClick(
                product: uz.idea.mobio.models.mainChildModel.Product,
                position: Int
            ) {
                activityMain.container?.screenNavigate?.createProductInfoScreen(product.productsss!![0].id)
            }
        })
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater,container,false)

    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {
            if (listProduct.isEmpty()){
                lifecycleScope.launchWhenStarted {
                    coroutineScope {
                        // category methode
                        async {getCategory()}.await()
                        //pager methode
                        async {pagerMethode()}.await()
                        //main data
                        async {getMainData()}.await()
                    }
                }
                // internet connection
                val connectionChecker = ConnectionChecker(viewLifecycleOwner)
                connectionChecker.connectivityListener = object: ConnectivityListener {
                    override fun onConnectionState(state: ConnectionState) {
                        // Your logic goes here
                        when (state) {
                            ConnectionState.CONNECTED -> {
                                binding.includeShimmer.shimmerCons.gone()
                                binding.swipeRefresh.visible()

                            }
                            ConnectionState.SLOW -> {
                            if (listProduct.isEmpty()) loadState(true)
                                else loadState(false)
                            }
                            else -> {
                                if (listProduct.isEmpty()) loadState(true)
                                else loadState(false)
                            }
                        }
                    }
                }

                linear.setOnClickListener {
                    activityMain.container?.screenNavigate?.createCategoryScreen()
                }

                includeShimmer.nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if (scrollY > oldScrollY) {
                        activityMain.bottomBarView(false)
                    }
                    if (scrollY < oldScrollY) {
                        activityMain.bottomBarView(true)
                    }
                })

                // nestedScroll
                nestedScrollHome.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if (scrollY>oldScrollY){
                        activityMain.scrollFabVisibleOrGone(true)
                    }
                    if (scrollY==0){
                        activityMain.scrollFabVisibleOrGone(false)
                    }
                    if (scrollY < oldScrollY) {
                        if (scrollY!=0){
                            activityMain.scrollFabVisibleOrGone(true)
                        }
                        activityMain.bottomBarView(true)
                    }
                    if (scrollY >= (abs(v.measuredHeight - nestedScrollHome.getChildAt(0).measuredHeight))) {
                        activityMain.bottomBarView(false)
                    }
                }

                // fab icon tint
                binding.fabScroll.setColorFilter(ContextCompat.getColor(requireContext(),R.color.white))

                fabScroll.setOnClickListener { nestedScrollHome.smoothScrollTo(0,0) }


                swipeRefresh.setOnRefreshListener {
                    loadState(true)
                    lifecycleScope.launchWhenStarted {
                        coroutineScope {
                            // category methode
                            async {getCategory()}.await()
                            //pager methode
                            async {pagerMethode()}.await()
                            //main data
                            async {getMainData()}.await()
                        }
                    }
                   // synchronized(requireActivity()) {

                    //}
                }

                // swipe refresh color
                swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(),R.color.app_background))
            }
        }
    }

    // category data
    private fun getCategory(){
        loadState(false)
        lifecycleScope.launchWhenStarted {
            homeViewModel.getCategory().collect { pagingData->
                categoryPagingAdapter.submitData(pagingData)
            }
        }
        binding.categoryRv.adapter = categoryPagingAdapter
    }

    // pager Methode
    private fun pagerMethode(){
        binding.apply {
            //new product
            homeViewModel.getNewProduct()
            lifecycleScope.launchWhenStarted {
                homeViewModel.newProduct.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            loadState(true)
                        }
                        is ResponseState.Success->{
                            swipeRefresh.isRefreshing = false
                            val newProduct = result.data?.parseClass(NewProduct::class.java)
                            newProductAdapter.submitList(newProduct?.data?.data)
                            // runtime load
                            viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                                override fun onPageSelected(position: Int) {
                                    super.onPageSelected(position)
                                    // TODO: try catch ViewPager position

                                    lifecycleScope.launch(Dispatchers.IO) {
                                        handler.removeCallbacks(slideRunnable)
                                        handler.postDelayed(slideRunnable,3000)

                                        if (position == (newProduct?.data?.data?.size?:0)-1){
                                            handler.postDelayed({
                                                try {
                                                    binding.viewPager2.setCurrentItem(0,false)
                                                }catch (e:Exception){
                                                    e.printStackTrace()
                                                }
                                            },3000)
                                        }
                                    }
                                }
                            })
                        }
                        is ResponseState.Error->{
                            swipeRefresh.isRefreshing = false
                            activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                                if (clickType==1) pagerMethode()
                                homeViewModel.clearErrorTable()
                            }
                        }
                    }
                }
            }
            viewPager2.adapter = newProductAdapter
            springDotsIndicator.attachTo(viewPager2)
            viewPager2.setPageTransformer { page, position ->
                if (position < -1) {    // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.alpha = 0F
                } else if (position <= 0) {    // [-1,0]
                    page.alpha = 1F
                    page.pivotX = page.width.toFloat()
                    page.rotationY = -90 * kotlin.math.abs(position)
                } else if (position <= 1) {    // (0,1]
                    page.alpha = 1F
                    page.pivotX = 0F
                    page.rotationY = 90 * kotlin.math.abs(position)
                } else {    // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.alpha = 0F
                }
            }

        }
    }



   // get main data methode
    private fun getMainData(){
        homeViewModel.getMainProducts()
        lifecycleScope.launchWhenStarted {
           homeViewModel.mainProduct.collect { result->
               when(result){
                   is ResponseState.Success->{
                       val data = result.data[0]?.parseClass(MainProductModel::class.java)?.data
                       listProduct.clear()
                       listProduct.addAll(data?: emptyList())
                       mainProductAdapter.submitList(data)
                       binding.rvCategoryProduct.adapter = mainProductAdapter
                       binding.rvCategoryProduct.setItemViewCacheSize(data?.size?:0)

                       val dataChild = result.data[1]?.parseClass(MainChildModel::class.java)
                       mainChildProductAdapter.submitList(dataChild?.data)
                       binding.rvCategoryProduct1.adapter = mainChildProductAdapter
                       binding.rvCategoryProduct1.setItemViewCacheSize(dataChild?.data?.size?:0)
                       loadState(false)
                   }
                   is ResponseState.Error->{
                       binding.swipeRefresh.isRefreshing = false
                       activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                           if (clickType==1) getMainData()
                           homeViewModel.clearErrorTable()
                       }
                       loadState(false)
                   }
                   is ResponseState.Loading->{
                       loadState(true)
                   }
               }
           }
        }
    }

    override fun start(savedInstanceState: Bundle?) {

    }

    private fun loadState(isLoading:Boolean){
        lifecycleScope.launch {
            binding.includeShimmer.shimmerCons.isVisible = isLoading
            if(!isLoading) binding.swipeRefresh.visible()
            else  binding.swipeRefresh.gone()
        }
    }

}