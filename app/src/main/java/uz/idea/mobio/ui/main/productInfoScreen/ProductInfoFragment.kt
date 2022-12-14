package uz.idea.mobio.ui.main.productInfoScreen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.idea.mobio.R
import uz.idea.mobio.adapters.genericRvAdapter.GenericRvAdapter
import uz.idea.mobio.databinding.FragmentProductInfoBinding
import uz.idea.mobio.databinding.ItemProductCategoryBinding
import uz.idea.mobio.models.basketModel.addBasket.addBasketReq.AddBasketReq
import uz.idea.mobio.models.basketModel.addBasket.addBasketRes.AddBasketRes
import uz.idea.mobio.models.locale.LocaleCommentModel
import uz.idea.mobio.models.productModel.Photo
import uz.idea.mobio.models.productModel.ProductModel
import uz.idea.mobio.ui.main.activity.MainActivity
import uz.idea.mobio.ui.main.baseFragment.BaseFragment
import uz.idea.mobio.utils.appConstant.AppConstant.PRODUCT_ID
import uz.idea.mobio.utils.extension.*
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.basketVm.BasketViewModel
import uz.idea.mobio.vm.productInfoVm.ProductInfoViewModel

@AndroidEntryPoint
class ProductInfoFragment : BaseFragment<FragmentProductInfoBinding>() {
    private val productInfoViewModel:ProductInfoViewModel by viewModels()
    // basketViewModel
    private var countMotion = 0
    private val basketViewModel:BasketViewModel by viewModels()
    private var productID:Int = 0
    private val adapterViewPager:GenericRvAdapter<Photo> by lazy {
        GenericRvAdapter(R.layout.item_viewpager_product){data, position, clickType,viewBinding ->

        }
    }
    // comment adapter
    private val commentAdapter:GenericRvAdapter<LocaleCommentModel> by lazy {
        GenericRvAdapter(R.layout.item_comment){data, position, clickType,viewBinding ->

        }
    }
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentProductInfoBinding.inflate(inflater,container,false)

    @SuppressLint("SetTextI18n")
    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {
            activityMain.toolbarView(true)
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

                            commentAdapter.submitList(LocaleCommentModel().getCommentList())
                            rvComment.adapter = commentAdapter

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
                        binding.progressImage.visible()
                        binding.lockIcon.gone()
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==1) addBasket(addBasketReq)
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