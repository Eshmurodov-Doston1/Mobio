package uz.idea.mobio.ui.main.productInfoScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Comment
import uz.idea.mobio.R
import uz.idea.mobio.adapters.genericRvAdapter.GenericRvAdapter
import uz.idea.mobio.databinding.FragmentProductInfoBinding
import uz.idea.mobio.databinding.RateViewBinding
import uz.idea.mobio.models.basketModel.addBasket.addBasketReq.AddBasketReq
import uz.idea.mobio.models.basketModel.addBasket.addBasketRes.AddBasketRes
import uz.idea.mobio.models.comment.commentList.CommentList
import uz.idea.mobio.models.comment.commentList.Data
import uz.idea.mobio.models.comment.saveComment.SaveComment
import uz.idea.mobio.models.favoritesData.favoritesReq.FavoritesReq
import uz.idea.mobio.models.favoritesData.resSaveFavorite.ResSaveFavorite
import uz.idea.mobio.models.productModel.Photo
import uz.idea.mobio.models.productModel.ProductModel
import uz.idea.mobio.models.userDataModel.UserDataModel
import uz.idea.mobio.ui.auth.activity.AuthActivity
import uz.idea.mobio.ui.main.baseFragment.BaseFragment
import uz.idea.mobio.utils.appConstant.AppConstant.IMAGE_URL
import uz.idea.mobio.utils.appConstant.AppConstant.NO_AUTH_STATUS
import uz.idea.mobio.utils.appConstant.AppConstant.PRODUCT_ID
import uz.idea.mobio.utils.extension.*
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.basketVm.BasketViewModel
import uz.idea.mobio.vm.homeVm.HomeViewModel
import uz.idea.mobio.vm.mainVm.MainViewModel
import uz.idea.mobio.vm.productInfoVm.ProductInfoViewModel
import kotlin.math.abs

@AndroidEntryPoint
class ProductInfoFragment : BaseFragment<FragmentProductInfoBinding>() {
    private val productInfoViewModel:ProductInfoViewModel by viewModels()
    // basketViewModel
    private var countMotion = 0
    private val basketViewModel:BasketViewModel by viewModels()
    // homeViewModel
    private val homeViewModel:HomeViewModel by viewModels()
    // mainViewModel
    private val mainViewModel:MainViewModel by viewModels()

    private var productID:Int = 0
    private val adapterViewPager:GenericRvAdapter<Photo> by lazy {
        GenericRvAdapter(R.layout.item_viewpager_product){data, position, clickType,viewBinding ->

        }
    }
    // comment adapter
    private val commentAdapter:GenericRvAdapter<Data> by lazy {
        GenericRvAdapter(R.layout.item_comment){data, position, clickType,viewBinding ->

        }
    }
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentProductInfoBinding.inflate(inflater,container,false)

    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {
            activityMain.toolbarView(true)
            // portrait screen
            activityMain.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            bacBtn.setOnClickListener {
                findNavController().popBackStack()
            }
            productInfoViewModel.getProductByID(productID)
            lifecycleScope.launchWhenCreated {
                productInfoViewModel.productData.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            progress.visible()
                        }
                        is ResponseState.Success->{
                            progress.gone()
                            val productModel = result.data?.parseClass(ProductModel::class.java)
                            adapterViewPager.submitList(productModel?.data?.photos)
                            viewPager2.adapter = adapterViewPager
                            springDotsIndicator.attachTo(viewPager2)
                            binding.title.text = productModel?.data?.name
                            val price = productModel?.data?.price
                            val oldPrice = productModel?.data?.old_price
                            if (price!=null){
                                binding.price.text = productModel.data.price.numberFormatter() + " ${getString(R.string.pay_type)}"
                            } else {
                              binding.linearPrice.gone()
                            }
                            if (oldPrice!=null){
                                binding.oldPrice.text = productModel.data.old_price.numberFormatter() + " ${getString(R.string.pay_type)}"
                            } else {
                                binding.linearOldPrice.gone()
                            }
                            if ((oldPrice?:0.0) > (price?:0.0)){
                                val percent = 100 - (((price?:0.0)/(oldPrice?:0.0))*100)
                                binding.percent.text = "${String.format("%.2f", percent)} %"
                            } else {
                                binding.linearPercent.gone()
                                binding.linearOldPrice.gone()
                            }
                            if (productModel?.data?.description.isNotEmptyOrNull()) {
                                binding.infoText.text = productModel?.data?.description
                            }else {
                                binding.infoText.gone()
                            }

                            binding.lockBtn.setOnClickListener {
                                val addBasketReq = AddBasketReq(productModel?.data?.id.toString())
                                addBasket(addBasketReq)
                            }

                            binding.lickBtn.setOnClickListener {
                                favoriteProduct(productModel?.data?.id?:0,binding)
                            }
                            // share click
                            binding.shareBtn.setOnClickListener {
                                val intent = Intent().apply {
                                    if (productModel?.data?.photos?.isNotEmpty() == true){
                                        this.action = Intent.ACTION_SEND
                                        this.putExtra(Intent.EXTRA_TEXT,"${IMAGE_URL}/${productModel.data.photos[0].id}/${productModel.data.photos[0].file_name}"   )
                                        this.type = "text/plain"
                                    }
                                }
                                activityMain.startActivity(intent)
                            }

                            binding.imageSend.setOnClickListener {
                                val comment = binding.editeTextComment.text.toString().trim()
                                if (comment.isNotEmptyOrNull()){
                                    rateMethode(comment,productModel)
                                } else {
                                    activityMain.motionAnimation("error",getString(R.string.no_comment))
                                }
                            }
                            buyBtn.setOnClickListener {
                                activityMain.container?.screenNavigate?.createPurchaseScreen()
                            }
                        }
                        is ResponseState.Error->{
                            progress.gone()
                            activityMain.container?.errorDialog(result.errorCode,result.liveError,activityMain.mainViewModel.getMySharedPreferences()){
                                if (it==1) findNavController().popBackStack()
                                productInfoViewModel.clearErrorTable()
                            }
                        }
                    }
                }
            }
            cardComment.setOnClickListener {
                if (commentEdit.isVisible) commentEdit.gone()
                else commentEdit.visible()
            }
            commentEdit.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus)
                    nested.scrollBy(0, 150)
            }
            // comment
           commentData()
        }
    }


    override fun onDestroyView() {
        activityMain.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        super.onDestroyView()
    }

    private fun rateMethode(comment:String,productModel: ProductModel?){
        val alertDialog = AlertDialog.Builder(requireContext())
        val create = alertDialog.create()
        val bindingRate = RateViewBinding.inflate(activityMain.layoutInflater)
        bindingRate.apply {
            cancel.setOnClickListener {
                create.dismiss()
            }
            var ratingData = ""
            ratingBar.onRateChanged = { rate->
                ratingData =  String.format("%.2f", rate)
            }

            okBtn.setOnClickListener {
                userData(comment,ratingData,productModel,bindingRate,create)
            }
           //  saveComment()
        }
        create.setView(bindingRate.root)
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        create.show()
    }


    private fun userData(comment: String,ratingData:String,productModel: ProductModel?,bindingRate:RateViewBinding,create:AlertDialog){
        binding.apply {
            mainViewModel.getUserData()
            lifecycleScope.launchWhenCreated {
                mainViewModel.userData.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            bindingRate.progress.visible()
                        }
                        is ResponseState.Success->{
                            bindingRate.progress.gone()
                            val userDataModel = result.data?.parseClass(UserDataModel::class.java)
                            val saveComment = SaveComment(comment,userDataModel?.user?.email,productModel?.data?.id.toString(),"1",userDataModel?.user?.name.toString(),ratingData.toDouble())

                            saveCommentData(saveComment,bindingRate,create)
                        }
                        is ResponseState.Error->{
                            bindingRate.progress.gone()
                            activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                                if (clickType==1) userData(comment,ratingData,productModel,bindingRate,create)
                                else if (clickType==2) {
                                    val intent = Intent(activityMain,AuthActivity::class.java)
                                    intent.putExtra(NO_AUTH_STATUS,2)
                                    activityMain.startActivity(intent)
                                }
                                mainViewModel.clearErrorTable()
                            }
                        }
                    }
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
                        if (viewBinding is FragmentProductInfoBinding){
                            viewBinding.progressProduct.visible()
                            viewBinding.lickIcon.gone()
                        }
                    }
                    is ResponseState.Success->{
                        if (viewBinding is FragmentProductInfoBinding){
                            viewBinding.progressProduct.gone()
                            viewBinding.lickIcon.visible()
                        }
                        val resSaveFavorite = result.data?.parseClass(ResSaveFavorite::class.java)
                        activityMain.motionAnimation("success",resSaveFavorite?.message)
                    }
                    is ResponseState.Error->{
                        if (viewBinding is FragmentProductInfoBinding){
                            viewBinding.progressProduct.gone()
                            viewBinding.lickIcon.visible()
                        }
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==2){
                                val intent = Intent(activityMain,AuthActivity::class.java)
                                intent.putExtra(NO_AUTH_STATUS,2)
                                activityMain.startActivity(intent)
                            }
                            else if (clickType == 1) favoriteProduct(productID,viewBinding)
                            basketViewModel.clearErrorTable()
                        }
                    }
                }
            }
        }
    }

    private fun commentData(){
        binding.apply {
            productInfoViewModel.getProductComment(productID)
            lifecycleScope.launchWhenCreated {
                productInfoViewModel.productComment.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            progressComment.visible()
                        }
                        is ResponseState.Success->{
                            LogData(result.data.toString())
                            val fromJson = Gson().fromJson(result.data?.asJsonObject, CommentList::class.java)
                            LogData( fromJson.toString())
                            val comment = result.data?.parseClass(CommentList::class.java)
                            commentAdapter.submitList(comment?.data)
                            rvComment.adapter = commentAdapter
                            progressComment.gone()
                        }
                        is ResponseState.Error->{
                            activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                                if (clickType==1) commentData()
                                basketViewModel.clearErrorTable()
                            }
                            progressComment.gone()
                        }
                    }
                }
            }
        }
    }


    private fun saveCommentData(saveComment: SaveComment,bindingRate: RateViewBinding,create: AlertDialog){
        productInfoViewModel.saveComment(saveComment)
        lifecycleScope.launchWhenCreated {
            productInfoViewModel.saveComment.collect { result->
                when(result){
                    is ResponseState.Loading->{
                        bindingRate.progress.visible()
                    }
                    is ResponseState.Success->{
                        LogData(result.data.toString())
                        bindingRate.progress.gone()
                        create.dismiss()
                        commentData()
                    }
                    is ResponseState.Error->{
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==1) saveCommentData(saveComment,bindingRate,create)
                            basketViewModel.clearErrorTable()
                        }
                        bindingRate.progress.gone()
                    }
                }
            }
        }
    }

    // addBasket
    private fun addBasket(addBasketReq: AddBasketReq){
        countMotion = 0
        basketViewModel.saveBasket(addBasketReq)
        lifecycleScope.launchWhenCreated {
            basketViewModel.addBasket.collect { result->
                when(result){
                    is ResponseState.Success->{
                        binding.progressImage.gone()
                        binding.lockIcon.visible()
                        val addBasketRes = result.data?.parseClass(AddBasketRes::class.java)
                        countMotion++
                        if (countMotion==1){
                            activityMain.motionAnimation(addBasketRes?.status,addBasketRes?.message)
                        }
                    }
                    is ResponseState.Error->{
                        binding.progressImage.gone()
                        binding.lockIcon.visible()
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==1) addBasket(addBasketReq)
                            else if (clickType == 2) {
                                val intent = Intent(activityMain,AuthActivity::class.java)
                                intent.putExtra(NO_AUTH_STATUS,2)
                                activityMain.startActivity(intent)
                            }
                            basketViewModel.clearErrorTable()
                        }
                    }
                    else->{
                        binding.progressImage.visible()
                        binding.lockIcon.gone()
                    }
                }
            }
        }
    }

    override fun start(savedInstanceState: Bundle?) {
       productID = arguments?.getInt(PRODUCT_ID,0)?:0
    }

    override fun onDestroy() {
        super.onDestroy()
        activityMain.toolbarView(false)
    }

}